package com.example.calendarplease;

import android.os.ParcelFileDescriptor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class SyllabusDocument {
    private String path;
    private String fileName;
    private ParcelFileDescriptor fileDescriptor;
    private XWPFDocument xwpfDocument;

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
}
