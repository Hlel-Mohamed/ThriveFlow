package de.tekup.thriveflow.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;

import de.tekup.thriveflow.R;

/**
 * PrayerAlarmReceiver is a BroadcastReceiver that handles prayer alarms.
 * When an alarm is received, it creates and shows a notification with the prayer name.
 */
public class PrayerAlarmReceiver extends BroadcastReceiver {
    // The ID of the notification channel
    private static final String CHANNEL_ID = "PRAYER_NOTIFICATION_CHANNEL";

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast for a prayer alarm.
     * It retrieves the prayer name from the Intent, creates a notification channel with a custom sound, and shows a notification with the prayer name.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the prayer name from the Intent
        String prayerName = intent.getStringExtra("prayerName");

        // Get the NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Uri for the custom notification sound
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.abdulbaset);

        // Create an AudioAttributes instance for the notification sound
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        // Create a NotificationChannel with the custom sound
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Prayer Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setSound(soundUri, audioAttributes);
        notificationManager.createNotificationChannel(channel);

        // Create a Notification with the prayer name
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Prayer Time")
                .setContentText("It's time for " + prayerName + " prayer.")
                .setSmallIcon(R.mipmap.ic_launcher_thriveflow_foreground)
                .build();

        // Show the notification
        notificationManager.notify(0, notification);
    }
}