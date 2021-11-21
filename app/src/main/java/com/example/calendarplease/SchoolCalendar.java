package com.example.calendarplease;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import biweekly.component.VEvent;

public class SchoolCalendar {
    private String title;
    private List<VEvent> events;
    private Calendar schoolStartDate;
    private List<SyllabusDocument> syllabusDocumentList;

    public SchoolCalendar() {
        this.syllabusDocumentList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VEvent> getEvents() {
        return events;
    }

    public void setEvents(List<VEvent> events) {
        this.events = events;
    }

    public Calendar getSchoolStartDate() {
        return schoolStartDate;
    }

    public void setSchoolStartDate(Calendar schoolStartDate) {
        this.schoolStartDate = schoolStartDate;
    }

    public List<SyllabusDocument> getSyllabusDocumentList() {
        return syllabusDocumentList;
    }

    public void setSyllabusDocumentList(List<SyllabusDocument> syllabusDocumentList) {
        this.syllabusDocumentList = syllabusDocumentList;
    }
}
