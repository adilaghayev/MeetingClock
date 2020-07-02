package com.example.meetingclock;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meeting_table")
public class Meeting {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String agenda;
    private String host_name;
    private String attendees;
    private int start_datetime;
    private int end_datetime;
    private int duration;

    public Meeting(String title, String agenda, String host_name, String attendees, int start_datetime, int end_datetime, int duration) {
        this.title = title;
        this.agenda = agenda;
        this.host_name = host_name;
        this.attendees = attendees;
        this.start_datetime = start_datetime;
        this.end_datetime = end_datetime;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAgenda() {
        return agenda;
    }

    public String getHost_name() {
        return host_name;
    }

    public String getAttendees() {
        return attendees;
    }

    public int getStart_datetime() {
        return start_datetime;
    }

    public int getEnd_datetime() {
        return end_datetime;
    }

    public int getDuration() {
        return duration;
    }
}
