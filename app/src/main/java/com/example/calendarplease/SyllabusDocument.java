package com.example.calendarplease;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import biweekly.component.VEvent;

public class SyllabusDocument {
    private String path;
    private String fileName;
    private ContentResolver contentResolver;
    private Uri uri;
    private ParcelFileDescriptor fileDescriptor;
    private String eventPrefix;
    private List<Integer> prefColumnsIndex;
    private XWPFDocument xwpfDocument;
    private XWPFTable wcoTable;
    private int maxWeekNumber;

    public SyllabusDocument() {
        prefColumnsIndex = new ArrayList<>();
    }

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

    public List<Integer> getPrefColumnsIndex() {
        return prefColumnsIndex;
    }

    public String getEventPrefix() {
        return eventPrefix;
    }

    public void setEventPrefix(String eventPrefix) {
        this.eventPrefix = eventPrefix;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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
            fileDescriptor = contentResolver.openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getMaxWeekNumber() {
        return maxWeekNumber;
    }

    public void setMaxWeekNumber(int maxWeekNumber) {
        this.maxWeekNumber = maxWeekNumber;
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

    public Hashtable<Integer, String> fetchAvailableColumns() {
        if (wcoTable == null) this.fetchWcoTable();
        Hashtable<Integer, String> availableColumns = new Hashtable<>();
        XWPFTableRow headerRow = wcoTable.getRow(0);
        List<XWPFTableCell> cells = headerRow.getTableCells();
        for (int i = 3; i < cells.size(); i++) {
            String text = cells.get(i).getText().trim();
            if (!TextUtils.isEmpty(text))
                availableColumns.put(i, text);
        }
        return availableColumns;
    }

    public List<VEvent> generateEvents(Calendar schoolStartDate) {
        List<VEvent> events = new ArrayList<>();

        if (prefColumnsIndex.size() == 0) return events;
        if (wcoTable == null) this.fetchWcoTable();

        int weekNumber = 1;
        for (XWPFTableRow row : this.wcoTable.getRows()) {
            if (row.getCell(0).getText().trim().equalsIgnoreCase("week")) continue;
            XWPFTableCell weekCell = row.getCell(0);

            // Redundant WeekTag when bulk upload :"(
            if (weekCell != null && !weekCell.getText().equals("")) {
                weekNumber = Integer.parseInt(weekCell.getText());
                maxWeekNumber = weekNumber;
            }

            Calendar rowStartDate = (Calendar) schoolStartDate.clone();
            int currentDayOfWeek = rowStartDate.get(Calendar.DAY_OF_WEEK);
            if (weekNumber != 1)
                rowStartDate.add(Calendar.DAY_OF_MONTH, ((7 * (weekNumber - 1))) - (currentDayOfWeek - 1));

            Calendar rowEndDate = (Calendar) rowStartDate.clone();
            currentDayOfWeek = rowEndDate.get(Calendar.DAY_OF_WEEK);
            rowEndDate.add(Calendar.DAY_OF_WEEK, (5 - (currentDayOfWeek - 1)));

            for (int i : prefColumnsIndex) {
                XWPFTableCell currentCell = row.getCell(i);
                if (currentCell != null) {
                    String title = row.getCell(i).getText();
                    if (!title.equals("")) {
                        VEvent event = new VEvent();
                        event.setSummary(this.eventPrefix + " - " + title);
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
