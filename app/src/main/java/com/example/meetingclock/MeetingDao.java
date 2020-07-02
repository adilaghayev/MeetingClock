package com.example.meetingclock;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MeetingDao {

    @Insert
    void insert(Meeting meeting);

    @Update
    void update(Meeting meeting);

    @Delete
    void delete(Meeting meeting);

    @Query("SELECT * FROM meeting_table ORDER BY start_datetime")
    LiveData<List<Meeting>> getAllMeetings();

}
