package com.armavi.shiftclient.adapter;

import static android.content.Context.MODE_PRIVATE;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.armavi.shift.ui.model.URL_Fractor;
import com.armavi.shiftclient.IntrudersInfo;
import com.armavi.shiftclient.R;
import com.armavi.shiftclient.model.Intruder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntruderAdapter extends RecyclerView.Adapter<IntruderAdapter.ViewHolder>{

    String KEY_ID = "id",
            KEY_IMAGE_FILE_NAME = "imageFileName";

    SharedPreferences prefIPs;
    String PrefIPsDetectionTemp;

    URL_Fractor url_fractor = new URL_Fractor();

    private Context context;
    private List<Intruder> list;

    public IntruderAdapter(Context context, List<Intruder> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.intruder_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        prefIPs = context.getSharedPreferences("IP_Prefs", MODE_PRIVATE);
        PrefIPsDetectionTemp = prefIPs.getString("ip_pref_database", "");

        Intruder intruder = list.get(position);
        holder.mainDetectTime.setText(intruder.getTime());
        holder.mainDetectDate.setText(intruder.getDate());
        Picasso.with(context).load(url_fractor.getHttp_()
                +PrefIPsDetectionTemp
                +url_fractor.getBaseUri()
                +url_fractor.getImageAsset()
                +intruder.getImage()).into(holder.mainDetectImage);

        holder.containerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, intruder.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.intruderDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteIntruder deleteIntruder = new DeleteIntruder(intruder.getId(),intruder.getImage());
//                Toast.makeText(context, deleteIntruder.intruderId, Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, deleteIntruder.intruderImageFile, Toast.LENGTH_SHORT).show();
                deleteIntruder.sendAlert();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mainDetectTime, mainDetectDate;
        public ImageView mainDetectImage, intruderDeleteBtn;
        public LinearLayoutCompat containerItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mainDetectTime = itemView.findViewById(R.id.main_detect_time);
            mainDetectDate = itemView.findViewById(R.id.main_detect_date);
            mainDetectImage = itemView.findViewById(R.id.main_detect_image);
            containerItem = itemView.findViewById(R.id.container_item);
            intruderDeleteBtn = itemView.findViewById(R.id.intruder_delete_btn);

        }
    }

    public class DeleteIntruder{
        String intruderId, intruderImageFile;

        public DeleteIntruder(String intruderId, String intruderImageFile) {
            this.intruderId = intruderId;
            this.intruderImageFile = intruderImageFile;
        }
        private void sendAlert(){

            //URL Fraction
            url_fractor = new URL_Fractor();
            /////

            //Read IP from shared pref
            prefIPs = context.getSharedPreferences("IP_Prefs", MODE_PRIVATE);
            PrefIPsDetectionTemp = prefIPs.getString("ip_pref_database", "");
            //////

            String server_uri_send_alert = url_fractor.getHttp_()
                    +PrefIPsDetectionTemp
                    +url_fractor.getBaseUri()
                    +url_fractor.getDeleteIntruderData();

            final StringRequest stringRequestSendAlert = new StringRequest(Request.Method.POST,server_uri_send_alert,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Toast.makeText(context, response.toString(),Toast.LENGTH_SHORT).show();
                            if(response.toString().contains("1")){
                                Toast.makeText(context, "Intruder data successfully deleted!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, IntrudersInfo.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }else if(response.toString().contains("0")){
                                Toast.makeText(context, "Something goes wrong!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(KEY_ID, intruderId);
                    params.put(KEY_IMAGE_FILE_NAME, intruderImageFile);
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
            RequestQueue requestQueueSendAlert = Volley.newRequestQueue(context);
            requestQueueSendAlert.add(stringRequestSendAlert);
        }
    }



}
