package com.example.meetingclock;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.meetingclock.NotificationPublisher.NOTIFICATION_ID;

public class AddEditMeeting extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.meetingclock.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.meetingclock.EXTRA_TITLE";
    public static final String EXTRA_AGENDA =
            "com.example.meetingclock.EXTRA_AGENDA";
    public static final String EXTRA_START_DATETIME =
            "com.example.meetingclock.EXTRA_START_DATETIME";
    public static final String EXTRA_END_DATETIME =
            "com.example.meetingclock.EXTRA_END_DATETIME";


    private EditText editTextTitle;
    private EditText editTextAgenda;
    private TimePicker time;
    private TimePicker end_time;
    private DatePicker date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextAgenda = findViewById(R.id.edit_text_agenda);

        date = findViewById(R.id.date_picker);
        date.setMinDate(System.currentTimeMillis() - 1000);

        time = findViewById(R.id.time_picker);
        time.setIs24HourView(true);

        end_time = findViewById(R.id.time_picker_end);
        end_time.setIs24HourView(true);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Meeting");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextAgenda.setText(intent.getStringExtra(EXTRA_AGENDA));


        } else {
            setTitle("Add Meeting");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveMeeting() {
        String title = editTextTitle.getText().toString();
        String agenda = editTextAgenda.getText().toString();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cal.set(Calendar.MONTH, date.getMonth());
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.HOUR_OF_DAY, time.getHour());
        cal.set(Calendar.MINUTE, time.getMinute());
        cal.set(Calendar.SECOND, 0);

        Calendar cal_end = Calendar.getInstance();
        cal_end.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cal_end.set(Calendar.MONTH, date.getMonth());
        cal_end.set(Calendar.YEAR, date.getYear());
        cal_end.set(Calendar.HOUR_OF_DAY, end_time.getHour());
        cal_end.set(Calendar.MINUTE, end_time.getMinute());
        cal_end.set(Calendar.SECOND, 0);

        int start_datetime = (int) (cal.getTimeInMillis() / 1000);
        int end_datetime = (int) (cal_end.getTimeInMillis() / 1000);

        if ((start_datetime + 1 >= end_datetime) || (start_datetime < ((int) (System.currentTimeMillis() / 1000)))) {
            Toast.makeText(this, "Please, enter a valid time interval ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.trim().isEmpty() || agenda.trim().isEmpty()) {
            Toast.makeText(this, "Please, enter a title and an agenda", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_AGENDA, agenda);
        data.putExtra(EXTRA_START_DATETIME, start_datetime);
        data.putExtra(EXTRA_END_DATETIME, end_datetime);


        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);

        // Time to show notification at
        long timeAt = (long) start_datetime*1000L;
        long timeNow = System.currentTimeMillis();
        long timeEnd = (long) end_datetime*1000L;
        long duration = timeEnd - timeNow;
        long timeHalf = (timeEnd+timeAt)/2;


        OneTimeWorkRequest.Builder workBuilder = new OneTimeWorkRequest.Builder(NotificationWorker.class);
// I just need to set an delay here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            workBuilder.setInitialDelay(timeAt - timeNow - 60000, TimeUnit.MILLISECONDS);
        }

// This is just to complete the example
        long unique = System.currentTimeMillis();
        WorkManager.getInstance().enqueueUniqueWork(String.valueOf(unique),
                ExistingWorkPolicy.REPLACE,
                workBuilder.build());

        scheduleNotification(getNotification("Meeting started!", title), timeAt);
        scheduleNotification(getNotification("Half way through!", title), timeHalf);
        scheduleNotification(getNotification("1 minute left!", title), (timeEnd - 60000));
        scheduleNotification(getNotification("5 minutes left!", title), (timeEnd - 300000));
        scheduleNotification(getNotification("Meeting finished!", title), timeEnd);

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void scheduleNotification(Notification notification, long delay) {

        int random = (int)(Math.random()*25);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, delay);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) delay/1000+random, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content, String contentTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ID);
        builder.setContentTitle(contentTitle);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_meeting, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_meeting:
                saveMeeting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}