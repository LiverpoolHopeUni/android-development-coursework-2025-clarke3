package uk.ac.hope.mcse.android.coursework;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import uk.ac.hope.mcse.android.coursework.adapter.EventAdapter;
import uk.ac.hope.mcse.android.coursework.calendar.TodayDecorator;
import uk.ac.hope.mcse.android.coursework.model.Event;

public class SecondFragment extends Fragment {

    private static final String PREFS_NAME    = "app_prefs";
    private static final String KEY_EVENTS    = "saved_events";
    private static final String KEY_USER_NAME = "user_name";

    private final List<Event>                  events   = new ArrayList<>();
    private final HashMap<CalendarDay,String>  emojiMap = new HashMap<>();

    private MaterialCalendarView calendar;
    private EventAdapter          adapter;
    private RecyclerView          table;

    public SecondFragment() {
        super(R.layout.fragment_second);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        // 1) Toolbar title with user name
        MaterialToolbar toolbar = root.findViewById(R.id.secondFragmentToolbar);
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String user = prefs.getString(KEY_USER_NAME, "").trim();
        toolbar.setTitle(user.isEmpty() ? "📅 Calendar" : "👋 " + user + "’s Calendar");

        // 2) Calendar view
        calendar = root.findViewById(R.id.calendarView);

        // 3) Load persisted events (may leave events empty)
        loadEvents();

        // 4) RecyclerView + adapter MUST come *before* any addEvent/seedDemoEvents
        table = root.findViewById(R.id.eventTable);
        table.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EventAdapter(events);
        table.setAdapter(adapter);

        // 5) If we have no saved events, seed demo now that adapter is non-null
        if (events.isEmpty()) {
            seedDemoEvents();
            Snackbar.make(root, "No events yet – tap + to add one!", Snackbar.LENGTH_LONG).show();
        }

        // 6) Swipe-to-delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            @NonNull RecyclerView.ViewHolder t) {
                return false;
            }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Event ev = events.remove(pos);
                clearEmojiRange(ev.startDate, ev.endDate);
                adapter.notifyItemRemoved(pos);
                saveEvents();
                redrawCalendar();
            }
        }).attachToRecyclerView(table);

        // 7) Initial draw of calendar
        redrawCalendar();

        // 8) Click feedback on days
        calendar.setOnDateChangedListener((w, d, sel) -> {
            String msg = d.getDay()+"/"+(d.getMonth()+1)+"/"+d.getYear();
            Toast.makeText(requireContext(), "Selected: "+msg, Toast.LENGTH_SHORT).show();
        });

        // 9) Scroll to bottom only if there is at least one item
        if (adapter.getItemCount() > 0) {
            table.post(() -> table.smoothScrollToPosition(adapter.getItemCount() - 1));
        }
    }

    /** Show “Add Event” dialog (called from FAB) */
    public void showAddEventDialog() {
        View dlg = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_event, null, false);

        TextInputEditText titleIn = dlg.findViewById(R.id.input_event_title);
        TextInputEditText emojiIn = dlg.findViewById(R.id.input_event_emoji);
        TextInputEditText sd      = dlg.findViewById(R.id.input_start_day);
        TextInputEditText sm      = dlg.findViewById(R.id.input_start_month);
        TextInputEditText sy      = dlg.findViewById(R.id.input_start_year);
        TextInputEditText ed      = dlg.findViewById(R.id.input_end_day);
        TextInputEditText em      = dlg.findViewById(R.id.input_end_month);
        TextInputEditText ey      = dlg.findViewById(R.id.input_end_year);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New Event")
                .setView(dlg)
                .setPositiveButton("Add", (d,w)->{
                    String title = Objects.requireNonNull(titleIn.getText()).toString().trim();
                    String emoji = Objects.requireNonNull(emojiIn.getText()).toString().trim();
                    if (title.isEmpty()||emoji.isEmpty()) return;
                    try {
                        CalendarDay start = CalendarDay.from(
                                Integer.parseInt(sy.getText().toString()),
                                Integer.parseInt(sm.getText().toString())-1,
                                Integer.parseInt(sd.getText().toString())
                        );
                        CalendarDay end   = CalendarDay.from(
                                Integer.parseInt(ey.getText().toString()),
                                Integer.parseInt(em.getText().toString())-1,
                                Integer.parseInt(ed.getText().toString())
                        );
                        addEvent(start, end, emoji, title);
                    } catch (NumberFormatException ignored){}
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ─── Core: add/remove events ─────────────────────────

    private void addEvent(CalendarDay s, CalendarDay e, String emoji, String desc) {
        events.add(new Event(s, e, emoji, desc));
        paintRange(s, e, emoji);
        events.sort(Comparator.comparing(ev -> ev.startDate.getDate()));
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0) {
            table.post(() -> table.smoothScrollToPosition(adapter.getItemCount() - 1));
        }
        redrawCalendar();
        saveEvents();
    }

    private void clearEmojiRange(CalendarDay s, CalendarDay e) {
        LocalDate cur  = LocalDate.of(s.getYear(),  s.getMonth()+1, s.getDay());
        LocalDate last = LocalDate.of(e.getYear(),  e.getMonth()+1, e.getDay());
        while (!cur.isAfter(last)) {
            CalendarDay cd = CalendarDay.from(
                    cur.getYear(), cur.getMonthValue()-1, cur.getDayOfMonth()
            );
            emojiMap.remove(cd);
            cur = cur.plusDays(1);
        }
    }

    private void paintRange(CalendarDay s, CalendarDay e, String emoji) {
        LocalDate cur  = LocalDate.of(s.getYear(),  s.getMonth()+1, s.getDay());
        LocalDate last = LocalDate.of(e.getYear(),  e.getMonth()+1, e.getDay());
        while (!cur.isAfter(last)) {
            CalendarDay cd = CalendarDay.from(
                    cur.getYear(), cur.getMonthValue()-1, cur.getDayOfMonth()
            );
            emojiMap.put(cd, emoji);
            cur = cur.plusDays(1);
        }
    }

    private void redrawCalendar() {
        calendar.removeDecorators();
        calendar.addDecorator(new TodayDecorator());
        for (CalendarDay d : emojiMap.keySet()) {
            calendar.addDecorator(new SingleEmojiDecorator(d, emojiMap.get(d)));
        }
        calendar.invalidateDecorators();
    }

    // ─── Persistence ────────────────────────────────────────

    private void loadEvents() {
        SharedPreferences p = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = p.getString(KEY_EVENTS, "[]");
        events.clear();
        emojiMap.clear();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                CalendarDay s = CalendarDay.from(
                        o.getInt("start_year"),
                        o.getInt("start_month"),
                        o.getInt("start_day")
                );
                CalendarDay e = CalendarDay.from(
                        o.getInt("end_year"),
                        o.getInt("end_month"),
                        o.getInt("end_day")
                );
                String emj = o.getString("emoji");
                String ttl = o.getString("title");
                events.add(new Event(s, e, emj, ttl));
                paintRange(s, e, emj);
            }
        } catch (JSONException ex) {
            Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
        }
        events.sort(Comparator.comparing(ev -> ev.startDate.getDate()));
    }

    private void saveEvents() {
        SharedPreferences p = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        for (Event ev : events) {
            try {
                JSONObject o = new JSONObject();
                o.put("start_year",  ev.startDate.getYear());
                o.put("start_month", ev.startDate.getMonth());
                o.put("start_day",   ev.startDate.getDay());
                o.put("end_year",    ev.endDate.getYear());
                o.put("end_month",   ev.endDate.getMonth());
                o.put("end_day",     ev.endDate.getDay());
                o.put("emoji",       ev.emoji);
                o.put("title",       ev.description);
                arr.put(o);
            } catch (JSONException ignored) { }
        }
        p.edit().putString(KEY_EVENTS, arr.toString()).apply();
    }

    private void seedDemoEvents() {
        CalendarDay d1 = CalendarDay.from(2025, 3, 15);
        CalendarDay d2 = CalendarDay.from(2025, 3, 17);
        addEvent(d1, d1, "🧠", "Study Algorithms");
        addEvent(d2, d2, "🏖️", "Beach Day");
    }

    // ─── Toolbar menu callbacks ────────────────────────────



    // ─── Per-day emoji decorator ────────────────────────────

    private static class SingleEmojiDecorator
            implements com.prolificinteractive.materialcalendarview.DayViewDecorator {
        private final CalendarDay day;
        private final String      emoji;
        SingleEmojiDecorator(CalendarDay d, String em) {
            day = d; emoji = em;
        }
        @Override public boolean shouldDecorate(CalendarDay d) {
            return day.equals(d);
        }
        @Override public void decorate(
                com.prolificinteractive.materialcalendarview.DayViewFacade view) {
            view.addSpan(new EmojiSpan(emoji));
        }
        private static class EmojiSpan
                implements android.text.style.LineBackgroundSpan {
            private final String emoji;
            EmojiSpan(String em) { emoji = em; }
            @Override public void drawBackground(Canvas canvas, Paint paint,
                                                 int left, int right, int top,
                                                 int baseline, int bottom,
                                                 CharSequence text, int start,
                                                 int end, int lineNum) {
                Paint p2 = new Paint(paint);
                p2.setTextAlign(Paint.Align.CENTER);
                p2.setTextSize(paint.getTextSize() * 0.85f);
                float x = (left + right) / 2f;
                float y = bottom + p2.getTextSize();
                canvas.drawText(emoji, x, y, p2);
            }
        }
    }
}
