package com.example.laundrymanagementghy.LaundryActivity;

import android.app.UiModeManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.model.GetSoiledView;
import com.example.laundrymanagementghy.model.GetStockView;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FreshBedrollReceipViewLaundry extends AppCompatActivity {
    private final static String SOILED_VIEW = "http://lmsguwahati.projectrailway.in/api/get_laundry_details_id";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    TextView tv_tittle, tv_empty_data;
    ImageView v_back;
    String id;
    UserDataModel userdataModel;
    StockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_bedroll_receip_view_laundry);

        id = getIntent().getStringExtra("id");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        tv_empty_data = findViewById(R.id.tv_empty_data);
        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StockAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(FreshBedrollReceipViewLaundry.this)) {
                    BedStockList();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });
        BedStockList();


    }

    private void BedStockList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("list_id", id);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SOILED_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            GetSoiledView getStockView = new Gson().fromJson(response.toString(), GetSoiledView.class);
                            recyclerView.setAdapter(new StockAdapter(getStockView.mItemsData));

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
        private ArrayList<GetSoiledView.itemsData> list;
        UiModeManager uiModeManager;

        public StockAdapter(ArrayList<GetSoiledView.itemsData> mList) {
            this.list = mList;
        }

        @NonNull
        @Override
        public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_soiled_bedroll_view_stocking, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull StockAdapter.ViewHolder holder, final int pos) {
            final int position = pos;
            uiModeManager = (UiModeManager) getApplicationContext().getSystemService(UI_MODE_SERVICE);
            holder.tv1.setText(list.get(position).mDate);
            holder.tv2.setText(list.get(position).mDepot_code);
            holder.tv3.setText(list.get(position).mTrain_no);
            holder.tv4.setText(list.get(position).mCoach);
            holder.tv5.setText(list.get(position).mBs);
            holder.tv6.setText(list.get(position).mBlanket);
            holder.tv7.setText(list.get(position).mPc);
            holder.tv8.setText(list.get(position).mBath_towel);
            holder.tv9.setText(list.get(position).mBlanket_cover);
            holder.tv10.setText(list.get(position).mFt);

            holder.tv1.setEnabled(false);
            holder.tv2.setEnabled(false);
            holder.tv3.setEnabled(false);
            holder.tv4.setEnabled(false);
            holder.tv5.setEnabled(false);
            holder.tv6.setEnabled(false);
            holder.tv7.setEnabled(false);
            holder.tv8.setEnabled(false);
            holder.tv9.setEnabled(false);
            holder.tv10.setEnabled(false);



            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }

            if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES){

                holder.tv1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv8.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv9.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));



                holder.tv1_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv2_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv3_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv4_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv5_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv6_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv7_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv8_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv9_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
                holder.tv10_t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));

                holder.tv1.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv2.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv3.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv4.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv5.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv6.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv7.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv8.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv9.setBackground(getDrawable(R.drawable.shape_white));
                holder.tv10.setBackground(getDrawable(R.drawable.shape_white));
//                holder.tv11.setBackground(getDrawable(R.drawable.shape_white));

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EditText tv1, tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11;
            TextView tv1_t, tv2_t, tv3_t, tv4_t, tv5_t, tv6_t, tv7_t, tv8_t, tv9_t, tv10_t;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv1 = (EditText) itemView.findViewById(R.id.tv_date);
                tv2 = (EditText) itemView.findViewById(R.id.tv_depot);
                tv3 = (EditText) itemView.findViewById(R.id.tv_train);
                tv4 = (EditText) itemView.findViewById(R.id.tv_coach);
                tv5 = (EditText) itemView.findViewById(R.id.tv_bed_sheet);
                tv6 = (EditText) itemView.findViewById(R.id.et_blanket);
                tv7 = (EditText) itemView.findViewById(R.id.tv_pillow_cover);
                tv8 = (EditText) itemView.findViewById(R.id.tv_bath_towel);
                tv9 = (EditText) itemView.findViewById(R.id.tv_blanket_cover);
                tv10 = (EditText) itemView.findViewById(R.id.tv_face_towel);


                tv1_t = (TextView) itemView.findViewById(R.id.txt1);
                tv2_t = (TextView) itemView.findViewById(R.id.txt2);
                tv3_t = (TextView) itemView.findViewById(R.id.txt3);
                tv4_t = (TextView) itemView.findViewById(R.id.txt4);
                tv5_t = (TextView) itemView.findViewById(R.id.txt5);
                tv6_t = (TextView) itemView.findViewById(R.id.txt6);
                tv7_t = (TextView) itemView.findViewById(R.id.txt7);
                tv8_t = (TextView) itemView.findViewById(R.id.txt8);
                tv9_t = (TextView) itemView.findViewById(R.id.txt9);
                tv10_t = (TextView) itemView.findViewById(R.id.txt10);
            }
        }
    }


}