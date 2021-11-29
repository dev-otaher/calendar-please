package com.example.calendarplease;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class NewCalendarActivity extends AppCompatActivity {

    private FirstStepFragment firstStepFragment;
    private SchoolCalendar schoolCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar);

        schoolCalendar = new SchoolCalendar();

        firstStepFragment = FirstStepFragment.newInstance(schoolCalendar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, firstStepFragment).commit();

        ImageButton buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (currentFragment.getClass() == FirstStepFragment.class) {
                if (schoolCalendar.getSyllabusDocumentList().size() > 0 && schoolCalendar.getSyllabusDocumentList() != null) {
                    SecondStepFragment secondStepFragment = SecondStepFragment.newInstance(schoolCalendar.getSyllabusDocumentList());
                    FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                    fragmentTransaction2.replace(R.id.fragment_container, secondStepFragment).commit();
                } else {
                    Snackbar.make(this, v, getString(R.string.syllabus_required_msg), BaseTransientBottomBar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show();
                }

            } else if (currentFragment.getClass() == SecondStepFragment.class) {
                List<VEvent> events = schoolCalendar.generateEvents();
                List<VEvent> weekTagEvents = schoolCalendar.generateWeekTagEvents();

                ICalendar iCalendar = new ICalendar();
                for (VEvent e : events) {
                    iCalendar.addEvent(e);
                }
                for (VEvent e : weekTagEvents) {
                    iCalendar.addEvent(e);
                }
                FileOutputStream fileOutputStream = null;
                try {
                    String calendarName = schoolCalendar.getTitle();
                    String filename = (!TextUtils.isEmpty(calendarName)) ? calendarName : "Calendar" + Calendar.getInstance().getTimeInMillis();
                    fileOutputStream = openFileOutput(filename + ".ics", MODE_PRIVATE);
                    Biweekly.write(iCalendar).go(fileOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finish();

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FirstStepFragment.BROWSE_DOC_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    String filePath = data.getData().getPath();
                    if (!isPathExist(filePath)) {
                        SyllabusDocument syllabusDocument = new SyllabusDocument();
                        syllabusDocument.setPath(filePath);
                        syllabusDocument.setUri(data.getData());
                        syllabusDocument.setContentResolver(getContentResolver());
                        schoolCalendar.getSyllabusDocumentList().add(syllabusDocument);
                        int size = schoolCalendar.getSyllabusDocumentList().size();
                        firstStepFragment.getRecyclerView().getAdapter().notifyItemInserted(size);
                        firstStepFragment.getRecyclerView().smoothScrollToPosition(size);
                    }
                } else {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            String filePath = item.getUri().getPath();
                            if (!isPathExist(filePath)) {
                                SyllabusDocument syllabusDocument = new SyllabusDocument();
                                syllabusDocument.setPath(filePath);
                                syllabusDocument.setUri(item.getUri());
                                syllabusDocument.setContentResolver(getContentResolver());
                                schoolCalendar.getSyllabusDocumentList().add(syllabusDocument);
                                int size = schoolCalendar.getSyllabusDocumentList().size();
                                firstStepFragment.getRecyclerView().getAdapter().notifyItemInserted(size);
                                firstStepFragment.getRecyclerView().smoothScrollToPosition(size);
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isPathExist(String filePath) {
        if (schoolCalendar.getSyllabusDocumentList() != null) {
            for (SyllabusDocument doc :
                    schoolCalendar.getSyllabusDocumentList()) {
                if (doc.getPath().equals(filePath))
                    return true;
            }
        }
        return false;
    }
}











