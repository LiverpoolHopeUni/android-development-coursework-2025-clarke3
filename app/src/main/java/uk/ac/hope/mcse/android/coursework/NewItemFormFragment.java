package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

public class NewItemFormFragment extends DialogFragment {

    public static NewItemFormFragment newInstance() {
        NewItemFormFragment frag = new NewItemFormFragment();
        frag.setStyle(STYLE_NORMAL, R.style.Theme_CourseworkApp_CenteredDialog);
        return frag;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_item_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        // TabLayout + two form containers
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        LinearLayout todoForm  = view.findViewById(R.id.todoForm);
        LinearLayout eventForm = view.findViewById(R.id.eventForm);

        // Toggle forms on tab select
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    todoForm.setVisibility(View.VISIBLE);
                    eventForm.setVisibility(View.GONE);
                } else {
                    todoForm.setVisibility(View.GONE);
                    eventForm.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        // To-Do Save
        TextInputEditText todoTitle = view.findViewById(R.id.inputTodoTitle);
        Button saveTodo = view.findViewById(R.id.btnSaveTodo);
        saveTodo.setOnClickListener(v -> {
            String title = todoTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                // handle save To-Do...
                dismiss();
            } else {
                todoTitle.setError("Required");
            }
        });

        // Event Save
        TextInputEditText eventTitle = view.findViewById(R.id.inputEventTitle);
        TextInputEditText eventDate  = view.findViewById(R.id.inputEventDate);
        Button saveEvent = view.findViewById(R.id.btnSaveEvent);
        saveEvent.setOnClickListener(v -> {
            String title = eventTitle.getText().toString().trim();
            String date  = eventDate.getText().toString().trim();
            if (!title.isEmpty() && !date.isEmpty()) {
                // handle save Event...
                dismiss();
            } else {
                if (title.isEmpty())  eventTitle.setError("Required");
                if (date.isEmpty())   eventDate.setError("Required");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Centered width (85% of screen)
        if (getDialog() != null && getDialog().getWindow() != null) {
            int w = (int)(getResources().getDisplayMetrics().widthPixels * 0.85);
            getDialog().getWindow().setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
