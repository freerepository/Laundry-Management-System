package com.example.laundrymanagementghy.DeportActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.BufferReceivedModel;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BufferReceiptFromLaundryActivity extends AppCompatActivity {
    private final static String get_Buffer_stock_API = "http://lmskyq.projectrailway.in/api/getbufferStockData";
    private final static String VerifyStatus = "http://lmskyq.projectrailway.in/api/VerifyStatus";

    ImageView v_back;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    EditText et_dateFrom, et_dateTo;
    SwipeRefreshLayout srl;
    String depot_code = "";
    AlertDialog dialog;
    BufferAapter testAapter;
    private ArrayList<BufferReceivedModel.ReceivedItem> stockDataItemList = new ArrayList<>();
    UserDataModel userdataModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_receipt_from_laundry);

        depot_code = getIntent().getStringExtra("deport_code");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeViews();
        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES)
        {
            et_dateFrom.setHintTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
            et_dateTo.setHintTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }else{
            et_dateFrom.setHintTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
            et_dateTo.setHintTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
        }
        setupDatePickers(uiModeManager);
        setupRecyclerView();
        setupSwipeRefresh();
        GetBufferList();
    }
    private void initializeViews() {
        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);

        v_back = findViewById(R.id.v_back);
        tv_empty_data = findViewById(R.id.tv_empty_data);


        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());

        et_dateFrom.addTextChangedListener(new BufferReceiptFromLaundryActivity.TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });

        et_dateTo.addTextChangedListener(new BufferReceiptFromLaundryActivity.TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });



    }

    private void setupDatePickers(UiModeManager uiModeManager) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        final DatePickerDialog.OnDateSetListener journeyDateFrom = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateFrom.setText(dateFormat.format(calendar.getTime()));
            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                et_dateFrom.setTextColor(getResources().getColor(R.color.whiteTextColor));
                et_dateTo.setTextColor(getResources().getColor(R.color.whiteTextColor));

            } else {
                et_dateTo.setTextColor(getResources().getColor(R.color.black));
                et_dateFrom.setTextColor(getResources().getColor(R.color.black));
            }
        };

        final DatePickerDialog.OnDateSetListener journeyDateTo = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateTo.setText(dateFormat.format(calendar.getTime()));
        };

        et_dateFrom.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(BufferReceiptFromLaundryActivity.this, journeyDateFrom, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(BufferReceiptFromLaundryActivity.this, journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.buffer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(BufferReceiptFromLaundryActivity.this)) {
                GetBufferList();
            } else {
                srl.setRefreshing(false);
            }
        });
    }

    private void filterBedrollStock() {
        String fromDateString = et_dateFrom.getText().toString();
        String toDateString = et_dateTo.getText().toString();

        if (!TextUtils.isEmpty(fromDateString) && !TextUtils.isEmpty(toDateString)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fromDate = dateFormat.parse(fromDateString);
                Date toDate = dateFormat.parse(toDateString);

                ArrayList<BufferReceivedModel.ReceivedItem> filteredList = new ArrayList<>();

                for (BufferReceivedModel.ReceivedItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mSubmission_date);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

             if (testAapter!=null){
                 testAapter.filterList(filteredList);
             }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (testAapter!=null){
                testAapter.resetFilter();
            }
        }
    }

    private void GetBufferList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_id", userdataModel.mUserItems.get(0).mLogin_id);
            srl.setRefreshing(true); // Start refreshing animation
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_Buffer_stock_API,
                response -> {
                    Log.d("buffer", "Response: " + response); // Log the response
                    try {
                        BufferReceivedModel bufferReceivedModel = new Gson().fromJson(response, BufferReceivedModel.class);

                        // Update data list and notify adapter
                        stockDataItemList.clear();
                        stockDataItemList.addAll(bufferReceivedModel.mReceivedList);

                        // Initialize adapter if not already initialized
                        if (testAapter == null) {
                            testAapter = new BufferAapter(stockDataItemList);
                            recyclerView.setAdapter(testAapter);
                        } else {
                            testAapter.notifyDataSetChanged();
                        }

                        // Check and update visibility based on data availability
                        checkEmptyData();
                    } catch (Exception e) {
                        Log.e("buffer", "Parsing error: " + e.getMessage(), e);
                    } finally {
                        srl.setRefreshing(false); // Stop refreshing animation
                    }
                },
                error -> {
                    Log.e("buffer", "Error Response: " + error.getMessage(), error);
                    srl.setRefreshing(false); // Stop refreshing animation
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
                    Log.e("buffer", "Encoding error: " + uee.getMessage(), uee);
                    return null;
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void checkEmptyData() {
        if (stockDataItemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tv_empty_data.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_empty_data.setVisibility(View.GONE);
        }
    }

    public class BufferAapter extends RecyclerView.Adapter<BufferAapter.ViewHolder> {
        private ArrayList<BufferReceivedModel.ReceivedItem> mList;
        private ArrayList<BufferReceivedModel.ReceivedItem> mListFull;

        public BufferAapter(ArrayList<BufferReceivedModel.ReceivedItem> mList) {
            this.mList = new ArrayList<>(mList);
            this.mListFull = new ArrayList<>(mList);
        }

        @NonNull
        @Override
        public BufferAapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buffer_received_laundry, parent, false);
            return new BufferAapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BufferAapter.ViewHolder holder, int position) {
            BufferReceivedModel.ReceivedItem item = mList.get(position);

            holder.tv_index.setText(String.valueOf(position + 1));
            holder.tv1.setText(mList.get(position).mSubmission_date);
            holder.tv2.setText(mList.get(position).mLaundry_name);
            holder.tv3.setText(mList.get(position).mStatus);
           // holder.tv4.setText(mList.get(position).mBed_sheet);



            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewAlertDialog();
                }

                private void viewAlertDialog() {
                    final Dialog dialog = new Dialog(BufferReceiptFromLaundryActivity.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_buffer_laundry);
                    final TextView tv_date = dialog.findViewById(R.id.tv_date);
                    final TextView tv_status = dialog.findViewById(R.id.tv_status);
                    final TextView tv_bed_sheet = dialog.findViewById(R.id.tv_bed_sheet);
                    final TextView tv_pillow = dialog.findViewById(R.id.tv_pillow);
                    final TextView tv_pillow_cover = dialog.findViewById(R.id.tv_pillow_cover);
                    final TextView tv_blanket = dialog.findViewById(R.id.tv_blanket);
                    final TextView tv_blanket_cover = dialog.findViewById(R.id.tv_blanket_cover);
                    final TextView tv_hand_towel = dialog.findViewById(R.id.tv_hand_towel);
                    final TextView et_reason = dialog.findViewById(R.id.et_reason);

                    tv_date.setText(mList.get(position).mSubmission_date);
                    tv_status.setText(mList.get(position).mStatus);
                    tv_bed_sheet.setText(mList.get(position).mBed_sheet);
                    tv_pillow.setText(mList.get(position).mPillow);
                    tv_pillow_cover.setText(mList.get(position).mPillow_cover);
                    tv_blanket.setText(mList.get(position).mBlanket);
                    tv_blanket_cover.setText(mList.get(position).mBlanket_cover);
                    tv_hand_towel.setText(mList.get(position).mHand_towel);
                    et_reason.setText(mList.get(position).mReason);


                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(et_reason.getText().toString())) {
                                Toast.makeText(BufferReceiptFromLaundryActivity.this, "remark", Toast.LENGTH_SHORT).show();
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
            if (mList.get(position).mStatus.equals("Verified")){
                holder.btn_verify.setBackground(getDrawable(R.drawable.btn4gray));
                holder.btn_verify.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
            }else{
                holder.btn_verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateDeliveryAlertDialog();

                    }
                    private void UpdateDeliveryAlertDialog() {
                        final Dialog dialog = new Dialog(BufferReceiptFromLaundryActivity.this, R.style.Dialog);
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
                                    OkDataSave(dialog,mList.get(position).mSupply_id);
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
                    private void OkDataSave(Dialog dialog, String row_id) {
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("row_id", row_id);
                            jsonObject.put("table_name", "lms_save_buffer_stock");



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String requestBody = jsonObject.toString();
                        Log.e("reqbody", requestBody);
                        showLoading("Please wait...");

                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                VerifyStatus, new Response.Listener<String>() {
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
                        RequestQueue requestQueue = Volley.newRequestQueue(BufferReceiptFromLaundryActivity.this);
                        requestQueue.add(stringRequest);
                    }
                });
            }
            checkEmptyData();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void filterList(ArrayList<BufferReceivedModel.ReceivedItem> filteredList) {
            mList.clear();
            mList.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void resetFilter() {
            mList.clear();
            mList.addAll(mListFull);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index,tv1, tv2,tv3,iv_view;
            Button btn_verify;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = (TextView) itemView.findViewById(R.id.tv_serial_no);
                tv1 = (TextView) itemView.findViewById(R.id.tv_date);
                tv2 = (TextView) itemView.findViewById(R.id.tv_laundry);
                tv3 = (TextView) itemView.findViewById(R.id.tv_status);
                iv_view = (TextView) itemView.findViewById(R.id.tv_view);
                btn_verify = (Button) itemView.findViewById(R.id.btn_verify);

            }
        }
    }

    // Helper class to simplify TextWatcher implementation
    public abstract class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed
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
        final Dialog dialog = new Dialog(BufferReceiptFromLaundryActivity.this);
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
                Intent intent = new Intent(BufferReceiptFromLaundryActivity.this, BufferReceiptFromLaundryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }



}