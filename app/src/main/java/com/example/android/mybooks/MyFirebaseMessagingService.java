package com.example.android.mybooks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// servicio iniciado automáticamente por el SDK de Firebase
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String BOOK_ID = "com.example.android.mybooks.MyFirebaseMessagingService.book_position";
    public static final String ACTION_DELETE_BOOK = "com.example.android.mybooks.MyFirebaseMessagingService.action_delete_book";
    public static final String ACTION_VIEW_DETAILS = "com.example.android.mybooks.MyFirebaseMessagingService.action_view_details";

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
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(String string){
        Log.d(TAG, getString(R.string.new_token) + string);
    }

    /**
     * Create and show a notification containing the received FCM message.
     *
     * @param remoteMessage FCM message received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        String messageBody = remoteMessage.getNotification().getBody();
        String bookID = remoteMessage.getData().get("book_position");

        // Checks if notification contains any data payload.
        if(bookID != null) {
            Log.d(TAG , "book position we want to delete or view details: " + bookID);

            // Intent to pass 'delete' action to BookListActivity
            Intent intent1 = new Intent(this, BookListActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// Clear back history
            intent1.setAction(ACTION_DELETE_BOOK);
            intent1.putExtra(BOOK_ID, bookID);// Extra with the position of the book we want to delete
            // Pendind intent that will be launched when the user taps the 'delete' button.

            PendingIntent deleteIntent = PendingIntent.getActivity(this, 1122, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            // Intent to pass 'view details' action to BookListActivity
            Intent intent2 = new Intent(this, BookListActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// Clear back history
            intent2.setAction(ACTION_VIEW_DETAILS);
            intent2.putExtra(BOOK_ID, bookID);// Extra with the position of the book we want to view details
            // Pending intent that will be launched when the user taps the 'view details' button.
            // Note the use of FLAG_UPDATE_CURRENT for keeping the PendingIntent (if already exists) but replacing
            // its extra data with what is in this new intent.
            PendingIntent viewDetailsIntent = PendingIntent.getActivity(this, 5566, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // Creates the expanded notification with two buttons
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.ic_book) // Set the notification icon. Now it's a book.
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setContentText(messageBody)
                    .setVibrate(new long[]{1000,1000,1000,1000,1000}) // Sets the vibration.
                    .setLights(Color.BLUE,3000,3000) // Sets the notification light in blue.
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setAutoCancel(true)//TODO: no funciona setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .addAction(R.drawable.ic_delete, getString(R.string.notification_button_delete), deleteIntent)
                    .addAction(R.drawable.ic_view_details, getString(R.string.notification_button_viewDetails), viewDetailsIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Sets notification id to zero, so all messages from firebase will group together.
            notificationManager.notify(0, notificationBuilder.build());
        }else{
            Log.d(TAG,"Data payload error: " + bookID );
        }
    }
}
