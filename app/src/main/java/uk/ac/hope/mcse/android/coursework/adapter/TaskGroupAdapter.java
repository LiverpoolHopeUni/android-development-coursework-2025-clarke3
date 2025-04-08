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

    private List<Object> items;

    public TaskGroupAdapter(List<TaskGroup> taskGroups) {
        this.items = new ArrayList<>();
        for (TaskGroup group : taskGroups) {
            items.add(group); // header
            if (group.isExpanded()) {
                items.addAll(group.getTasks()); // tasks
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof TaskGroup) return VIEW_TYPE_HEADER;
        else return VIEW_TYPE_TASK;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            TaskGroup group = (TaskGroup) items.get(position);
            ((HeaderViewHolder) holder).groupTitle.setText(group.getGroupTitle());
        } else {
            Task task = (Task) items.get(position);
            TaskViewHolder taskHolder = (TaskViewHolder) holder;
            taskHolder.checkBox.setText(task.getTitle());
            taskHolder.checkBox.setChecked(task.isDone());
            taskHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView groupTitle;

        HeaderViewHolder(View view) {
            super(view);
            groupTitle = view.findViewById(R.id.group_title);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        TaskViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.task_checkbox);
        }
    }
}
