package uk.ac.hope.mcse.android.coursework.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashMap;
import java.util.Map;

public class EmojiBelowDayDecorator implements DayViewDecorator {
    private final Map<CalendarDay, String> emojiMap;
    // we’ll stash the day here when shouldDecorate(...) is called
    private CalendarDay lastDecoratedDay;

    public EmojiBelowDayDecorator(HashMap<CalendarDay, String> emojiMap) {
        this.emojiMap = emojiMap;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (emojiMap.containsKey(day)) {
            lastDecoratedDay = day;
            return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        String emoji = emojiMap.get(lastDecoratedDay);
        boolean faded = lastDecoratedDay.isBefore(CalendarDay.today());
        view.addSpan(new EmojiSpan(emoji, faded));
    }

    private static class EmojiSpan implements LineBackgroundSpan {
        private final String emoji;
        private final boolean faded;

        EmojiSpan(String emoji, boolean faded) {
            this.emoji = emoji;
            this.faded = faded;
        }

        @Override
        public void drawBackground(Canvas canvas,
                                   Paint paint,
                                   int left, int right,
                                   int top, int baseline, int bottom,
                                   CharSequence text, int start, int end, int lineNum) {

            Paint p = new Paint(paint);
            p.setTextAlign(Paint.Align.CENTER);

            // scale emoji a bit smaller than the date number
            float size = paint.getTextSize() * 0.82f;
            p.setTextSize(size);
            p.setAlpha(faded ? 110 : 255);

            float x = (left + right) / 2f;
            // draw it just below the date’s baseline
            float y = baseline + 1.2f * size;

            canvas.drawText(emoji, x, y, p);
        }
    }
}

