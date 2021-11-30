package com.example.calendarplease;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewNoData;
    TextView textViewNoData;
    RecyclerView recyclerView;

    DbHelper dbHelper;
    List<String> calendarNameList;
    CalendarListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewNoData = findViewById(R.id.imageview_no_data);
        textViewNoData = findViewById(R.id.textview_no_data);
        recyclerView = findViewById(R.id.recyclerview_calendars);
        registerForContextMenu(recyclerView);

        Log.d("meow", getFilesDir().toString());

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        dbHelper = new DbHelper(this);
        adapter = new CalendarListAdapter(this, getCalendarNames());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null) {
            adapter.mCalendarNameList = getCalendarNames();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_new_calendar) {
            showAddCalendarActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 10) {
            String calendarName = ((TextView) recyclerView.findViewHolderForAdapterPosition(item.getOrder())
                    .itemView.findViewById(R.id.textView_calendar_name))
                    .getText().toString();

            try {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                FileInputStream inputStream = new FileInputStream(getFilesDir().toString() + "/" + calendarName + ".ics");
                File outputFile = new File("/storage/self/primary/Download/" + calendarName + ".ics");
                Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Snackbar.make(this, findViewById(R.id.recyclerview_calendars), "File copied successfully!", BaseTransientBottomBar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(this, findViewById(R.id.recyclerview_calendars), "Failed to copy file!", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    private void showAddCalendarActivity() {
        Intent intent = new Intent(this, NewCalendarActivity.class);
        startActivity(intent);
    }

    public List<String> getCalendarNames() {
        Cursor cursor = dbHelper.fetchNames();
        List<String> calendarNameList = new ArrayList<>();
        if (cursor.getCount() == 0) {
            imageViewNoData.setVisibility(View.VISIBLE);
            textViewNoData.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                calendarNameList.add(cursor.getString(0));
            }
            imageViewNoData.setVisibility(View.GONE);
            textViewNoData.setVisibility(View.GONE);
        }
        return calendarNameList;
    }
}