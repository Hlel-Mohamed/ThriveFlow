package de.tekup.thriveflow.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;

import de.tekup.thriveflow.MainActivity;
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
     * @param intent  The Intent received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the prayer name from the Intent
        String prayerName = intent.getStringExtra("prayerName");

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create an Intent for the notification
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(prayerName)
                .setSmallIcon(R.mipmap.ic_launcher_thriveflow_foreground)
                .setContentIntent(pendingIntent);

        // Create the notification channel
        CharSequence name = "Prayer Notification Channel";
        String description = "Channel for prayer notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Set the sound on the channel
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.abdulbaset);
        channel.setSound(soundUri, audioAttributes);

        // Create the notification channel
        notificationManager.createNotificationChannel(channel);

        // Build and show the notification
        Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }
}