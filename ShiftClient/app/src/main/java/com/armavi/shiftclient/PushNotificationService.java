package com.armavi.shiftclient;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.armavi.shiftclient.appID.AppID;
import com.armavi.shiftclient.messaginglink.DataFlow;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {

    //Data flow
    DataFlow dataFlow;
    //

    String NOTIFICATION_CHANNEL_ID = "max";
    Notification mNotification;
    int importance = 0;
    private LocalBroadcastManager broadcaster;

    //////////////Shared Pref////////////////////////
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String user_id_guardian, user_id_tutor;
    ////////////////////////////////////////////////
    String topic_temp_val = "application", topic_job_post_val = "job_post";

    //App Unique ID
    AppID appID = new AppID();

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getFrom().equals("/topics/" + "detect")){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(remoteMessage.getData().get("content-text").contains(appID.getUniqueID())){

                        Toast.makeText(getApplicationContext(), "Detected !", Toast.LENGTH_SHORT).show();

//                        dataFlow = new DataFlow();
//                        dataFlow.setFlowAppUniqueID(appID.getUniqueID());

                        Intent intent = new Intent(getApplicationContext(), AlertPanel.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
            });
        }
    }

}
