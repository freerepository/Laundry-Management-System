package com.example.laundrymanagementghy.DeportActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.laundrymanagementghy.Amodel.ReceivedLaundryList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.example.laundrymanagementghy.util.PreferenceUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class ReceivedFronLaundry extends AppCompatActivity {
    private final static String Received_Laundry_List = "http://lmskyq.projectrailway.in/Api/depot_received_from_laundry";
    private final static String update_delivery = "http://lmskyq.projectrailway.in/Api/depot_update_delivery_status";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    String deport_code = "";
    TextView tv_empty_data;
    AlertDialog dialog;
    final Calendar myCalendar = Calendar.getInstance();
    ReceivedLaundryAdapter adapter;

    UserDataModel userdataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.received_fron_laundry);
        deport_code = getIntent().getStringExtra("deport_code");
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
        recyclerView = (RecyclerView) findViewById(R.id.view_received);
        tv_empty_data=findViewById(R.id.tv_empty_data);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReceivedLaundryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if(O.checkNetwork(ReceivedFronLaundry.this)) {
                SentLaundryList();
            }else{
                srl.setRefreshing(false);
            }
        });
        SentLaundryList();
    }

    private void SentLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", PreferenceUtil.getDepot(ReceivedFronLaundry.this));
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Received_Laundry_List,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            ReceivedLaundryList mReceivedList = new Gson().fromJson(response.toString(), ReceivedLaundryList.class);
                            recyclerView.setAdapter(new ReceivedLaundryAdapter(mReceivedList.mReceivedList));

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
    public class ReceivedLaundryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<ReceivedLaundryList.ReceivedItem> list;

        public ReceivedLaundryAdapter(ArrayList<ReceivedLaundryList.ReceivedItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_receiced_laundry_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            final int position=pos;
            holder.tv_index.setText((position+1)+"");
            holder.tv.setText(list.get(position).mSupply_date);
            holder.tv1.setText(list.get(position).mLaundry_area);
            holder.tv2.setText(list.get(position).mPacket_count);
            holder.tv3.setText(list.get(position).mTrain_no);
            holder.tv4.setText(list.get(position).mCoach);
            holder.tv5.setText(list.get(position).mNo_of_bag);
            holder.tv6.setText(list.get(position).mBs);
            holder.tv7.setText(list.get(position).mPc);
            holder.tv8.setText(list.get(position).mBath_towel);
            holder.tv9.setText(list.get(position).mBlanket_cover);
            holder.tv10.setText(list.get(position).mFt);
            holder.tv11.setText(list.get(position).mNo_blanket);
            holder.btn_status.setText(list.get(position).mDelivery_status);

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }
            holder.iv_qr_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scanCodeAlertDialog();
                }

                private void scanCodeAlertDialog() {

                    final Dialog dialog = new Dialog(ReceivedFronLaundry.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_qr_code_delivery);
                    final ImageView iv_qr_codes = dialog.findViewById(R.id.iv_qr_code);

                    //Picasso.get().load(list.get(position).mQr_url).into(iv_qr_codes);
                    Picasso.with(ReceivedFronLaundry.this)
                            .load(list.get(position).mQr_url)
                            .noFade().resize(230,230)
                            .centerCrop().into(iv_qr_codes);

                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (TextUtils.isEmpty(et_date.getText().toString())) {
//                                Toast.makeText(LaundryReceived.this, "Select Date", Toast.LENGTH_SHORT).show();
//                            } else {
                            try {

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
            });
            holder.btn_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateDeliveryAlertDialog();

                }
                private void UpdateDeliveryAlertDialog() {
                    final Dialog dialog = new Dialog(ReceivedFronLaundry.this, R.style.Dialog);
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
                                OkDataSave(dialog,list.get(position).mSupply_id);
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
                private void OkDataSave(Dialog dialog, String item_id) {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("item_id", item_id);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonObject.toString();
                    Log.e("reqbody", requestBody);
                    showLoading("Please wait...");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            update_delivery, new Response.Listener<String>() {
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
                    RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFronLaundry.this);
                    requestQueue.add(stringRequest);
                }
            });


            holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogoutAlertDialog();
                }

                private void showLogoutAlertDialog() {
                    final Dialog dialog = new Dialog(ReceivedFronLaundry.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_received);
                    final EditText et_remark = dialog.findViewById(R.id.et_remark);

                    et_remark.setText(list.get(position).mRemark);



                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(et_remark.getText().toString())) {
                                Toast.makeText(ReceivedFronLaundry.this, "remark", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    //EditDataSave(list.get(position).mSupply_id);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }


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


            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv, tv1, tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11;
        ImageView iv_edit,iv_qr_code;
        Button btn_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index=(TextView)itemView.findViewById(R.id.tv_serial_no);
            tv = (TextView) itemView.findViewById(R.id.tv_date);
            tv1 = (TextView) itemView.findViewById(R.id.tv_laundry_area);
            tv2 = (TextView) itemView.findViewById(R.id.tv_packet);
            tv3 = (TextView) itemView.findViewById(R.id.tv_train);
            tv4 = (TextView) itemView.findViewById(R.id.tv_coach);
            tv5 = (TextView) itemView.findViewById(R.id.tv_no_of_bag);
            tv6 = (TextView) itemView.findViewById(R.id.tv_bed_sheet);
            tv7 = (TextView) itemView.findViewById(R.id.tv_pillow_cover);
            tv8 = (TextView) itemView.findViewById(R.id.tv_bath_towel);
            tv9 = (TextView) itemView.findViewById(R.id.tv_blanket_cover);
            tv10 = (TextView) itemView.findViewById(R.id.tv_face_towel);
            tv11 = (TextView) itemView.findViewById(R.id.tv_blanket);
            btn_status = (Button) itemView.findViewById(R.id.tv_status);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            iv_qr_code = (ImageView) itemView.findViewById(R.id.iv_qr_code);

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
        final Dialog dialog = new Dialog(ReceivedFronLaundry.this);
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
                Intent intent = new Intent(ReceivedFronLaundry.this, ReceivedFronLaundry.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}
