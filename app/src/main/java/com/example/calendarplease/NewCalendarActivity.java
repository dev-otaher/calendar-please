package com.example.calendarplease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class NewCalendarActivity extends AppCompatActivity {

    private EditText mStartDate;
    private RecyclerView mRecyclerView;
    private FileListAdapter fileListAdapter;

    private Calendar schoolStartDate;

    private List<String> uploadedFilesPathList = new ArrayList<>();
    private List<ParcelFileDescriptor> fileDescriptors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar);

        mRecyclerView = findViewById(R.id.recyclerview);
        fileListAdapter = new FileListAdapter(this, this.uploadedFilesPathList);
        mRecyclerView.setAdapter(fileListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                schoolStartDate = new Calendar.Builder().setDate(year, month, dayOfMonth).build();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/y", Locale.getDefault());
                mStartDate.setText(simpleDateFormat.format(schoolStartDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ImageButton buttonRun = findViewById(R.id.button_run);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 512 - Artificial Intelligence.docx");
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 513 - Mobile Computing.docx");
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CIS 517 - Data Mining.docx");
//                    FileInputStream fileInputStream = new FileInputStream(getFilesDir().toString() + "/CS 422 - Language Theory & Finite Automata.docx");
//                    FileInputStream fileInputStream = new FileInputStream("storage/emulated/0/Download/CIS 517 - Data Mining.docx");
//                    XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
//                    XWPFTable weekCourseOutlineTable = getWeekCourseOutlineTable(xwpfDocument.getTables());
//                    fileInputStream.close();
//
//                    if (weekCourseOutlineTable == null)
//                        throw new NullPointerException("NO TABLE FOUND!");
//
//                    weekCourseOutlineTable.removeRow(0);
//                    List<VEvent> schoolEvents = getSchoolEvents(weekCourseOutlineTable, new int[]{4, 5});
//                    ICalendar iCalendar = new ICalendar();
//                    for (VEvent e : schoolEvents) {
//                        iCalendar.addEvent(e);
//                    }
//                    FileOutputStream fileOutputStream = openFileOutput("mycalendar.ics", MODE_PRIVATE);
//                    Biweekly.write(iCalendar).go(fileOutputStream);
                Intent intent = new Intent();
                intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 200);
//                } catch (IOException e) {
//                    Log.d("meow", e.getMessage());
//                    e.printStackTrace();
//                }
            }
        });

        ImageButton buttonPickDate = findViewById(R.id.button_show_date_dialog);
        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        ImageButton buttonAddFile = findViewById(R.id.button_add_file_path);
        buttonAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadedFilesPathList.add("");
                int size = uploadedFilesPathList.size();
                mRecyclerView.getAdapter().notifyItemInserted(size);
                mRecyclerView.smoothScrollToPosition(size);
            }
        });


        mStartDate = findViewById(R.id.editText_start_date);
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

            // Redundant WeekTag when bulk upload :"(
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("meow", "invoked");
        Log.d("meow", "ResultCode = " + resultCode);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                    FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
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

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

            }
        }
        if (requestCode == FileListAdapter.DOC_REQUEST) {
            if (resultCode == RESULT_OK) {
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                    fileDescriptors.add(parcelFileDescriptor);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}