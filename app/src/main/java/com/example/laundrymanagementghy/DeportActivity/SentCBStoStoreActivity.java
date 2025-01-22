package com.example.laundrymanagementghy.DeportActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.example.laundrymanagementghy.Bmodel.GetSentcbsStoreList;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SentCBStoStoreActivity extends AppCompatActivity {
    private final static String get_SentCbstostore_API = "http://lmskyq.projectrailway.in/Api/get_SentCbstostoreData";

    ImageView iv_addSupply;
    UserDataModel userdataModel;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    TextView tv_empty_data;
    SentCBStoStoreAdapter storeAdapter;
    AlertDialog dialog;
    String  id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_cbsto_store);
        id = getIntent().getStringExtra("id");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        iv_addSupply=findViewById(R.id.v_add_scondemned);
        findViewById(R.id.v_add_scondemned).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SentCBStoStoreActivity.this, SentCBStoStoreAddActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.view_scbs);
        tv_empty_data=findViewById(R.id.tv_empty_data);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        storeAdapter = new SentCBStoStoreAdapter(new ArrayList<>());
        recyclerView.setAdapter(storeAdapter);
        storeAdapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(SentCBStoStoreActivity.this)) {
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
            jsonObject.put("depot_id", userdataModel.mUserItems.get(0).mLogin_id);
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
                            recyclerView.setAdapter(new SentCBStoStoreAdapter(getstoreList.mList));

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

    public class SentCBStoStoreAdapter extends RecyclerView.Adapter<SentCBStoStoreActivity.ViewHolder> {
        private ArrayList<GetSentcbsStoreList.mStoreItem> list;

        public SentCBStoStoreAdapter(ArrayList<GetSentcbsStoreList.mStoreItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public SentCBStoStoreActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_sent_cbs_storelist, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SentCBStoStoreActivity.ViewHolder holder, final int pos) {
            final int position=pos;
            holder.tv_serial_no.setText((position + 1) + "");
            holder.tv.setText(list.get(position).mSubmission_date);
            holder.tv1.setText(list.get(position).mStore);
            holder.btn_delivery_status.setText(list.get(position).mDelivery_status);

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }

            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SentCBStoStoreActivity.this, SentCBStoStoreListActivity.class);
                    intent.putExtra("id",  list.get(position).mId);
                    startActivity(intent);
                }
            });

            holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SentCBStoStoreActivity.this,SentCBStoStoreUpdateActivity.class);
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
        LinearLayout ll = new LinearLayout(this);
        ll.setPadding(16, 16, 16, 16);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(llParam);

        TextView tv = new TextView(this);
        tv.setText(message0);
        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(llParam);
        tv.setPadding(8, 8, 8, 8);
        ll.addView(tv);

        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(rlParam);

        ImageView iv = new ImageView(this);
        iv.setImageDrawable(getDrawable(R.drawable.progress));
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv.setLayoutParams(rlParam);
        rl.addView(iv);
        iv.animate().setInterpolator(new DecelerateInterpolator()).rotation(-3600).setDuration(20000).start();

        ImageView iv_logo = new ImageView(this);
        iv_logo.setImageDrawable(getDrawable(R.mipmap.logo));
        iv_logo.setPadding(20, 20, 20, 20);
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv_logo.setLayoutParams(rlParam);
        rl.addView(iv_logo);
        iv_logo.animate().setInterpolator(new DecelerateInterpolator()).rotation(3600).setDuration(20000).start();

        ll.addView(rl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(ll);
        dialog = builder.create();
        dialog.show();
    }


    protected void hideLoading() {
        dialog.dismiss();
    }
    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(SentCBStoStoreActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(SentCBStoStoreActivity.this, SentCBStoStoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}