package com.example.calendarplease;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class NewCalendarActivity extends AppCompatActivity {

    private static final int BROWSE_DOC_REQUEST = 100;
    private final List<String> uploadedFilesPathList = new ArrayList<>();
    private final List<ParcelFileDescriptor> fileDescriptors = new ArrayList<>();
    private EditText mCalendarName;
    private EditText mStartDate;
    private Calendar schoolStartDate;
    private RecyclerView mRecyclerView;
    private FileListAdapter fileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar);

        schoolStartDate = Calendar.getInstance();

        mCalendarName = findViewById(R.id.editText_calendar_name);
        mCalendarName.setText(getResources().getString(R.string.cal_name_placeholder, schoolStartDate.getTimeInMillis()));

        mStartDate = findViewById(R.id.editText_start_date);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            schoolStartDate = new Calendar.Builder().setDate(year, month, dayOfMonth).build();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/y", Locale.getDefault());
            mStartDate.setText(simpleDateFormat.format(schoolStartDate.getTime()));
        }, schoolStartDate.get(Calendar.YEAR), schoolStartDate.get(Calendar.MONTH), schoolStartDate.get(Calendar.DAY_OF_MONTH));

        ImageButton buttonPickDate = findViewById(R.id.button_show_date_dialog);
        buttonPickDate.setOnClickListener(v -> datePickerDialog.show());

        ImageButton buttonAddFile = findViewById(R.id.button_add_file_path);
        buttonAddFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, BROWSE_DOC_REQUEST);

        });

        mRecyclerView = findViewById(R.id.recyclerview);
        fileListAdapter = new FileListAdapter(this, this.uploadedFilesPathList);
        mRecyclerView.setAdapter(fileListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {
            List<VEvent> schoolEvents = new ArrayList<>();
            for (ParcelFileDescriptor p : fileDescriptors) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(p.getFileDescriptor());
                    XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
                    XWPFTable weekCourseOutlineTable = getWeekCourseOutlineTable(xwpfDocument.getTables());
                    fileInputStream.close();

                    if (weekCourseOutlineTable == null)
                        throw new NullPointerException("NO TABLE FOUND!");

                    weekCourseOutlineTable.removeRow(0);
                    schoolEvents.addAll(getSchoolEvents(weekCourseOutlineTable, new int[]{4, 5}));
                } catch (IOException e) {
                    Log.d("meow", e.getMessage());
                    e.printStackTrace();
                }
            }
            ICalendar iCalendar = new ICalendar();
            for (VEvent e : schoolEvents) {
                iCalendar.addEvent(e);
            }
            FileOutputStream fileOutputStream = null;
            try {
                String calendarName = mCalendarName.getText().toString();
                String filename = (!calendarName.equals("")) ? calendarName : String.valueOf(schoolStartDate.getTimeInMillis());
                fileOutputStream = openFileOutput(filename + ".ics", MODE_PRIVATE);
                Biweekly.write(iCalendar).go(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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
        if (requestCode == BROWSE_DOC_REQUEST) {
            if (resultCode == RESULT_OK) {
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    String[] splittedPath = data.getData().getPath().split("/");
                    String fileName = splittedPath[splittedPath.length - 1];
                    if (!uploadedFilesPathList.contains(fileName)) {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                        fileDescriptors.add(parcelFileDescriptor);

                        uploadedFilesPathList.add(fileName);
                        int size = uploadedFilesPathList.size();
                        mRecyclerView.getAdapter().notifyItemInserted(size);
                        mRecyclerView.smoothScrollToPosition(size);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}











