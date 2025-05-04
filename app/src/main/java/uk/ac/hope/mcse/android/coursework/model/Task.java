package uk.ac.hope.mcse.android.coursework.model;

public class Task {
    private final String title;
    private boolean isDone;

    public Task(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public String getTitle() { return title; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}
