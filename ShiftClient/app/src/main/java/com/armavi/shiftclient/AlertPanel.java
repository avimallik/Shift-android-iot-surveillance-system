package com.armavi.shiftclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class AlertPanel extends AppCompatActivity {

    SharedPreferences prefIPs;
    String PrefIPsDetectionTemp;

    URL_Fractor url_fractor;
    String urlBase, urlFull, urlImageFile;

    //Alarm
    MediaPlayer alertDetection;
    FloatingActionButton stopAlarm;
    ImageView intruderFace;
    TextView detectionTime, detectionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        stopAlarm = (FloatingActionButton) findViewById(R.id.stop_alert_btn);
        intruderFace = (ImageView) findViewById(R.id.intruder_face);
        detectionDate = (TextView) findViewById(R.id.detection_date);
        detectionTime = (TextView) findViewById(R.id.detction_time);

        //Test IP//
        url_fractor = new URL_Fractor();
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        PrefIPsDetectionTemp = prefIPs.getString("ip_pref_database", "");

        //Alert
        alertDetection = MediaPlayer.create(getApplicationContext(), R.raw.intruder_v_2);
        alertDetection.start();
        alertDetection.setLooping(true);

        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDetection.pause();
//                Toast.makeText(getApplicationContext(), url_fractor.getHttp_()
//                        +PrefIPsDetectionTemp
//                        +url_fractor.getBaseUri()
//                        +url_fractor.getIntruderAsset(), Toast.LENGTH_SHORT).show();
            }
        });

        detecTionInfo();

    }

    private void detecTionInfo(){
        //URL
        url_fractor = new URL_Fractor();

        urlFull = url_fractor.getHttp_()
                +PrefIPsDetectionTemp
                +url_fractor.getBaseUri()
                +url_fractor.getIntruderAsset();

        urlBase = url_fractor.getHttp_()
                +PrefIPsDetectionTemp
                +url_fractor.getBaseUri();

        urlImageFile = url_fractor.getImageAsset();
        /////

        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        PrefIPsDetectionTemp = prefIPs.getString("ip_pref_database", "");
        /////
//        Log.e("TAG", url_fractor.getHttp_()+PrefIPsDetectionTemp);
        RequestQueue queue = Volley.newRequestQueue(AlertPanel.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                urlFull,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //JSON object
                    String intruderFaceFile = response.getString("image");
                    String detectionTimeObj = response.getString("time");
                    String detectionDateObj = response.getString("date");

                    Picasso.with(getApplicationContext()).load(urlBase+urlImageFile+intruderFaceFile).into(intruderFace);
                    detectionDate.setText(detectionDateObj);
                    detectionTime.setText(detectionTimeObj);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Log.e("TAG", String.valueOf(error));
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                    Log.e("TAG", String.valueOf(error));
                }
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void onBackPressed() {
        alertDetection.pause();
        finish();
    }


}