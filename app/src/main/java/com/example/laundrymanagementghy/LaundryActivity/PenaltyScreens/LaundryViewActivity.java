package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.example.laundrymanagementghy.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LaundryViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LaundryViewAdapter adapter;
    private ArrayList<PenaltyItemsViewDataModel.PenaltyItem> arrayList;
    private SwipeRefreshLayout srl;
    private TextView tv_empty_data;
    private ProgressBar progressBar;
    private String myid;
    private ImageView v_back;

    private static final String STOCKDATA_VIEW = "http://lmsguwahati.projectrailway.in/api/getPenaltyitems";  // Replace with your actual URL
//    private static final String STOCKDATA_VIEW = "http://lmsguwahati.projectrailway.in/api/getmissingPenaltyitems";  // Replace with your actual URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_view_screen);
        myid = getIntent().getStringExtra("id");
        recyclerView = findViewById(R.id.rv);
        tv_empty_data = findViewById(R.id.tv_empty_data);
        srl = findViewById(R.id.srl);
        progressBar = findViewById(R.id.progressBar);

        v_back= findViewById(R.id.v_back);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();;
            }
        });

        arrayList = new ArrayList<>();
        adapter = new LaundryViewAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Swipe Refresh Layout to reload data
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PenaltyList();
            }
        });

        // Initial load
        PenaltyList();
    }

    private void PenaltyList() {
        JSONObject jsonObject = new JSONObject();
        try {
            // Add any parameters required by your API
            jsonObject.put("row_id", myid);  // example parameter
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, STOCKDATA_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            // Parsing response into the Model Class
                            PenaltyItemsViewDataModel getStockView = new Gson().fromJson(response, PenaltyItemsViewDataModel.class);

                            if (getStockView != null && getStockView.penaltyItemsData != null && !getStockView.penaltyItemsData.isEmpty()) {
                                arrayList.clear();
                                arrayList.addAll(getStockView.penaltyItemsData);
                                adapter.notifyDataSetChanged();
                                tv_empty_data.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                tv_empty_data.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srl.setRefreshing(false);
                Log.e("Volley Error", error.toString());
                tv_empty_data.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
