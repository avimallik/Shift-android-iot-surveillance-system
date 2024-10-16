package com.armavi.shift.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.armavi.shift.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PictureActivity extends AppCompatActivity {

    //Sharedprefs
    SharedPreferences prefIPs;
    String prefIPsTemp;


    //Handler
    Handler handler = new Handler(Looper.getMainLooper());

    ////Tokens/////
    String KEY_TUTOR_ID = "user_id",
            KEY_CREDENDIAL_MEDIA = "image",
            KEY_CREDENTIAL_TYPE = "type",
            KEY_IMAGE_MAIN_PATH = "mainpath",
            KEY_IMAGE_PATH = "path";

    ///Path URL////////////
    String path_url = "public/asset/credential/";
    String main_path_url = "asset/credential/";
    //////////////

    Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST= 99;

    private ImageView imageView;
    private static final String IMAGE_DIRECTORY = "/CustomImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bitmap = MainActivity.bitmap;

        imageView = findViewById(R.id.img);
        imageView.setImageBitmap(bitmap);

        credentialInput();

    }

    private void credentialInput(){

        //Read IP from shared pref
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        prefIPsTemp = prefIPs.getString("ip_pref_database", "");

        //URL processing
        final String image_temp = getStringImage(MainActivity.bitmap);
        String server_uri_img_update = "http://"+prefIPsTemp+"/survi_test/insert_status.php";

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
//                       Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), error.toString(),Toast.LENGTH_SHORT).show();

//                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_CREDENTIAL_TYPE, "Test");
                params.put(KEY_CREDENDIAL_MEDIA, image_temp);
                params.put(KEY_IMAGE_MAIN_PATH, main_path_temp);
                params.put(KEY_IMAGE_PATH, path_temp);
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

        //finish task after some seconds
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), "Task finished", Toast.LENGTH_SHORT).show();
//                Intent pictureActivityAgain = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(pictureActivityAgain);
////                finish();
//            }
//        }, 10000);

    }

    public String getStringImage(Bitmap bitmap){
        Log.i("Arm_Avi","" + bitmap);
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

//    @Override
//    public void onBackPressed() {
//        Intent pictureActivityAgain = new Intent(this, MainActivity.class);
//        startActivity(pictureActivityAgain);
//        finish();
//
//    }
}