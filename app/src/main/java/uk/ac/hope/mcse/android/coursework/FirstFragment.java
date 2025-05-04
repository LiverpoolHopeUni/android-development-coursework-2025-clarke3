package uk.ac.hope.mcse.android.coursework;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.adapter.TaskGroupAdapter;
import uk.ac.hope.mcse.android.coursework.model.Task;
import uk.ac.hope.mcse.android.coursework.model.TaskGroup;

public class FirstFragment extends Fragment
        implements TaskGroupAdapter.OnAddTaskListener {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_GROUPS = "task_groups";

    private static final String KEY_USER_NAME = "user_name";

    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private TaskGroupAdapter adapter;
    private List<TaskGroup> groupedTasks = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState);

        prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        // 1) Load persisted or fallback samples
        loadTaskGroups();

        // 2) RecyclerView + adapter
        recyclerView = view.findViewById(R.id.grouped_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskGroupAdapter(groupedTasks, this);
        recyclerView.setAdapter(adapter);

        // 3) Swipe-to-delete...
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder t) {
                return false;
            }
            @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Object item = adapter.getItemAt(pos);
                adapter.notifyItemChanged(pos);
                if (item instanceof TaskGroup) {
                    TaskGroup g = (TaskGroup)item;
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete List")
                            .setMessage("Delete “" + g.getGroupTitle() + "”?")
                            .setPositiveButton("Delete", (d,w) -> {
                                groupedTasks.remove(g);
                                adapter.refresh();
                                saveTaskGroups();
                            })
                            .setNegativeButton("Cancel", (d,w)-> adapter.notifyItemChanged(pos))
                            .show();
                } else {
                    Task t = (Task)item;
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete Task")
                            .setMessage("Delete “" + t.getTitle() + "”?")
                            .setPositiveButton("Delete", (d,w)->{
                                for (TaskGroup g : groupedTasks) {
                                    if (g.getTasks().remove(t)) break;
                                }
                                adapter.refresh();
                                saveTaskGroups();
                            })
                            .setNegativeButton("Cancel",(d,w)-> adapter.notifyItemChanged(pos))
                            .show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        // 4) “Add List” button
        view.findViewById(R.id.addListButton).setOnClickListener(v -> {
            View dlg = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_input_text, null, false);
            TextInputEditText in = dlg.findViewById(R.id.inputListTitle);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("New List")
                    .setView(dlg)
                    .setPositiveButton("Add",(d,w)->{
                        String title = in.getText().toString().trim();
                        if (!title.isEmpty()) {
                            groupedTasks.add(0, new TaskGroup(title, new ArrayList<>()));
                            adapter.refresh();
                            saveTaskGroups();
                            recyclerView.scrollToPosition(0);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public void onAddTask(TaskGroup group) {
        // existing “add a task to a specific group” dialog
        View dlg = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_input_text, null, false);
        TextInputEditText in = dlg.findViewById(R.id.inputListTitle);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Task to \"" + group.getGroupTitle() + "\"")
                .setView(dlg)
                .setPositiveButton("Add", (d,w)->{
                    String text = in.getText().toString().trim();
                    if (!text.isEmpty()) {
                        group.getTasks().add(new Task(text, false));
                        adapter.refresh();
                        saveTaskGroups();
                        int idx = groupedTasks.indexOf(group);
                        if (idx>=0) recyclerView.scrollToPosition(idx);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Called from MainActivity FAB when on this fragment.
     * Shows a dropdown of existing lists, then calls onAddTask(...)
     */
    public void showAddTaskToGroupDialog() {
        if (groupedTasks.isEmpty()) {
            Toast.makeText(getContext(),
                    "No lists available. Please add a list first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // build array of titles
        String[] titles = new String[groupedTasks.size()];
        for (int i = 0; i < titles.length; i++) {
            titles[i] = groupedTasks.get(i).getGroupTitle();
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select List")
                .setItems(titles, (dialog, which) -> {
                    // invoke your existing per-group dialog
                    onAddTask(groupedTasks.get(which));
                })
                .show();
    }

    /** Serialize groupedTasks → JSON → prefs */
    private void saveTaskGroups() {
        JSONArray arr = new JSONArray();
        for (TaskGroup g : groupedTasks) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", g.getGroupTitle());
                JSONArray tasks = new JSONArray();
                for (Task t : g.getTasks()) {
                    JSONObject to = new JSONObject();
                    to.put("text", t.getTitle());
                    to.put("done", t.isDone());
                    tasks.put(to);
                }
                obj.put("tasks", tasks);
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit()
                .putString(KEY_GROUPS, arr.toString())
                .apply();
    }

    /** Read prefs JSON → groupedTasks; fallback to samples if empty */
    private void loadTaskGroups() {
        groupedTasks.clear();
        String json = prefs.getString(KEY_GROUPS, null);
        if (json != null) {
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String title = obj.getString("title");
                    List<Task> tasks = new ArrayList<>();
                    JSONArray ta = obj.optJSONArray("tasks");
                    if (ta != null) {
                        for (int j = 0; j < ta.length(); j++) {
                            JSONObject to = ta.getJSONObject(j);
                            tasks.add(new Task(
                                    to.getString("text"),
                                    to.optBoolean("done", false)
                            ));
                        }
                    }
                    groupedTasks.add(new TaskGroup(title, tasks));
                }
                if (!groupedTasks.isEmpty()) return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // fallback sample data
        groupedTasks.addAll(getSampleTaskGroups());
    }

    private List<TaskGroup> getSampleTaskGroups() {
        List<TaskGroup> groups = new ArrayList<>();
        groups.add(new TaskGroup("Getting Started", Arrays.asList(
                new Task("✅ Create tasks, free up your mind", true),
                new Task("📋 Use lists to manage tasks", false)
        )));
        // … add other samples if you like …
        return groups;
    }
}
