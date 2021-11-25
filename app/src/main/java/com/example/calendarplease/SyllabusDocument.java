package com.example.calendarplease;

import android.content.ContentResolver;
import android.content.Context;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import biweekly.component.VEvent;

public class SyllabusDocument {
    private String path;
    private String fileName;
    private ParcelFileDescriptor fileDescriptor;
    private ContentResolver contentResolver;
    private XWPFDocument xwpfDocument;
    private XWPFTable wcoTable;
    public Uri uri;
    private List<VEvent> events;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        String[] splittedPath = path.split("/");
        this.fileName = splittedPath[splittedPath.length - 1];
    }

    public String getFileName() {
        return fileName;
    }

    public XWPFDocument getXwpfDocument() {
        return xwpfDocument;
    }

    public void setXwpfDocument(XWPFDocument xwpfDocument) {
        this.xwpfDocument = xwpfDocument;
    }

    public XWPFTable getWcoTable() {
        return wcoTable;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void getParcelFileDescriptor() {
        if (fileDescriptor != null) return;
        try {
            Log.d("meow2", path);
            Log.d("meow2", "*******************************");

//            fileDescriptor = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
            fileDescriptor = contentResolver.openFileDescriptor(uri,"r");
            Log.d("meow1", "parcel");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fetchWcoTable() {
        if (wcoTable != null) return;
        this.getParcelFileDescriptor();
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        try {
            XWPFDocument xwpfDocument = new XWPFDocument(inputStream);
            for (XWPFTable table : xwpfDocument.getTables()) {
                XWPFTableRow headerRow = table.getRows().get(0);
                XWPFTableCell firstCell = headerRow.getCell(0);
                XWPFTableCell secondCell = headerRow.getCell(1);
                if (firstCell.getText().trim().toLowerCase().contains("week")
                        && secondCell.getText().trim().toLowerCase().contains("date")) {
                    wcoTable = table;
                    break;
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateEvents(Calendar schoolStartDate, int[] prefColumnsIndex) {
        if (events == null) return;
        this.fetchWcoTable();

        int weekNumber = 1;
        for (XWPFTableRow row : this.wcoTable.getRows()) {
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

            for (int i : prefColumnsIndex) {
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

    }

    public Hashtable<Integer, String> fetchWcoHeaderColumns() {
        if (wcoTable == null) {
            this.fetchWcoTable();
        }

        Hashtable<Integer, String> prefColumns = new Hashtable<>();

        XWPFTableRow headerRow = wcoTable.getRow(0);
        List<XWPFTableCell> cells = headerRow.getTableCells();
        for (int i = 0; i < cells.size(); i++) {
            String text = cells.get(i).getText().trim();
            if (!TextUtils.isEmpty(text))
                prefColumns.put(i, text);
        }
        return prefColumns;
    }

}
