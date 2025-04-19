package uk.ac.hope.mcse.android.coursework.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.model.Task;
import uk.ac.hope.mcse.android.coursework.model.TaskGroup;

public class TaskGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private final List<Object> displayItems;

    public TaskGroupAdapter(List<TaskGroup> taskGroups) {
        this.displayItems = new ArrayList<>();
        for (TaskGroup group : taskGroups) {
            displayItems.add(group); // Add group header
            if (group.isExpanded()) {
                displayItems.addAll(group.getTasks()); // Add all tasks under this group
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (displayItems.get(position) instanceof TaskGroup) ? VIEW_TYPE_HEADER : VIEW_TYPE_TASK;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            TaskGroup group = (TaskGroup) displayItems.get(position);
            ((HeaderViewHolder) holder).groupTitle.setText(group.getGroupTitle());
        } else if (holder instanceof TaskViewHolder) {
            Task task = (Task) displayItems.get(position);
            TaskViewHolder taskHolder = (TaskViewHolder) holder;

            taskHolder.checkBox.setText(task.getTitle());
            taskHolder.checkBox.setChecked(task.isDone());

            taskHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
        }
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView groupTitle;

        HeaderViewHolder(View view) {
            super(view);
            groupTitle = view.findViewById(R.id.group_title);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;

        TaskViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.task_checkbox);
        }
    }
}
