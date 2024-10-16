package com.armavi.shift.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.armavi.shift.R;
import com.armavi.shift.ui.model.AppUniqueID;
import com.armavi.shift.ui.model.URL_Fractor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //URL Fraction variable
    URL_Fractor url_fractor;

    //App Unique id
    AppUniqueID appUniqueID;

    //Time and date
    TimeOfDetection timeOfDetection;
    ///////

    ////Tokens/////
    String KEY_CREDENDIAL_MEDIA = "image",
            KEY_CREDENTIAL_TYPE = "type",
            KEY_IMAGE_MAIN_PATH = "mainpath",
            KEY_IMAGE_PATH = "path",
            KEY_TIME = "time",
            KEY_DATE = "date";

    String KEY_APP_UNIQUE_ID = "appUniqueID";

    ///Path URL////////////
    String path_url = "public/asset/credential/";
//    String main_path_url = "asset/credential/";
    //////////////

//    Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST= 99;

    private ImageView imageView;
    private static final String IMAGE_DIRECTORY = "/CustomImage";
    /////////////////////////////////////

    //Sharedpref variables
    SharedPreferences prefIPs;
    String prefIPsTemp;
    String PrefIPsDetectionTemp;

    //handler
    Handler handler;
    Runnable updater;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private FloatingActionButton switchCamera;
    private FloatingActionButton capture;
    private FloatingActionButton logOut;
    private TextView pirStatusTxt, ultrasonicStatusTxt;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = true;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Nuke SSL
        NukeSSLCerts.nuke();

        //PIR Status TextView
        pirStatusTxt = (TextView) findViewById(R.id.pir_status);
        ///////////

        //Ultrasonic Status TextView
        ultrasonicStatusTxt = (TextView) findViewById(R.id.ultrasonic_status);
        ///////////

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        mCamera =  Camera.open();
        mCamera.setDisplayOrientation(90);
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (FloatingActionButton) findViewById(R.id.btnCam);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        switchCamera = (FloatingActionButton) findViewById(R.id.btnSwitch);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the number of cameras
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    //switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {

                }
            }
        });

        logOut = (FloatingActionButton) findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Read IP from shared pref
                prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
                prefIPs.edit().clear().commit();

                Intent intentFinish = new Intent(getApplicationContext(), IPSignIn.class);
                intentFinish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentFinish);
                finish();
            }
        });

        chooseCamera();
        mCamera.startPreview();

        ////////////////Captured bitmap///////////
        imageView = findViewById(R.id.img);

        //////////////////////////////////////////

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                networkTaskDetection();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
        mCamera.startPreview();
    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {

        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }
    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    void networkTaskDetection(){

        //URL
        url_fractor = new URL_Fractor();
        /////

        //Shared pref detection IP//
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        PrefIPsDetectionTemp = prefIPs.getString("ip_pref", "");
        /////
//        Log.e("TAG", url_fractor.getHttp_()+PrefIPsDetectionTemp);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url_fractor.getHttp_()+PrefIPsDetectionTemp,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String pirStatus = response.getString("pir_status");
//                    String ultrasonicStatus = response.getString("ultrasonic_status");

                    pirStatusTxt.setText("PIR : "+ pirStatus);
//                    ultrasonicStatusTxt.setText("Ultrasonic : "+ ultrasonicStatus);

                    if(pirStatus.contains("1")){
                        Toast.makeText(getApplicationContext(), "Detected", Toast.LENGTH_SHORT).show();
                        mCamera.takePicture(null, null, mPicture);
                    }else{

                    }
//                    else if(ultrasonicStatus.contains("1")){
//                        Toast.makeText(getApplicationContext(), "Detected", Toast.LENGTH_SHORT).show();
//                        mCamera.takePicture(null, null, mPicture);
//                    }else if(pirStatus.contains("1") && ultrasonicStatus.contains("1")){
//                        Toast.makeText(getApplicationContext(), "Detected", Toast.LENGTH_SHORT).show();
//                        mCamera.takePicture(null, null, mPicture);
//                    }


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


    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Intent intent = new Intent(MainActivity.this,PictureActivity.class);
//                startActivity(intent);
//                finish();
                imageView.setImageBitmap(bitmap);
                credentialInput();
                sendAlert();
                mCamera.startPreview();
            }
        };
        return picture;
    }

    private void credentialInput(){
        //URL Fraction
        url_fractor = new URL_Fractor();
        /////

        //Time and date generation//
        timeOfDetection = new TimeOfDetection();
        //////////

        //Read IP from shared pref
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        prefIPsTemp = prefIPs.getString("ip_pref_database", "");
        //////

        //Image URL processing
        final String image_temp = getStringImage(bitmap);
        String server_uri_img_update = url_fractor.getHttp_()
                +prefIPsTemp
                +url_fractor.getBaseUri()
                +url_fractor.getImageInput();

        //URL Checking
        Toast.makeText(this, server_uri_img_update, Toast.LENGTH_SHORT).show();
        //Image File name generation////
        String imgFileGeneratedString = Long.toHexString(Double.doubleToLongBits(Math.random()));
        final String path_temp = path_url+imgFileGeneratedString+".png";
        final String main_path_temp = imgFileGeneratedString+".png";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST,server_uri_img_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().contains("1")){
                            Toast.makeText(getApplicationContext(), "Image Captured",Toast.LENGTH_SHORT).show();
                        }else if(response.toString().contains("0")){
                            Toast.makeText(getApplicationContext(), "Failed to capture image",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_CREDENTIAL_TYPE, "Test");
                params.put(KEY_CREDENDIAL_MEDIA, image_temp);
                params.put(KEY_IMAGE_MAIN_PATH, main_path_temp);
                params.put(KEY_IMAGE_PATH, path_temp);
                params.put(KEY_TIME, timeOfDetection.timeTemp);
                params.put(KEY_DATE, timeOfDetection.timeDateTemp);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 10000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //////////Image String////////
    public String getStringImage(Bitmap bitmap){
        Log.i("Arm_Avi","" + bitmap);
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    /////////////////////////////

    //Send alert through Firebase Cloud Messaging
    private void sendAlert(){

        //App unique id object
        appUniqueID = new AppUniqueID();

        //URL Fraction
        url_fractor = new URL_Fractor();
        /////

        //Read IP from shared pref
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        prefIPsTemp = prefIPs.getString("ip_pref_database", "");
        //////

        String server_uri_send_alert = url_fractor.getHttp_()
                +prefIPsTemp
                +url_fractor.getBaseUri()
                +url_fractor.getAlertSender();

        final StringRequest stringRequestSendAlert = new StringRequest(Request.Method.POST,server_uri_send_alert,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response.toString(),Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_APP_UNIQUE_ID, appUniqueID.getAppUniqueID());
                return params;
            }
        };

        stringRequestSendAlert.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 10000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
        RequestQueue requestQueueSendAlert = Volley.newRequestQueue(this);
        requestQueueSendAlert.add(stringRequestSendAlert);
    }

}