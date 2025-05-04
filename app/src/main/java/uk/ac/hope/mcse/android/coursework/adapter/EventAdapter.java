package uk.ac.hope.mcse.android.coursework.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.model.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_row, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.start.setText(format(event.startDate));
        holder.end.setText(format(event.endDate));
        holder.emoji.setText(event.emoji);
        holder.description.setText(event.description);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private String format(CalendarDay day) {
        return day.getDay() + "/" + (day.getMonth() + 1) + "/" + day.getYear();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView start, end, emoji, description;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.colStart);
            end = itemView.findViewById(R.id.colEnd);
            emoji = itemView.findViewById(R.id.colEmoji);
            description = itemView.findViewById(R.id.colDesc);
        }
    }
}
