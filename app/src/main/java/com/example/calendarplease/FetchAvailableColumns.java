package com.example.calendarplease;

import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.widget.AppCompatCheckBox;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.List;

public class FetchAvailableColumns extends AsyncTask<ParcelFileDescriptor, Void, Hashtable<Integer, String>> {
    private Context mContext;
    private WeakReference<SyllabusDocument> mSyllabusDocument;
    private WeakReference<AvailableColumnsAdapter.SyllabusColumnViewHolder> mViewHolder;
    private XWPFTable wcoTable;


    public FetchAvailableColumns(Context context, SyllabusDocument document, AvailableColumnsAdapter.SyllabusColumnViewHolder viewHolder) {
        this.mContext = context;
        this.mSyllabusDocument = new WeakReference<>(document);
        this.mViewHolder = new WeakReference<>(viewHolder);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Hashtable<Integer, String> doInBackground(ParcelFileDescriptor... parcelFileDescriptors) {
        wcoTable = this.fetchWcoTable(parcelFileDescriptors[0]);
        if (wcoTable == null) return new Hashtable<>();
        return getAvailableColumns(wcoTable);
    }

    @Override
    protected void onPostExecute(Hashtable<Integer, String> availableColumns) {
        super.onPostExecute(availableColumns);

        mSyllabusDocument.get().setWcoTable(wcoTable);
        mSyllabusDocument.get().setAvailableColumnsIndex(availableColumns);
        mSyllabusDocument.get().getAvailableColumnsIndex().forEach((integer, s) -> {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(mContext);
            checkBox.setText(s);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int index = mSyllabusDocument.get().getPrefColumnsIndex().indexOf(integer);
                if (index == -1)
                    mSyllabusDocument.get().getPrefColumnsIndex().add(integer);
                else
                    mSyllabusDocument.get().getPrefColumnsIndex().remove(index);
            });
            mViewHolder.get().linearLayout.addView(checkBox);
        });
    }

    private XWPFTable fetchWcoTable(ParcelFileDescriptor parcelFileDescriptor) {
        XWPFTable wcoTable = null;
        try {
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
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
            parcelFileDescriptor.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wcoTable;
    }

    private Hashtable<Integer, String> getAvailableColumns(XWPFTable wcoTable) {
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
}
