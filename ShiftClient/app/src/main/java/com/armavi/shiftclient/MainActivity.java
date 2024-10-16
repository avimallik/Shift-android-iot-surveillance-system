package com.armavi.shiftclient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.armavi.shift.ui.model.URL_Fractor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SharedPreferences prefIPs;
    String PrefIPsDetectionTemp;

    View actionView;
    ImageView logoutBtn;
    FloatingActionButton intruderDetectedBtn, stopAlertBtn, allIntruderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Custom actionbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        actionView = getSupportActionBar().getCustomView();

        //UI variables
        logoutBtn = (ImageView) actionView.findViewById(R.id.logout_btn);
        allIntruderBtn = (FloatingActionButton) findViewById(R.id.all_intruder_btn);

        //All intruder button
        allIntruderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IntrudersInfo.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
                prefIPs.edit().clear().commit();

                Intent intentFinish = new Intent(getApplicationContext(), IPSignIn.class);
                intentFinish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentFinish);
                finish();
            }
        });

    }

}