package uk.ac.hope.mcse.android.coursework.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;
import java.util.HashMap;

public class EmojiBelowDayDecorator implements DayViewDecorator {

    private final HashMap<CalendarDay, String> emojiMap;

    public EmojiBelowDayDecorator(HashMap<CalendarDay, String> emojiMap) {
        this.emojiMap = emojiMap;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return emojiMap.containsKey(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new EmojiSpan());
    }

    private class EmojiSpan implements LineBackgroundSpan {
        @Override
        public void drawBackground(Canvas canvas, Paint paint,
                                   int left, int right, int top, int baseline, int bottom,
                                   CharSequence charSequence, int start, int end, int lineNum) {

            String dayString = charSequence.subSequence(start, end).toString();
            int day;
            try {
                day = Integer.parseInt(dayString);
            } catch (NumberFormatException e) {
                return;
            }

            Calendar today = Calendar.getInstance();
            CalendarDay thisDay = CalendarDay.from(today.get(Calendar.YEAR), today.get(Calendar.MONTH), day);
            String emoji = emojiMap.get(thisDay);
            if (emoji == null) return;

            boolean faded = thisDay.getDate().before(CalendarDay.today().getDate());

            Paint emojiPaint = new Paint(paint);
            emojiPaint.setTextAlign(Paint.Align.CENTER);
            emojiPaint.setTextSize(paint.getTextSize() * 0.85f);
            emojiPaint.setAlpha(faded ? 100 : 255);

            float x = (left + right) / 2f;
            float y = bottom + 36f; // margin between date and emoji

            canvas.drawText(emoji, x, y, emojiPaint);
        }
    }
}
