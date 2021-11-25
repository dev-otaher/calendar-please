package com.example.calendarplease;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
            SecondStepFragment secondStepFragment = SecondStepFragment.newInstance(schoolCalendar.getSyllabusDocumentList());
            FragmentManager fragmentManager2 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
            fragmentTransaction2.replace(R.id.fragment_container, secondStepFragment).commit();

//            if (this.uploadedFilesPathList.size() != 0) {
//                List<VEvent> schoolEvents = new ArrayList<>();
//                for (ParcelFileDescriptor p : fileDescriptors) {
//                    try {
//                        FileInputStream fileInputStream = new FileInputStream(p.getFileDescriptor());
//                        XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
//                        XWPFTable weekCourseOutlineTable = getWeekCourseOutlineTable(xwpfDocument.getTables());
//                        fileInputStream.close();
//
//                        if (weekCourseOutlineTable == null)
//                            throw new NullPointerException("NO TABLE FOUND!");
//
//                        weekCourseOutlineTable.removeRow(0);
//                        schoolEvents.addAll(getSchoolEvents(weekCourseOutlineTable, new int[]{4, 5}));
//                    } catch (IOException e) {
//                        Log.d("meow", e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//                ICalendar iCalendar = new ICalendar();
//                for (VEvent e : schoolEvents) {
//                    iCalendar.addEvent(e);
//                }
//                FileOutputStream fileOutputStream = null;
//                try {
//                    String calendarName = mCalendarName.getText().toString();
//                    String filename = (!calendarName.equals("")) ? calendarName : String.valueOf(schoolStartDate.getTimeInMillis());
//                    fileOutputStream = openFileOutput(filename + ".ics", MODE_PRIVATE);
//                    Biweekly.write(iCalendar).go(fileOutputStream);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FirstStepFragment.BROWSE_DOC_REQUEST) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getData().getPath();
                if (!isPathExist(filePath)) {
                    SyllabusDocument syllabusDocument = new SyllabusDocument();
                    syllabusDocument.setPath(filePath);
                    syllabusDocument.uri = data.getData();
                    schoolCalendar.getSyllabusDocumentList().add(syllabusDocument);
                    int size = schoolCalendar.getSyllabusDocumentList().size();
                    firstStepFragment.getRecyclerView().getAdapter().notifyItemInserted(size);
                    firstStepFragment.getRecyclerView().smoothScrollToPosition(size);

//                    try {
//                        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
//                        Log.d("meow2", "================" + getContentResolver().toString());
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
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

    private XWPFTable getWeekCourseOutlineTable(List<XWPFTable> tables) {
        XWPFTable weekCourseOutlineTable = null;
        for (XWPFTable table : tables) {
            XWPFTableRow headerRow = table.getRows().get(0);
            XWPFTableCell firstCell = headerRow.getCell(0);
            XWPFTableCell secondCell = headerRow.getCell(1);
            if (firstCell.getText().trim().toLowerCase().contains("week")
                    && secondCell.getText().trim().toLowerCase().contains("date")) {
                weekCourseOutlineTable = table;
                break;
            }
        }
        return weekCourseOutlineTable;
    }

    private List<VEvent> getSchoolEvents(XWPFTable table, int[] columnsIndex) {
        List<VEvent> events = new ArrayList<>();

//        int weekNumber = 1;
//        for (XWPFTableRow row : table.getRows()) {
//            XWPFTableCell weekCell = row.getCell(0);
//
//            // Redundant WeekTag when bulk upload :"(
//            VEvent weekTag = null;
//            if (weekCell != null && !weekCell.getText().equals("")) {
//                weekNumber = Integer.parseInt(weekCell.getText());
//                weekTag = new VEvent();
//                weekTag.setSummary("Week#" + weekNumber);
//            }
//
//            Calendar rowStartDate = (Calendar) schoolStartDate.clone();
//            int currentDayOfWeek = rowStartDate.get(Calendar.DAY_OF_WEEK);
//            if (weekNumber != 1)
//                rowStartDate.add(Calendar.DAY_OF_MONTH, ((7 * (weekNumber - 1))) - (currentDayOfWeek - 1));
//
//            Calendar rowEndDate = (Calendar) rowStartDate.clone();
//            currentDayOfWeek = rowEndDate.get(Calendar.DAY_OF_WEEK);
//            rowEndDate.add(Calendar.DAY_OF_WEEK, (5 - (currentDayOfWeek - 1)));
//
//            if (weekTag != null) {
//                weekTag.setDateStart(rowStartDate.getTime(), false);
//                weekTag.setDateEnd(rowEndDate.getTime(), false);
//                events.add(weekTag);
//            }
//
//            for (int i : columnsIndex) {
//                XWPFTableCell currentCell = row.getCell(i);
//                if (currentCell != null) {
//                    String title = row.getCell(i).getText();
//                    if (!title.equals("")) {
//                        VEvent event = new VEvent();
//                        event.setSummary(title);
//                        event.setDateStart(rowStartDate.getTime(), false);
//                        event.setDateEnd(rowEndDate.getTime(), false);
//                        events.add(event);
//                    }
//                }
//            }
//        }
        return events;
    }


}











