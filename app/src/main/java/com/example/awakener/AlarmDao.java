package com.example.awakener;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM alarms")
    List<Alarm> getAll();

    @Query("SELECT * FROM alarms WHERE id = :id")
    Alarm getAlarmById(int id);

    @Query("SELECT id FROM alarms WHERE time = :time AND name = :name AND type = :type")
    int getAlarmIdByTimeNameAndType(String time, String name, int type);

    @Insert
    void insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);
}
