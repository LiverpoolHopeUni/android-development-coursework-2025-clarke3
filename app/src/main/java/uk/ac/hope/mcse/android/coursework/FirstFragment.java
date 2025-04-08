package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import uk.ac.hope.mcse.android.coursework.databinding.FragmentFirstBinding;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.adapter.TaskGroupAdapter;
import uk.ac.hope.mcse.android.coursework.model.Task;
import uk.ac.hope.mcse.android.coursework.model.TaskGroup;
/**
 *
 */
public class FirstFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskGroupAdapter adapter;
    private List<TaskGroup> groupedTasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_first, container, false);

        recyclerView = root.findViewById(R.id.grouped_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupedTasks = getSampleTaskGroups(); // temp dummy data
        adapter = new TaskGroupAdapter(groupedTasks);
        recyclerView.setAdapter(adapter);

        return root;
    }

    private List<TaskGroup> getSampleTaskGroups() {
        List<TaskGroup> groups = new ArrayList<>();

        groups.add(new TaskGroup("Getting Started", Arrays.asList(
                new Task("✅ Create tasks, free up your mind", true),
                new Task("📋 Use lists to manage tasks", false)
        )));

        groups.add(new TaskGroup("Key Features", Arrays.asList(
                new Task("📅 Calendar: Check your schedule", false),
                new Task("🎯 Eisenhower Matrix: Prioritize tasks", false),
                new Task("🍅 Pomo: Beat procrastination", false),
                new Task("⏰ Habit: Visualize your efforts", false),
                new Task("✨ More amazing features", false)
        )));

        groups.add(new TaskGroup("Explore More", Arrays.asList(
                new Task("💎 Premium", false),
                new Task("💡 Follow us", false)
        )));

        return groups;
    }
}
