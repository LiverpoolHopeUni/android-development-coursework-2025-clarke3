package uk.ac.hope.mcse.android.coursework.calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;

    public TodayDecorator() {
        this.today = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day != null && day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, Color.parseColor("#4CAF50"))); // soft green
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#2E7D32"))); // darker text green
    }
}
