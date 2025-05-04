package uk.ac.hope.mcse.android.coursework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A header + (expandable) list of Task objects.  Tasks are always stored
 * in a mutable ArrayList so you can safely add/remove at runtime.
 */
public class TaskGroup {
    private String groupTitle;
    private List<Task> tasks;
    private boolean isExpanded;

    /**
     * Primary constructor: wrap incoming list in a new ArrayList so it's mutable.
     */
    public TaskGroup(String groupTitle, List<Task> tasks) {
        this.groupTitle = groupTitle;
        // Wrap to ensure mutability:
        this.tasks = (tasks != null)
                ? new ArrayList<>(tasks)
                : new ArrayList<>();
        this.isExpanded = true;
    }

    /**
     * Convenience constructor for an empty task list.
     */
    public TaskGroup(String groupTitle) {
        this(groupTitle, new ArrayList<>());
    }

    // --- Getters & Setters ---

    public String getGroupTitle() {
        return groupTitle;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

}
