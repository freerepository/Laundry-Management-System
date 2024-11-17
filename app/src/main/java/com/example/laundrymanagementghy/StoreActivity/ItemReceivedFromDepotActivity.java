package com.example.laundrymanagementghy.StoreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.laundrymanagementghy.Bmodel.GetSentcbsStoreList;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QueStoreModel;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ItemReceivedFromDepotActivity extends AppCompatActivity {
    private final static String get_SentCbstostore_API = "http://lmsguwahati.projectrailway.in/Api/get_SentCbstostoreData";


    ImageView iv_backView,iv_calender;
    EditText et_date;
    Button submit;
    Spinner sp_depot;
    ItemGivenDepotadapter storeAdapter;
    RecyclerView recyclerView;
    UserDataModel userdataModel;
    String requestBody;
    String message,id;
    QueStoreModel queStoreModel=null;
    SwipeRefreshLayout srl;
    public JSONArray questionArray;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_received_from_depot);
        id = getIntent().getStringExtra("id");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        srl=findViewById(R.id.srl);
        recyclerView=findViewById(R.id.rv);
        et_date=findViewById(R.id.et_select_date);
        iv_calender=findViewById(R.id.iv_calender);



        iv_backView=findViewById(R.id.iv_backView);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        storeAdapter = new ItemGivenDepotadapter(new ArrayList<>());
        recyclerView.setAdapter(storeAdapter);
        storeAdapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(ItemReceivedFromDepotActivity.this)) {
                    getStorList();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });
        getStorList();


    }


    private void getStorList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("store_id", userdataModel.mUserItems.get(0).mLogin_id);
        } catch (JSONException e) {

        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_SentCbstostore_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            GetSentcbsStoreList getstoreList = new Gson().fromJson(response.toString(), GetSentcbsStoreList.class);
                            recyclerView.setAdapter(new ItemGivenDepotadapter(getstoreList.mList));

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



    public class ItemGivenDepotadapter extends RecyclerView.Adapter<ItemReceivedFromDepotActivity.ViewHolder> {
        private ArrayList<GetSentcbsStoreList.mStoreItem> list;

        public ItemGivenDepotadapter(ArrayList<GetSentcbsStoreList.mStoreItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ItemReceivedFromDepotActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_item_given_from_depot, parent, false);
             ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemReceivedFromDepotActivity.ViewHolder holder, final int pos) {
            final int position=pos;
            holder.tv_serial_no.setText((position + 1) + "");
            holder.tv.setText(list.get(position).mSubmission_date);
            holder.tv1.setText(list.get(position).mDepot_name);
            holder.btn_delivery_status.setText(list.get(position).mDelivery_status);
            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ItemReceivedFromDepotActivity.this, ItemReceivedFromDepotListActivity.class);
                    intent.putExtra("id",  list.get(position).mId);
                    startActivity(intent);
                }
            });


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_serial_no,tv, tv1;
        ImageView iv_edit, iv_view;
        Button btn_delivery_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_serial_no = (TextView) itemView.findViewById(R.id.tv_serial_no);
            tv = (TextView) itemView.findViewById(R.id.tv_date);
            tv1 = (TextView) itemView.findViewById(R.id.tv_store);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            iv_view = (ImageView) itemView.findViewById(R.id.iv_view_scbs);
            btn_delivery_status = (Button) itemView.findViewById(R.id.btn_status);
        }
    }
    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }


}
