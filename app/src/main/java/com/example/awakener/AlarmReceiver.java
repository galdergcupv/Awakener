package com.example.awakener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int alarmId = intent.getIntExtra("alarm_id", -1);

        Log.d("AlarmReceiver", "Received: " + alarmId);

        // Create an intent to open the AlarmActivity and pass the alarm ID as an extra
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("isAlarm", true);
        alarmIntent.putExtra("alarm_id", alarmId);

        // Start the AlarmActivity
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
