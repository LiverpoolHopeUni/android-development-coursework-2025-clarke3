package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.adapter.TaskGroupAdapter;
import uk.ac.hope.mcse.android.coursework.model.Task;
import uk.ac.hope.mcse.android.coursework.model.TaskGroup;

public class FirstFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskGroupAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🧭 Setup toolbar with title and dropdown menu
        MaterialToolbar toolbar = view.findViewById(R.id.firstFragmentToolbar);
        toolbar.setTitle("👋 Hello Clarke");
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        // 🧩 Setup RecyclerView with dummy task groups
        recyclerView = view.findViewById(R.id.grouped_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskGroupAdapter(getSampleTaskGroups());
        recyclerView.setAdapter(adapter);
    }

    private boolean onMenuItemClicked(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_account) {
            Toast.makeText(getContext(), "👤 Account clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_settings) {
            Toast.makeText(getContext(), "⚙️ Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private List<TaskGroup> getSampleTaskGroups() {
        return Arrays.asList(
                new TaskGroup("Getting Started", Arrays.asList(
                        new Task("✅ Create tasks, free up your mind", true),
                        new Task("📋 Use lists to manage tasks", false)
                )),
                new TaskGroup("Key Features", Arrays.asList(
                        new Task("📅 Calendar: Check your schedule", false),
                        new Task("🎯 Eisenhower Matrix: Prioritize tasks", false),
                        new Task("🍅 Pomo: Beat procrastination", false),
                        new Task("⏰ Habit: Visualize your efforts", false),
                        new Task("✨ More amazing features", false)
                )),
                new TaskGroup("Explore More", Arrays.asList(
                        new Task("💎 Premium", false),
                        new Task("💡 Follow us", false)
                ))
        );
    }
}
