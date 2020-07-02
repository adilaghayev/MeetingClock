package com.example.meetingclock;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Meeting.class, version = 1)
public abstract class MeetingDatabase extends RoomDatabase {

    private static MeetingDatabase instance;

    public abstract MeetingDao meetingDao();

    public static synchronized MeetingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MeetingDatabase.class, "meeting_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();

        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private MeetingDao meetingDao;

        private PopulateDbAsyncTask(MeetingDatabase db) {
            meetingDao = db.meetingDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            meetingDao.insert(new Meeting("Meeting 1", "Agenda 1", "Username", "Attendees", 1593540020, 1593540090, 69));
            meetingDao.insert(new Meeting("Meeting 2", "Agenda 2", "Username", "Attendees b", 1593540100, 1593540110, 10));
            meetingDao.insert(new Meeting("Meeting 3", "Agenda 3", "Username", "Attendees c", 1593540113, 1593540123, 10));
            return null;
        }
    }
}
