package uk.ac.hope.mcse.android.coursework.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.R;
import uk.ac.hope.mcse.android.coursework.model.Task;
import uk.ac.hope.mcse.android.coursework.model.TaskGroup;

public class TaskGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnAddTaskListener {
        void onAddTask(TaskGroup group);
    }

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TASK   = 1;

    private final List<TaskGroup> groups;
    private final List<Object>    displayItems = new ArrayList<>();
    private final OnAddTaskListener addTaskListener;

    /** Return the flat‐list position of that group header */
    public int positionOfGroup(TaskGroup target) {
        rebuildDisplayItems();          // ensure displayItems is fresh
        for (int i = 0; i < displayItems.size(); i++) {
            Object o = displayItems.get(i);
            if (o instanceof TaskGroup && o == target) {
                return i;
            }
        }
        return -1;
    }


    public TaskGroupAdapter(List<TaskGroup> groups, OnAddTaskListener listener) {
        this.groups = groups;
        this.addTaskListener = listener;
        rebuildDisplayItems();
    }

    /** Rebuild the flat list from the groups+tasks */
    private void rebuildDisplayItems() {
        displayItems.clear();
        for (TaskGroup g : groups) {
            displayItems.add(g);
            if (g.isExpanded()) {
                displayItems.addAll(g.getTasks());
            }
        }
    }

    /** Call when your underlying data (groups/tasks) changes */
    public void refresh() {
        rebuildDisplayItems();
        notifyDataSetChanged();
    }

    /** Expose the flat item at position (for swipe logic) */
    public Object getItemAt(int position) {
        return displayItems.get(position);
    }

    @Override public int getItemViewType(int pos) {
        return (displayItems.get(pos) instanceof TaskGroup)
                ? VIEW_TYPE_HEADER
                : VIEW_TYPE_TASK;
    }

    @Override public int getItemCount() {
        return displayItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_group_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(
            RecyclerView.ViewHolder holder, int position) {

        Object item = displayItems.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((TaskGroup) item);
        } else {
            ((TaskViewHolder) holder).bind((Task) item);
        }
    }

    // —— ViewHolders —— //

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView groupTitle;
        ImageButton addBtn;
        HeaderViewHolder(View v) {
            super(v);
            groupTitle = v.findViewById(R.id.group_title);
            addBtn     = v.findViewById(R.id.button_add_task);
        }
        void bind(final TaskGroup g) {
            groupTitle.setText(g.getGroupTitle());
            addBtn.setOnClickListener(x -> {
                if (addTaskListener != null) {
                    addTaskListener.onAddTask(g);
                }
            });
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TaskViewHolder(View v) {
            super(v);
            checkBox = v.findViewById(R.id.task_checkbox);
        }
        void bind(final Task t) {
            checkBox.setText(t.getTitle());
            checkBox.setChecked(t.isDone());
            checkBox.setOnCheckedChangeListener((b, checked) -> t.setDone(checked));
        }
    }
}

