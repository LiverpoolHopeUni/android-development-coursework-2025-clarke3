package uk.ac.hope.mcse.android.coursework;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import uk.ac.hope.mcse.android.coursework.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME    = "app_prefs";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_NAME      = "user_name";
    private static final String KEY_DOB       = "user_dob";
    private static final String KEY_GENDER    = "user_gender";

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfig;
    private SharedPreferences   prefs;
    private int                 currentDestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // — prefs & view-binding —
        prefs   = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // — first-run user info dialog —
        if (prefs.getBoolean(KEY_FIRST_RUN, true)) {
            showUserInfoDialog();
        }

        // — toolbar setup —
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitleTextColor(Color.BLACK);

        // — Navigation Component setup —
        NavController nav = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main
        );
        appBarConfig = new AppBarConfiguration.Builder(nav.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, nav, appBarConfig);

        // — track current destination for FAB routing —
        nav.addOnDestinationChangedListener((c, dest, args) -> {
            currentDestId = dest.getId();
            if (dest.getId() == R.id.FirstFragment) {
                String name = prefs.getString(KEY_NAME, "").trim();
                getSupportActionBar().setTitle(
                        name.isEmpty() ? dest.getLabel() : "👋 Hello " + name
                );
            } else {
                getSupportActionBar().setTitle(dest.getLabel());
            }
        });

        nav.addOnDestinationChangedListener((controller, dest, args) -> {
            String title;
            if (dest.getId() == R.id.FirstFragment) {
                String name = prefs.getString(KEY_NAME, "").trim();
                if (name.isEmpty()) {
                    assert dest.getLabel() != null;
                    title = dest.getLabel().toString();
                } else {
                    title = "👋 Hello " + name;
                }
            } else if (dest.getId() == R.id.SecondFragment) {
                String name = prefs.getString(KEY_NAME, "").trim();
                title = name.isEmpty()
                        ? "📅 Calendar"
                        : "👋 " + name + "'s Calendar";
            } else {
                assert dest.getLabel() != null;
                title = dest.getLabel().toString();
            }
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        });





        // — bottom nav buttons —
        binding.navHome.setOnClickListener(v -> nav.navigate(R.id.FirstFragment));
        binding.navCalendar.setOnClickListener(v -> nav.navigate(R.id.SecondFragment));

        // — FAB routing —
        binding.fab.setOnClickListener(v -> {
            if (currentDestId == R.id.FirstFragment) {
                FirstFragment ff = (FirstFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.nav_host_fragment_content_main)
                                .getChildFragmentManager()
                                .getPrimaryNavigationFragment();
                if (ff != null) ff.showAddTaskToGroupDialog();

            } else if (currentDestId == R.id.SecondFragment) {
                SecondFragment sf = (SecondFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.nav_host_fragment_content_main)
                                .getChildFragmentManager()
                                .getPrimaryNavigationFragment();
                if (sf != null) sf.showAddEventDialog();

            } else {
                Snackbar.make(v, "FAB tapped on other screen", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /** Collect name/DOB/gender on first-run only */
    private void showUserInfoDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_info, null);
        TextInputEditText nameIn  = dialogView.findViewById(R.id.input_name);
        TextInputEditText dayIn   = dialogView.findViewById(R.id.input_day);
        TextInputEditText monIn   = dialogView.findViewById(R.id.input_month);
        TextInputEditText yearIn  = dialogView.findViewById(R.id.input_year);
        RadioGroup        genderGp= dialogView.findViewById(R.id.input_gender);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Welcome! Tell us about you")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Save", (d, w) -> {
                    String name = nameIn.getText().toString().trim();
                    String dStr  = dayIn.getText().toString().trim();
                    String mStr  = monIn.getText().toString().trim();
                    String yStr  = yearIn.getText().toString().trim();
                    if (name.isEmpty()||dStr.isEmpty()||mStr.isEmpty()||yStr.isEmpty()) return;
                    int day, mon, yr;
                    try {
                        day = Integer.parseInt(dStr);
                        mon = Integer.parseInt(mStr);
                        yr  = Integer.parseInt(yStr);
                    } catch (NumberFormatException ex) { return; }
                    String dob = String.format("%04d-%02d-%02d", yr, mon, day);
                    int sel = genderGp.getCheckedRadioButtonId();
                    String gender = sel == R.id.radio_male   ? "Male"
                            : sel == R.id.radio_female ? "Female"
                            : "Other";

                    prefs.edit()
                            .putBoolean(KEY_FIRST_RUN, false)
                            .putString(KEY_NAME, name)
                            .putString(KEY_DOB, dob)
                            .putString(KEY_GENDER, gender)
                            .apply();

                    // if we're on FirstFragment right now, update subtitle
                    NavDestination cur = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                            .getCurrentDestination();
                    if (cur != null && cur.getId() == R.id.FirstFragment) {
                        getSupportActionBar().setTitle("👋 Hello " + name);
                    }
                })
                .show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                && binding.writeInputLayout.getVisibility() == View.VISIBLE) {
            Rect out = new Rect();
            binding.writeInputLayout.getGlobalVisibleRect(out);
            if (!out.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
                binding.writeInputLayout.startAnimation(slideDown);
                binding.writeInputLayout.setVisibility(View.GONE);
                binding.dimBackground.setVisibility(View.GONE);
                binding.fab.setVisibility(View.VISIBLE);
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(binding.writeInputField.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override public boolean onSupportNavigateUp() {
        NavController nav = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(nav, appBarConfig) || super.onSupportNavigateUp();
    }
}
