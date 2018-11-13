package com.example.android.mybooks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// servicio iniciado automáticamente por el SDK de Firebase
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        // [START_EXCLUDE]
        // There are two types of messages: data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String string){
        Log.d(TAG, "New token: " + string);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent1 = new Intent(this, BookListActivity.class);
        intent1.setAction(Intent.ACTION_DELETE);
        PendingIntent deleteIntent = PendingIntent.getActivity(this,0,intent1,0);
        Intent intent2 = new Intent(this,BookListActivity.class);
        intent2.setAction(Intent.ACTION_VIEW);
        //intent2.putExtra(Intent.EXTRA_TEXT,"10");
        PendingIntent viewDetailsIntent = PendingIntent.getActivity(this, 1, intent2, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Ejemplo Firebase")
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(new NotificationCompat.Action(R.drawable.ic_stat_ic_notification,"borrar libro",deleteIntent))
                .addAction(R.drawable.ic_stat_ic_notification,"ver detalle libro",viewDetailsIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,notificationBuilder.build());
    }
}
