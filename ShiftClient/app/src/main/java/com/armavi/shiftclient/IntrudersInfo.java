package com.armavi.shiftclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.armavi.shift.ui.model.URL_Fractor;
import com.armavi.shiftclient.adapter.IntruderAdapter;
import com.armavi.shiftclient.model.Intruder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IntrudersInfo extends AppCompatActivity {

    //Url
    URL_Fractor url_fractor;
    String urlFull;
    ///
    SharedPreferences prefIPs;
    String PrefIPsDetectionTemp;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Intruder> intruderList;
    private RecyclerView.Adapter adapter;

    RecyclerView mainIntruderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruder_info);

        mainIntruderList = (RecyclerView) findViewById(R.id.main_intruder_list);

        intruderList = new ArrayList<>();
        adapter = new IntruderAdapter(getApplicationContext(),intruderList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        dividerItemDecoration = new DividerItemDecoration(mainIntruderList.getContext(), linearLayoutManager.getOrientation());



        mainIntruderList.setHasFixedSize(true);
        mainIntruderList.setLayoutManager(linearLayoutManager);
//        mainIntruderList.addItemDecoration(dividerItemDecoration);
        mainIntruderList.setAdapter(adapter);


        getIntruderInfo();

    }

    private void getIntruderInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //Sharedpref
        prefIPs = getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        PrefIPsDetectionTemp = prefIPs.getString("ip_pref_database", "");

        //Url generation
        url_fractor = new URL_Fractor();
        urlFull = url_fractor.getHttp_()
                +PrefIPsDetectionTemp+url_fractor.getBaseUri()
                +url_fractor.getAllIntruderInfo();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlFull, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Intruder intruder = new Intruder();
                            intruder.setTime(jsonObject.getString("time"));
                            intruder.setDate(jsonObject.getString("date"));
                            intruder.setImage(jsonObject.getString("image"));
                            intruder.setId(jsonObject.getString("id"));
                            intruderList.add(intruder);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}