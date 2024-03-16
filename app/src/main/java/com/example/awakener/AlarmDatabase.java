package com.example.awakener;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();

    private static volatile AlarmDatabase INSTANCE;

    public static synchronized AlarmDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmDatabase.class, "alarm_database")
                    .build();
        }
        return INSTANCE;
    }

}
