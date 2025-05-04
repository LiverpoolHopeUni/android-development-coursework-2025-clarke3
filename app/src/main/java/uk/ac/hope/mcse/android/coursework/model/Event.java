package uk.ac.hope.mcse.android.coursework.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;

public class Event {
    public CalendarDay startDate;
    public CalendarDay endDate;
    public String emoji;
    public String description;

    public Event(CalendarDay startDate, CalendarDay endDate, String emoji, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.emoji = emoji;
        this.description = description;
    }
}
