package com.example.laundrymanagementghy.LaundryActivity.Updating;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.example.laundrymanagementghy.Bmodel.CodemendGetDataItem;
import com.example.laundrymanagementghy.R;
//import com.example.laundrymanagementghy.model.GetStockView;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class BedrollStockViewItemActivity extends AppCompatActivity {
    private final static String STOCKDATA_VIEW = "http://lmsguwahati.projectrailway.in/api/get_bedroll_condemnation_Items";
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
        setContentView(R.layout.activity_bedroll_stock_view_item);
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StockAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(BedrollStockViewItemActivity.this)) {
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
            jsonObject.put("row_id", id);
            srl.setRefreshing(true);
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
                            CodemendGetDataItem getStockView = new Gson().fromJson(response.toString(), CodemendGetDataItem.class);
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
        private ArrayList<CodemendGetDataItem.itemsData> list;

        public StockAdapter(ArrayList<CodemendGetDataItem.itemsData> mList) {
            this.list = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_bedroll_view_stocking_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
            final int position = pos;
            holder.tv_index_number.setText((position + 1) + "");
            holder.tv_item.setText(list.get(position).mItemName);
            holder.tv_qty.setText(list.get(position).mQuantity);
            holder.tv_refer_no.setText(list.get(position).mReason);
//            holder.tv_rate.setText(list.get(position).mPrice_rate);

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index_number,tv_item,tv_qty,tv_refer_no;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index_number = (TextView) itemView.findViewById(R.id.tv_index_number);
                tv_item = (TextView) itemView.findViewById(R.id.tv_item);
                tv_qty = (TextView) itemView.findViewById(R.id.tv_qty);
                tv_refer_no = (TextView) itemView.findViewById(R.id.tv_reason);
//                tv_rate = (TextView) itemView.findViewById(R.id.tv_rate);

            }
        }
    }


}