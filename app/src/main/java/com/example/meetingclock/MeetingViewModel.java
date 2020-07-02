package com.example.meetingclock;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MeetingViewModel extends AndroidViewModel {
    private MeetingRepository repository;
    private LiveData<List<Meeting>> allMeetings;

    public MeetingViewModel(@NonNull Application application) {
        super(application);
        repository = new MeetingRepository(application);
        allMeetings = repository.getAllMeetings();
    }

    public void insert(Meeting meeting) {
        repository.insert(meeting);
    }

    public void update(Meeting meeting) {
        repository.update(meeting);
    }

    public void delete(Meeting meeting) {
        repository.delete(meeting);
    }

    public LiveData<List<Meeting>> getAllMeetings() {
        return allMeetings;
    }
}
