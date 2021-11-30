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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        Log.d("meow", getFilesDir().toString());

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        dbHelper = new DbHelper(this);
        calendarNameList = getCalendarNames();
        adapter = new CalendarListAdapter(this, calendarNameList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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