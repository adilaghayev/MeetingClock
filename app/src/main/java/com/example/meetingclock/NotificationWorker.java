package com.example.meetingclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification();
        return Result.success();
    }

    private void displayNotification(){

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("meetingiscoming", "meetingiscoming", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "meetingiscoming")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Your meeting is starting in a minute!")
                .setSmallIcon(R.mipmap.ic_launcher);

        int id = (int) System.currentTimeMillis()/1000;
        manager.notify(id, builder.build());

    }
}
