package com.example.calendarplease;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Name;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnFragmentInteractionListener {

    private Button buttonRun;
    private Button buttonShowDatePicker;
    private TextView textViewSchoolDate;

    private DatePickerFragment datePickerFragment;

    private Calendar schoolStartDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datePickerFragment = new DatePickerFragment();
        textViewSchoolDate = findViewById(R.id.text_view_school_date);

        Log.d("meow", getFilesDir().toString());

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        buttonRun = findViewById(R.id.button_run);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 512 - Artificial Intelligence.docx");
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 513 - Mobile Computing.docx");
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CIS 517 - Data Mining.docx");
                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 422 - Language Theory & Finite Automata.docx");
                    XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
                    XWPFTable weekCourseOutlineTable = getWeekCourseOutlineTable(xwpfDocument.getTables());
                    fileInputStream.close();

                    if (weekCourseOutlineTable == null)
                        throw new NullPointerException("NO TABLE FOUND!");

                    weekCourseOutlineTable.removeRow(0);
                    List<VEvent> schoolEvents = getSchoolEvents(weekCourseOutlineTable, new int[]{4, 5});
                    ICalendar iCalendar = new ICalendar();
                    for (VEvent e : schoolEvents) {
                        iCalendar.addEvent(e);
                    }
                    FileOutputStream fileOutputStream = openFileOutput("mycalendar.ics", MODE_PRIVATE);
                    Biweekly.write(iCalendar).go(fileOutputStream);
                } catch (IOException e) {
                    Log.d("meow", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        buttonShowDatePicker = findViewById(R.id.button_date_picker);
        buttonShowDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    @Override
    public void onDateSelect(int year, int month, int dayOfMonth) {
        schoolStartDate = new Calendar.Builder().setDate(year, month, dayOfMonth).build();
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

        int weekNumber = 1;
        for (XWPFTableRow row : table.getRows()) {
            XWPFTableCell weekCell = row.getCell(0);
            VEvent weekTag = null;
            if (weekCell != null && !weekCell.getText().equals("")) {
                weekNumber = Integer.parseInt(weekCell.getText());
                weekTag = new VEvent();
                weekTag.setSummary("Week#" + weekNumber);
            }

            Calendar rowStartDate = (Calendar) schoolStartDate.clone();
            int currentDayOfWeek = rowStartDate.get(Calendar.DAY_OF_WEEK);
            if (weekNumber != 1)
                rowStartDate.add(Calendar.DAY_OF_MONTH, ((7 * (weekNumber - 1))) - (currentDayOfWeek - 1));

            Calendar rowEndDate = (Calendar) rowStartDate.clone();
            currentDayOfWeek = rowEndDate.get(Calendar.DAY_OF_WEEK);
            rowEndDate.add(Calendar.DAY_OF_WEEK, (5 - (currentDayOfWeek - 1)));

            if (weekTag != null) {
                weekTag.setDateStart(rowStartDate.getTime(), false);
                weekTag.setDateEnd(rowEndDate.getTime(), false);
                events.add(weekTag);
            }

            for (int i : columnsIndex) {
                XWPFTableCell currentCell = row.getCell(i);
                if (currentCell != null) {
                    String title = row.getCell(i).getText();
                    if (!title.equals("")) {
                        VEvent event = new VEvent();
                        event.setSummary(title);
                        event.setDateStart(rowStartDate.getTime(), false);
                        event.setDateEnd(rowEndDate.getTime(), false);
                        events.add(event);
                    }
                }
            }
        }
        return events;
    }
}