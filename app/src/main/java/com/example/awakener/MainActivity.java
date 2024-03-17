package com.example.awakener;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AddAlarmFragment.OnAlarmAddedListener, AlarmAdapter.OnAlarmLongClickListener, SettingsFragment.OnSettingsConfirmedListener {

    private AlarmDatabase alarmDatabase;
    private FrameLayout fragmentContainer;
    private TextView emptyListTextView;

    private List<Alarm> alarmList;
    private AlarmAdapter alarmAdapter;

    // Permission code for request (for higher APIs)
    private static final int PERMISSION_REQUEST_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the language preference, defaulting to "en" if it's not set yet
        String language = sharedPreferences.getString("language", "en");

        // Set the locale based on the language preference
        setLocale(language);

        // Initialize views
        fragmentContainer = findViewById(R.id.fragment_container);
        emptyListTextView = findViewById(R.id.empty_list_textview);
        emptyListTextView.setText(R.string.empty_list_message);

        checkAndRequestPermissions();

        // Initialize the database
        alarmDatabase = AlarmDatabase.getInstance(this);

        Log.d("MainActivity", "SavedInstance = null: " + (savedInstanceState == null));
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if there are any alarms in the database
        AsyncTask.execute(() -> {
            alarmList = alarmDatabase.alarmDao().getAll();
            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Restore the state of the fragment if savedInstanceState is not null
                if (savedInstanceState != null) {
                    // Restore the current fragment
                    Fragment currentFragment = fragmentManager.getFragment(savedInstanceState, "fragment");
                    Log.d("MainActivity", "Fragment typeOnLoad: " + currentFragment.getClass().getName());

                    if (currentFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, currentFragment)
                                .commitAllowingStateLoss();
                    }
                    Log.d("MainActivity", "Fragment type Loaded: " + getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getName());
                    // Update view visibility based on alarm count
                    updateViewVisibility();
                }
                else {
                    // Load the alarm list fragment
                    loadAlarmListFragment();
                }

            });
        });

        alarmAdapter = new AlarmAdapter(this, alarmList, this);
        alarmAdapter.setOnAlarmLongClickListener(this);


        // "Add alarm" button
        ImageButton addAlarmButton = findViewById(R.id.add_alarm_button);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAlarmFragment();
            }
        });

        // "Home" button
        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAlarmListFragment();

                emptyListTextView = findViewById(R.id.empty_list_textview);
                emptyListTextView.setText(R.string.empty_list_message);

                // Update view visibility based on alarm count
                updateViewVisibility();
            }
        });

        // "Activities" button
        ImageButton activitiesButton = findViewById(R.id.activities_button);
        activitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openButtonsFragment();
            }
        });

        // "Settings" button
        ImageButton settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsFragment();
            }
        });


        Log.d("MainActivity", "Fragment type null?: " + (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null));

    }

    private void setLocale(String language) {
        // Create a Locale object based on the language preference
        Locale locale;
        switch (language) {
            case "en":
                locale = new Locale("en");
                break;
            case "es":
                locale = new Locale("es", "ES");
                break;
            case "eu":
                locale = new Locale("eu", "ES"); // Basque language code
                break;
            default:
                locale = new Locale("en");
                break;
        }

        // Set the locale for the entire application
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    private void loadAlarmListFragment() {
        Log.d("MainActivity", "AlarmList loaded");
        // Perform database operation asynchronously using Room's built-in support for async queries
        AsyncTask.execute(() -> {
            alarmList = alarmDatabase.alarmDao().getAll();
            // Update the UI on the main thread
            runOnUiThread(() -> {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AlarmListFragment())
                        .runOnCommit(() -> { // THIS MAKES IT WORK!!!
                            // Update visibility of views after loading the fragment
                            updateViewVisibility();
                        })
                        .commit();
            });

        });
    }

    @Override
    public void onAlarmAdded(Alarm alarm) {
        // Add the alarm to the database
        AsyncTask.execute(() -> {
            alarmDatabase.alarmDao().insert(alarm);
        });

        scheduleAlarm(alarm);

        loadAlarmListFragment();

        // Display a toast message when the alarm is added
        Toast.makeText(this, getString(R.string.ToastAddAlarm) + " " + alarm.getName() + " (" + alarm.getTime() + ")", Toast.LENGTH_SHORT).show();

        Log.d("MainActivity", "New alarm added: " + alarm.getTime());
    }


    private void updateViewVisibility() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (alarmList.isEmpty() && currentFragment instanceof AlarmListFragment ) {
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            emptyListTextView.setVisibility(View.GONE);
        }


    }

    private void openAddAlarmFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof AddAlarmFragment) {
            return;
        }
        emptyListTextView.setVisibility(View.GONE);
        AddAlarmFragment addAlarmFragment = new AddAlarmFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addAlarmFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openButtonsFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ButtonsFragment) {
            return;
        }
        emptyListTextView.setVisibility(View.GONE);
        ButtonsFragment buttonsFragment = new ButtonsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, buttonsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openSettingsFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SettingsFragment) {
            return;
        }
        emptyListTextView.setVisibility(View.GONE);
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, settingsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteConfirmationDialog(Alarm alarm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.TextDeleteAlarm));
        builder.setMessage(getString(R.string.TextYouSureDeleteAlarm));
        builder.setPositiveButton(getString(R.string.ButtonOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAlarm(alarm);
            }
        });
        builder.setNegativeButton(getString(R.string.ButtonCancel), null);
        builder.show();
    }


    private void deleteAlarm(Alarm alarm) {
        int alarmId = alarm.getId();

        // Cancel the PendingIntent associated with the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        // Delete the alarm from the database
        AsyncTask.execute(() -> {
            alarmDatabase.alarmDao().delete(alarm);
            runOnUiThread(() -> {
                // Notify the adapter that the dataset has changed
                alarmAdapter.notifyDataSetChanged();

                loadAlarmListFragment();

            });
        });

        // Display a toast message when the alarm is deleted
        Toast.makeText(this, getString(R.string.TextDeleteAlarm) + " " + alarm.getName() + " (" + alarm.getTime() + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlarmLongClicked(Alarm alarm) {
        showDeleteConfirmationDialog(alarm);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadAlarmListFragment();
//        emptyListTextView = findViewById(R.id.empty_list_textview);
//        emptyListTextView.setText(R.string.empty_list_message);
//    }

    private void scheduleAlarm(Alarm alarm) {
        // Parse the time string into hours and minutes
        String[] parts = alarm.getTime().split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        int currentHours = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);

        // Set the alarm time (seconds to 0)
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        // If the alarm time is before the current time, schedule it for the next day
        if (hours < currentHours || (hours == currentHours && minutes <= currentMinutes)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long triggerTimeInMillis = calendar.getTimeInMillis();

        // Perform database operation asynchronously
        AsyncTask.execute(() -> {
            // Retrieve the alarm ID from the database
            int alarmId = alarmDatabase.alarmDao().getAlarmIdByTimeNameAndType(alarm.getTime(), alarm.getName(), alarm.getType());

            // Create an intent for the AlarmReceiver
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra("name", alarm.getName());
            intent.putExtra("alarm_id", alarmId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE);

            // Schedule the alarm
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);

            Log.d("MainActivity", "Set: " + alarmId);
        });
    }

    @Override
    public void onSettingsConfirmed() {
        loadAlarmListFragment();
        emptyListTextView = findViewById(R.id.empty_list_textview);
        emptyListTextView.setText(R.string.empty_list_message);
    }

    // Method to check and request permissions (for higher APIs)
    private void checkAndRequestPermissions() {
        // Check if the permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.d("MainActivity", "Fragment type onSave: " + currentFragment.getClass().getName());

        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, "fragment", currentFragment);
        }
    }

}