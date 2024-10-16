package com.armavi.shiftclient;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.armavi.shiftclient.messaginglink.DataFlow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMTest extends AppCompatActivity {

    Button subscribeToken;
    TextView fcmToken;

    //Data flow
    DataFlow dataFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmtest);

        subscribeToken = (Button) findViewById(R.id.subscribeToken);
        fcmToken = (TextView) findViewById(R.id.fcmToken);

        //Dataflow object call
        dataFlow = new DataFlow();
        //

        subscribeToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().subscribeToTopic("detect");
                Toast.makeText(getApplicationContext(), "Subscribed !", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}