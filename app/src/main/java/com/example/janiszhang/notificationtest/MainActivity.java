package com.example.janiszhang.notificationtest;

//import android.app.Notification;
import android.app.Notification;
import android.app.Notification.DecoratedCustomViewStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity {

    //    /**
//     * The request code can be any number as long as it doesn't match another request code used
//     * in the same app.
//     */
    private static final int REQUEST_CODE = 1234;
    private static final String ACTION_NOTIFICATION_DIRECT_REPLY = "com.example.janiszhang.notificationtest.delete";

    //    private static final String NOTIFICATION_GROUP =
//            "com.example.android.activenotifications.notification_type";
    private static final String NOTIFICATION_GROUP = "com.example.janiszhang.notificationtest.MainActivity.notificationgroup";

    private static final int NOTIFICATION_GROUP_SUMMARY_ID = 1;

    //    // Every notification needs a unique ID otherwise the previous one would be overwritten. This
//    // variable is incremented when used.
    private static int sNotificationId = NOTIFICATION_GROUP_SUMMARY_ID + 1;

//    private PendingIntent mDeletePendingIntent;
    private PendingIntent mDirectReplyPendingIntent;

    private BroadcastReceiver mDirectReplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("zhangbz", getMessageText(intent).toString());

            // Build a new notification, which informs the user that the system
// handled their interaction with the previous notification.
            Notification repliedNotification =
                    new Notification.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText("replyed")
                            .build();

// Issue the new notification.
//            NotificationManager notificationManager =
//                    NotificationManager.from(context);
            mNotificationManager.notify(mNewDirectReplyNotificationId, repliedNotification);
        }
    };

    private NotificationManager mNotificationManager;
    private Button mAddNotification;



    // Key for the string that's delivered in the action's intent
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    String replyLabel = "replyLabel";//getResources().getString(R.string.reply_label);
    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(replyLabel)
            .build();

    private Button mDirectReply;
    private int mNewDirectReplyNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        registerReceiver(mDirectReplyReceiver, new IntentFilter(ACTION_NOTIFICATION_DIRECT_REPLY));

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent directReplyIntent = new Intent(ACTION_NOTIFICATION_DIRECT_REPLY);
        mDirectReplyPendingIntent = PendingIntent.getBroadcast(this,REQUEST_CODE,directReplyIntent,0);

        mAddNotification = (Button) findViewById(R.id.add_notification);
        mAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotificationAndUpdateSummaries();
            }
        });

        mDirectReply = (Button) findViewById(R.id.direct_reply);
        mDirectReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDirectReplyNotification();
            }
        });
//        String replyLabel = "reply label";//getResources().getString(R.string.reply_label);
//        RemoteInput remoteInput = new RemoteInput().Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build();
//
//        //Create the reply action and add the remote input
//        new Notification.Action.Builder(R.drawable.)
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mDirectReplyReceiver, new IntentFilter(ACTION_NOTIFICATION_DIRECT_REPLY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDirectReplyReceiver);
    }

    private void addDirectReplyNotification() {
        // Create the reply action and add the remote input
        Notification.Action action =
                new Notification.Action.Builder(R.mipmap.ic_launcher,
                        "label", mDirectReplyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_view);
        // Build the notification and add the action
        Notification notification =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("DirectReply")
                        .setContentText("Direct Reply Test")
                        .addAction(action)
//                        .setCustomContentView(remoteViews)
//                        .setStyle(new Notification.DecoratedCustomViewStyle())
                        .setStyle(new Notification.MessagingStyle("Me")
                                .setConversationTitle("Team lunch")
                                .addMessage("Hi", /*timestamp*/1, null) // Pass in null for user.
                                .addMessage("What's up?", /*timestamp*/2, "Coworker")
                                .addMessage("Not much", /*timestamp*/3, null)
                                .addMessage("How about lunch?", /*timestamp*/4, "Coworker"))
                        .build();

// Issue the notification
//        NotificationManager notificationManager =
//                NotificationManager.from(this);
//        notificationManager.notify(notificationId, notification);
        mNewDirectReplyNotificationId = getNewNotificationId();
        mNotificationManager.notify(mNewDirectReplyNotificationId,notification);
    }

    private void addNotificationAndUpdateSummaries() {

        NotificationCompat.Builder builder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("通知测试")
                .setContentText("这是一个通知")
                .setAutoCancel(true)
//                .setDeleteIntent(mDeletePendingIntent)
                .setGroup(NOTIFICATION_GROUP);

        Notification notification = builder.build();
        mNotificationManager.notify(getNewNotificationId(),notification);

        undateNotificationSummary();
    }

    private void undateNotificationSummary() {
        StatusBarNotification[] activeNotifications = mNotificationManager.getActiveNotifications();

        int numberOfNotifications = activeNotifications.length;

        for (StatusBarNotification notification : activeNotifications){
            if(notification.getId() == NOTIFICATION_GROUP_SUMMARY_ID){
                numberOfNotifications--;
                break;
            }
        }

        if(numberOfNotifications > 1) {
            String notificationContent = getString(R.string.notification_summary_content,""+numberOfNotifications);
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText(notificationContent))
//                    .setStyle(new DecoratedCustomViewStyle())
                    .setGroup(NOTIFICATION_GROUP).setGroupSummary(true);
            Notification notification = builder.build();
            mNotificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID,notification);
        } else {
            mNotificationManager.cancel(NOTIFICATION_GROUP_SUMMARY_ID);
        }
    }

    private int getNewNotificationId() {
        int notificatinId = sNotificationId++;
        if(notificatinId == NOTIFICATION_GROUP_SUMMARY_ID) {
            notificatinId = sNotificationId++;
        }
        return notificatinId;
    }


// Obtain the intent that started this activity by calling
// Activity.getIntent() and pass it into this method to
// get the associated string.
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}

