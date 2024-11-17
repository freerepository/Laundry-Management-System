package com.example.laundrymanagementghy.DeportActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.GetTestCheckList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TestCheckDepotActivity extends AppCompatActivity {
    private final static String get_Testcheck_API = "http://lmsguwahati.projectrailway.in/Api/get_testcheck";
    private final static String update_testcheck_status = "http://lmsguwahati.projectrailway.in/Api/update_testcheck_status";
    ImageView v_add_test_check;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    String  depot_code="";
    AlertDialog dialog;
    TestAapter testAapter;
    UserDataModel userdataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_check_depot);
        depot_code = getIntent().getStringExtra("deport_code");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.view_test_check);
        tv_empty_data=findViewById(R.id.tv_empty_data);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        testAapter = new TestAapter(new ArrayList<>());
        recyclerView.setAdapter(testAapter);
        testAapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(TestCheckDepotActivity.this)) {
                    GetTestCheckList();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });
        GetTestCheckList();

        v_add_test_check = findViewById(R.id.v_add_test_check);
        v_add_test_check.findViewById(R.id.v_add_test_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestCheckDepotActivity.this, TestCheckActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }



    private void GetTestCheckList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code",userdataModel.mUserItems.get(0).mDepot_code);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_Testcheck_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            GetTestCheckList testCheckList = new Gson().fromJson(response.toString(), GetTestCheckList.class);
                            recyclerView.setAdapter(new TestAapter(testCheckList.mTestCheckList));

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

    public class TestAapter extends RecyclerView.Adapter<TestCheckDepotActivity.ViewHolder> {
        private ArrayList<GetTestCheckList.mGetTestItem> mList;

        public TestAapter(ArrayList<GetTestCheckList.mGetTestItem> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public TestCheckDepotActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_test_check_laundry_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TestCheckDepotActivity.ViewHolder holder, final int pos) {
            final int position=pos;
            holder.tv_index.setText((position+1)+"");
            holder.tv1.setText(mList.get(position).mCheck_date);
            holder.tv2.setText(mList.get(position).mTrain_no);
            holder.btn_status.setText(mList.get(position).mDelivery_status);

            if (mList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }
            holder.btn_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateDeliveryAlertDialog();
                }

                private void UpdateDeliveryAlertDialog() {
                    final Dialog dialog = new Dialog(TestCheckDepotActivity.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_update_delivery);
                    final TextView tv_update = dialog.findViewById(R.id.tv_update);



                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                OkDataSave(dialog,mList.get(position).mId);
                            } catch (Exception e) {
                                throw new RuntimeException(e);

                            }
                        }

                    });

                    dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                }
                private void OkDataSave(Dialog dialog, String list_id) {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("list_id", list_id);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonObject.toString();
                    Log.e("reqbody", requestBody);
                    showLoading("Please wait...");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            update_testcheck_status, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideLoading();
                            try {
                                Log.e("response", response);

                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (jsonResponse != null && jsonResponse.has("message")) {
                                    String message = jsonResponse.getString("message");

                                    showConfirmationDialog(message);
                                } else {
                                    showConfirmationDialog(response);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoading();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws com.android.volley.AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                return null;
                            }
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(TestCheckDepotActivity.this);
                    requestQueue.add(stringRequest);
                }
            });

            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(TestCheckDepotActivity.this, TestCheckDepotUpdateActivity.class);
                    intent.putExtra("id",  mList.get(position).mId);
                    startActivity(intent);

                }
            });
        }
        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index,tv1, tv2;
        ImageView iv_edit,iv_view;
        Button btn_status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_serial_no);
            tv1 = (TextView) itemView.findViewById(R.id.tv_date);
            tv2 = (TextView) itemView.findViewById(R.id.tv_train);
            btn_status = (Button) itemView.findViewById(R.id.btn_status);
            iv_view = (ImageView) itemView.findViewById(R.id.iv_view);

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
        final Dialog dialog = new Dialog(TestCheckDepotActivity.this);
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
                Intent intent = new Intent(TestCheckDepotActivity.this, TestCheckDepotActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}