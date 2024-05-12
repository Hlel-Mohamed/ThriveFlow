package de.tekup.thriveflow.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import de.tekup.thriveflow.R;

public class PrayerAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "PRAYER_NOTIFICATION_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayerName");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.abdulbaset);

        // Create an AudioAttributes instance to pass to the NotificationChannel
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Prayer Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setSound(soundUri, audioAttributes);
        notificationManager.createNotificationChannel(channel);


        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Prayer Time")
                .setContentText("It's time for " + prayerName + " prayer.")
                .setSmallIcon(R.mipmap.ic_launcher_thriveflow_foreground)
                .build();

        notificationManager.notify(0, notification);
    }
}
