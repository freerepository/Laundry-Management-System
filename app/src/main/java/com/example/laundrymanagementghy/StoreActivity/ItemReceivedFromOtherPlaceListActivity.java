package com.example.laundrymanagementghy.StoreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ItemReceivedFromOtherPlaceListActivity extends AppCompatActivity {
    private final static String get_received_cbs_Api = "http://lmsguwahati.projectrailway.in/Api/get_received_CBSItems";

    String  id;
    TextView iv_close;
    ItemReceivedListAdapter adapter;

    UserDataModel userdataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_received_from_other_place_list);
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
        adapter = new ItemReceivedListAdapter(new ArrayList<>());
        recyclerView1.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        srl1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl1.setRefreshing(true);
                if (O.checkNetwork(ItemReceivedFromOtherPlaceListActivity.this)) {
                    SupplyCBSDepotList();
                } else {
                    srl1.setRefreshing(false);
                }
            }
        });
        SupplyCBSDepotList();


    }

    private void SupplyCBSDepotList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tans_id", id);
            jsonObject.put("table_name","lms_save_storeReceived_items");
            jsonObject.put("tans_type","otherplace");

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_received_cbs_Api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // srl1.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            final RecyclerView recyclerView1 = findViewById(R.id.view_received_store);

                            GetReceiveddailogStoreList mSupply = new Gson().fromJson(response.toString(), GetReceiveddailogStoreList.class);
                            recyclerView1.setAdapter(new ItemReceivedListAdapter(mSupply.mStoreData));


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
        RequestQueue requestQueue = Volley.newRequestQueue(ItemReceivedFromOtherPlaceListActivity.this);
        requestQueue.add(stringRequest);

    }
    public class ItemReceivedListAdapter extends RecyclerView.Adapter<ItemReceivedListAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<GetReceiveddailogStoreList.ReceivedDataStore> ListData;

        public ItemReceivedListAdapter(ArrayList<GetReceiveddailogStoreList.ReceivedDataStore> ListData) {
            this.ListData = ListData;
        }

        @NonNull
        @Override
        public ItemReceivedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_receiced_store_list, parent, false);
             ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull ItemReceivedListAdapter.ViewHolder holder, int position) {

            holder.tv_dse.setText(ListData.get(position).mItem_description);
            holder.tv_pl.setText(ListData.get(position).mPl_no);
            holder.tv_qty.setText(ListData.get(position).mQty);

        }

        @Override
        public int getItemCount() {
            return  ListData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_dse,tv_pl,tv_qty;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_dse = (TextView) itemView.findViewById(R.id.tv_dec);
                tv_pl = (TextView) itemView.findViewById(R.id.tv_pl_no);
                tv_qty = (TextView) itemView.findViewById(R.id.tv_qty);

            }
        }
    }


}