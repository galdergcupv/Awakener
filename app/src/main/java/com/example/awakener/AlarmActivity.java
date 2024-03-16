package com.example.awakener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.concurrent.atomic.AtomicInteger;

public class AlarmActivity extends AppCompatActivity {
    private FrameLayout infoFragmentContainer;
    private FrameLayout controlFragmentContainer;
    private AlarmDatabase alarmDatabase;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;
    private int activiyType;
    // Constants for notification channel
    private static final String CHANNEL_ID = "AlarmChannel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for alarm notifications";
    // Unique ID for the notification
    private static final int NOTIFICATION_ID = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        createNotificationChannel();

        infoFragmentContainer = findViewById(R.id.info_fragment_container);
        controlFragmentContainer = findViewById(R.id.control_fragment_container);

        alarmDatabase = AlarmDatabase.getInstance(this);


        if (savedInstanceState == null) {
            boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
            Log.d("AlarmActivity", "isAlarm: " + isAlarm);

            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AlarmActivity:wakeLock");

            // If it's an alarm sets AlarmInfoFragment on top + alarm features
            if (isAlarm) {
                if (true) { // TODO: Change to vibrator preference

                    long[] pattern = {0, 1000, 1000}; // Vibrate for 1 second, then pause for 1 second
                    vibrator.vibrate(pattern, 0);
                }

                wakeLock.acquire();

                // Start playing the alarm sound
                mediaPlayer.setLooping(true);
                mediaPlayer.start();

                int alarmId = getIntent().getIntExtra("alarm_id", -1);
                Log.d("AlarmActivity", "ReceivedID: " + alarmId);
                if (alarmId != -1) {
                    AsyncTask.execute(() -> {
                        // Access database operation in a background thread
                        Alarm alarm = alarmDatabase.alarmDao().getAlarmById(alarmId);
                        String time = alarm.getTime();
                        String name = alarm.getName();
                        activiyType = alarm.getType();
                        alarmDatabase.alarmDao().delete(alarm);
                        Log.d("AlarmActivity", "AlarmID: " + alarmId);
                        Log.d("AlarmActivity", "Alarm getID: " + alarm.getId());
                        Log.d("AlarmActivity", "Alarm Name: " + name);
                        Log.d("AlarmActivity", "Alarm Time: " + time);
                        Log.d("AlarmActivity", "Alarm Type: " + activiyType);
                        runOnUiThread(() -> {

                            sendNotification(name,time);

                            // AlarmInfoFragment
                            Bundle args = new Bundle();
                            args.putString("alarm_name", name);
                            args.putString("alarm_time", time);
                            args.putInt("alarm_type", activiyType);
                            AlarmInfoFragment alarmInfoFragment = new AlarmInfoFragment();
                            alarmInfoFragment.setArguments(args);

                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.info_fragment_container, alarmInfoFragment)
                                    .commit();

                            // ControlFragment
                            switchControlFragment();

                        });
                    });
                }

            }
            // If it's not an alarm sets the TestInfoFragment without the alarm features
            else {
                activiyType = getIntent().getIntExtra("type", -1);
                String name = getIntent().getStringExtra("game_name");
                Log.d("AlarmActivity", "Test Type: " + activiyType);

                // TestInfoFragment
                Bundle args = new Bundle();
                args.putInt("alarm_type", activiyType);
                TestInfoFragment testInfoFragment = new TestInfoFragment();
                testInfoFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.info_fragment_container, testInfoFragment)
                        .commit();

                // ControlFragment
                switchControlFragment();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();

        vibrator.cancel();

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void switchControlFragment() {
        // Depending on the activityType sets a different controlFragment
        Log.d("AlarmActivity", "switch type: " + activiyType);
        switch (activiyType) {
            case 0:
                // AlarmControlFragment
                AlarmControlFragment alarmControlFragment = new AlarmControlFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.control_fragment_container, alarmControlFragment)
                        .commit();
                break;
            case 1:
                // GuessGameFragment
                GuessGameFragment guessGameFragment = new GuessGameFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.control_fragment_container, guessGameFragment)
                        .commit();
                break;
        }
    }

    // Method to create notification channel (for higher APIs)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String name, String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(name)
                .setContentText(getString(R.string.NotiYourAlarm) + " " + name + " " + getString(R.string.NotiProgrammedFor) + " " + time + " " + getString(R.string.NotiHasBeenTriggered))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}

