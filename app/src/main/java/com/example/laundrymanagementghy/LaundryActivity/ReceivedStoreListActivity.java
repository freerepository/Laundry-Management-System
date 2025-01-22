package com.example.laundrymanagementghy.LaundryActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.Bmodel.GetReceiveddailogStoreList;
import com.example.laundrymanagementghy.Bmodel.ReceivedStoreAdapter;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ReceivedStoreListActivity extends AppCompatActivity {
    private final static String received_Api = "http://lmskyq.projectrailway.in/Api/get_received_CBSItems";
    RecyclerView recyclerView1;
    SwipeRefreshLayout srl1;
    String  deport_code="" ,id;
    TextView iv_close;
    ReceivedStoreAdapter storeAdapter;
    AlertDialog dialog;
    UserDataModel userdataModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_store_list);

        id = getIntent().getStringExtra("id");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_close=findViewById(R.id.v_negative);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView1 = findViewById(R.id.view_received_store);
        SwipeRefreshLayout srl1 = findViewById(R.id.srl1);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(layoutManager);
        storeAdapter = new ReceivedStoreAdapter(new ArrayList<>());
        recyclerView1.setAdapter(storeAdapter);
        storeAdapter.notifyDataSetChanged();

        srl1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl1.setRefreshing(true);
                if (O.checkNetwork(ReceivedStoreListActivity.this)) {
                    ReceivedStoreList();
                } else {
                    srl1.setRefreshing(false);
                }
            }
        });
        ReceivedStoreList();


    }

    private void ReceivedStoreList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tans_id", id);
            jsonObject.put("table_name","lms_save_CBSItems");
            jsonObject.put("tans_type","depot");


        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, received_Api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // srl1.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            final RecyclerView recyclerView1 = findViewById(R.id.view_received_store);

                            GetReceiveddailogStoreList mStore = new Gson().fromJson(response.toString(), GetReceiveddailogStoreList.class);
                            recyclerView1.setAdapter(new ReceivedStoreAdapter(mStore.mStoreData));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }


            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedStoreListActivity.this);
        requestQueue.add(stringRequest);

    }


    }