package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.calendar.EmojiBelowDayDecorator;
import uk.ac.hope.mcse.android.coursework.calendar.TodayDecorator;

public class SecondFragment extends Fragment {

    public SecondFragment() {
        super(R.layout.fragment_second); // Use fragment_second.xml layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🧭 Setup toolbar with menu click listeners
        MaterialToolbar toolbar = view.findViewById(R.id.secondFragmentToolbar);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        // 📆 Reference the calendar view
        MaterialCalendarView calendarView = view.findViewById(R.id.calendarView);

        // 📌 Dummy event data
        List<CalendarDay> eventDays = new ArrayList<>();
        eventDays.add(CalendarDay.from(2025, 3, 10));
        eventDays.add(CalendarDay.from(2025, 3, 11));
        eventDays.add(CalendarDay.from(2025, 3, 12));

        // 🔁 Emoji mapping per event date
        HashMap<CalendarDay, String> emojiMap = new HashMap<>();
        emojiMap.put(CalendarDay.from(2025, 3, 10), "🧠");
        emojiMap.put(CalendarDay.from(2025, 3, 11), "🏋️");
        emojiMap.put(CalendarDay.from(2025, 3, 12), "💻");

        // ✅ Apply decorators: today highlight & emoji under dates
        calendarView.addDecorators(
                new TodayDecorator(),
                new EmojiBelowDayDecorator(emojiMap)
        );

        // 🎯 Date click feedback
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
            Toast.makeText(getContext(), "📅 Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
        });
    }

    // ⚙️ Top-right menu actions
    private boolean onMenuItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.menu_account) {
            Toast.makeText(getContext(), "👤 Account clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menu_settings) {
            Toast.makeText(getContext(), "⚙️ Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
