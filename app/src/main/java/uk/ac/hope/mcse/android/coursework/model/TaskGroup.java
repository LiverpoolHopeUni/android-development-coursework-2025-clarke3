package uk.ac.hope.mcse.android.coursework.model;

import java.util.List;

public class TaskGroup {
    private String groupTitle;
    private List<Task> tasks;
    private boolean isExpanded;

    public TaskGroup(String groupTitle, List<Task> tasks) {
        this.groupTitle = groupTitle;
        this.tasks = tasks;
        this.isExpanded = true;
    }

    public String getGroupTitle() { return groupTitle; }
    public List<Task> getTasks() { return tasks; }
    public boolean isExpanded() { return isExpanded; }
    public void toggleExpanded() { isExpanded = !isExpanded; }
}

