package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import uk.ac.hope.mcse.android.coursework.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View binding setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set top toolbar
        setSupportActionBar(binding.toolbar);

        // Set up nav controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // FAB = Mic button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "🎤 Mic clicked — hook up speech input here", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.fab)
                    .show();
        });

        // Home + Calendar buttons
        ImageButton homeBtn = findViewById(R.id.nav_home);
        ImageButton calendarBtn = findViewById(R.id.nav_calendar);

        homeBtn.setOnClickListener(v -> navController.navigate(R.id.FirstFragment));
        calendarBtn.setOnClickListener(v -> navController.navigate(R.id.SecondFragment));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Optional action bar items (top-right)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Optional settings handler
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}

