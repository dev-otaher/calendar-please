package com.example.calendarplease;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

    public List<VEvent> generateEvents() {
        List<VEvent> events = new ArrayList<>();
        if (syllabusDocumentList == null || syllabusDocumentList.size() == 0) return events;

        for (SyllabusDocument doc : syllabusDocumentList) {
            events.addAll(doc.generateEvents(schoolStartDate));
        }
        return events;
    }

    public List<VEvent> generateWeekTagEvents() {
        List<VEvent> events = new ArrayList<>();
        if (syllabusDocumentList == null || syllabusDocumentList.size() == 0) return events;
        List<Integer> maxWeekNumberList = new ArrayList<>();
        for (SyllabusDocument doc : syllabusDocumentList) {
            maxWeekNumberList.add(doc.getMaxWeekNumber());
        }
        Collections.sort(maxWeekNumberList, Collections.reverseOrder());

        for (int i = 1; i < maxWeekNumberList.get(0) + 1; i++) {
            Calendar startDate = (Calendar) schoolStartDate.clone();
            int currentDayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            if (i != 1)
                startDate.add(Calendar.DAY_OF_MONTH, ((7 * (i - 1))) - (currentDayOfWeek - 1));

            Calendar endDate = (Calendar) startDate.clone();
            currentDayOfWeek = endDate.get(Calendar.DAY_OF_WEEK);
            endDate.add(Calendar.DAY_OF_WEEK, (5 - (currentDayOfWeek - 1)));

            VEvent weekTag = new VEvent();
            weekTag.setSummary("Week#" + i);
            weekTag.setDateStart(startDate.getTime(), false);
            weekTag.setDateEnd(endDate.getTime(), false);

            events.add(weekTag);
        }


        return events;
    }
}
