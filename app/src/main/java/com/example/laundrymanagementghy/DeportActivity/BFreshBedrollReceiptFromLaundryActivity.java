package com.example.laundrymanagementghy.DeportActivity;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.example.laundrymanagementghy.Amodel.FreshBedrollModel;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class BFreshBedrollReceiptFromLaundryActivity extends AppCompatActivity {
    private final static String BEDROLL_API = "http://lmskyq.projectrailway.in/api/fresh_bedroll_received_from_laundry";
    private final static String VRIFY_API = "http://lmskyq.projectrailway.in/api/verify_and_train_supply";

    ImageView iv_add_supply;
    TextView tv_tittle, tv_empty_data;
    EditText et_dateFrom, et_dateTo;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;////////////
    AlertDialog dialog;
    String depot_code = "";
    final Calendar myCalendar = Calendar.getInstance();
    BedrollStockAdapter bedrollStockAdapter;
    private ArrayList<FreshBedrollModel.mFbedroll> stockDataItemList = new ArrayList<>();

    UserDataModel userdataModel;
    UserDataModel userDModel_depot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bfresh_bedroll_receipt_from_laundry);

        depot_code = getIntent().getStringExtra("model");

//        Toast.makeText(this, "depot "+depot_code, Toast.LENGTH_SHORT).show();


        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();
//        setupDatePickers();
        setupRecyclerView();
        setupSwipeRefresh();

        GetLaundryList();
    }

    private void initializeViews() {
        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);
        tv_tittle = findViewById(R.id.tv_toolbar_title);
        //                                                              Bedroll Recieved From Laundry
//        tv_tittle.setText(userdataModel.mUserItems.get(0).mHeader);
        tv_tittle.setText("Bedroll Recieved From Laundry");
        iv_add_supply = findViewById(R.id.iv_add_supply);
        tv_empty_data = findViewById(R.id.tv_empty_data);

//        iv_add_supply.setOnClickListener(v -> {
//            Intent intent = new Intent(BFreshBedrollReceiptFromLaundryActivity.this, BedrollStockingAddActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        });

        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());
    }

//    private void setupDatePickers() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//
//        final DatePickerDialog.OnDateSetListener journeyDateFrom = (view, year, monthOfYear, dayOfMonth) -> {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, monthOfYear);
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            et_dateFrom.setText(dateFormat.format(calendar.getTime()));
//        };
//
//        final DatePickerDialog.OnDateSetListener journeyDateTo = (view, year, monthOfYear, dayOfMonth) -> {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, monthOfYear);
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            et_dateTo.setText(dateFormat.format(calendar.getTime()));
//        };
////
////        et_dateFrom.setOnClickListener(v -> {
////            Calendar calendar = Calendar.getInstance();
////            new DatePickerDialog(BFreshBedrollReceiptFromLaundryActivity.this, journeyDateFrom, calendar.get(Calendar.YEAR),
////                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
////        });
////
////        et_dateTo.setOnClickListener(v -> {
////            Calendar calendar = Calendar.getInstance();
////            DatePickerDialog dpd = new DatePickerDialog(BFreshBedrollReceiptFromLaundryActivity.this, journeyDateTo,
////                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
////                    calendar.get(Calendar.DAY_OF_MONTH));
////            dpd.getDatePicker().setMaxDate(new Date().getTime());
////            dpd.show();
////        });
//    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.bedroll);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(BFreshBedrollReceiptFromLaundryActivity.this)) {
                GetLaundryList();
            } else {
                srl.setRefreshing(false);
            }
        });
    }

//    private void filterBedrollStock() {
//        String fromDateString = et_dateFrom.getText().toString();
//        String toDateString = et_dateTo.getText().toString();
//
////        Toast.makeText(this, ""+fromDateString + " " + toDateString, Toast.LENGTH_SHORT).show();
//
//        if (!TextUtils.isEmpty(fromDateString) && !TextUtils.isEmpty(toDateString)) {
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                Date fromDate = dateFormat.parse(fromDateString);
//                Date toDate = dateFormat.parse(toDateString);
//
//                ArrayList<FreshBedrollModel.mFbedroll> filteredList = new ArrayList<>();
//
//                for (FreshBedrollModel.mFbedroll item : stockDataItemList) {
//                    try {
//                        Date itemDate = dateFormat.parse(item.mSupply_date);
//                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
//                            filteredList.add(item);
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                bedrollStockAdapter.filterList(filteredList);
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } else {
//            bedrollStockAdapter.resetFilter();
//        }
//    }

    private void GetLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BEDROLL_API,
                response -> {
                    Log.d("BedrollStocking", "Response: " + response); // Log the response
                    try {
                        FreshBedrollModel freshBedrollModel = new Gson().fromJson(response, FreshBedrollModel.class);

                        // Update data list and notify adapter
                        stockDataItemList.clear();
                        stockDataItemList.addAll(freshBedrollModel.mRollList);

                        // Initialize adapter if not already initialized
                        if (bedrollStockAdapter == null) {
                            bedrollStockAdapter = new BedrollStockAdapter(stockDataItemList,getApplicationContext());
                            recyclerView.setAdapter(bedrollStockAdapter);
                        } else {
                            bedrollStockAdapter.notifyDataSetChanged();
                        }

                        // Check and update visibility based on data availability
                        checkEmptyData();
                    } catch (Exception e) {
                        Log.e("BedrollStocking", "Parsing error: " + e.getMessage(), e);
                    } finally {
                        srl.setRefreshing(false); // Stop refreshing animation
                    }
                },
                error -> {
                    Log.e("BedrollStocking", "Error Response: " + error.getMessage(), error);
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
                    Log.e("BedrollStocking", "Encoding error: " + uee.getMessage(), uee);
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

    public class BedrollStockAdapter extends RecyclerView.Adapter<BedrollStockAdapter.ViewHolder> {

        private ArrayList<FreshBedrollModel.mFbedroll> mCurrentList;
        private ArrayList<FreshBedrollModel.mFbedroll> filterList;
        private Context context;


        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        private boolean checkMode = false;

        public BedrollStockAdapter(ArrayList<FreshBedrollModel.mFbedroll> mCurrentList, Context context) {
            this.mCurrentList = new ArrayList<>(mCurrentList);
            this.filterList = new ArrayList<>(mCurrentList);
            this.context = context;
        }

        @NonNull
        @Override
        public BedrollStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_supply_list2, parent, false);
            return new BedrollStockAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BedrollStockAdapter.ViewHolder holder, int position) {
            FreshBedrollModel.mFbedroll item = mCurrentList.get(position);

            holder.tv_index.setText(String.valueOf(position + 1));
            holder.tv1.setText(item.mSupply_date);
            holder.tv2.setText(item.mLaundry_area);
            holder.tv3.setText(item.mTrain_no);
            holder.tv4.setText(item.mTrain_no);
            holder.tv5.setText(item.mPacket_count);


            holder.tv5_.setText(mCurrentList.get(position).mCoach);
            holder.tv6_.setText(mCurrentList.get(position).mNo_of_bag);
            holder.tv7.setText(mCurrentList.get(position).mBs);
            holder.tv8.setText(mCurrentList.get(position).mPc);
            holder.tv9.setText(mCurrentList.get(position).mPillow);
            holder.tv10.setText(mCurrentList.get(position).mBlanket_cover);
            holder.tv11.setText(mCurrentList.get(position).mFt);
            holder.tv12.setText(mCurrentList.get(position).mNo_blanket);


            String status = item.getmStatus();


            if (status.equals("0")) {


                if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES){
                    holder.iv_view.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                }else{
                    holder.iv_view.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                }
                holder.iv_view.setEnabled(true);
                holder.iv_view.setText("Verify");
                holder.iv_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewAlertDialog();
                    }

                    private void viewAlertDialog() {
                        final Dialog dialog = new Dialog(BFreshBedrollReceiptFromLaundryActivity.this, R.style.Dialog);
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
                                    OkDataSave(dialog, mCurrentList.get(position).mId);
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
                            jsonObject.put("table_name", "lms_laundry_sent_to_depot");
                            jsonObject.put("verify_by", userdataModel.mUserItems.get(0).mDepot_code);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String requestBody = jsonObject.toString();
                        Log.e("reqbody", requestBody);
                        showLoading("Please wait...");

                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                VRIFY_API, new Response.Listener<String>() {
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
                        RequestQueue requestQueue = Volley.newRequestQueue(BFreshBedrollReceiptFromLaundryActivity.this);
                        requestQueue.add(stringRequest);

                    }
                });
            } else if (status.equals("1")) {
                if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES){
                    holder.iv_view.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                }else{
                    holder.iv_view.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                }
                holder.iv_view.setEnabled(false);
                holder.iv_view.setText("Verified");
                holder.iv_view.setOnClickListener(null);
            }

            checkEmptyData();


        }

        @Override
        public int getItemCount() {
            return mCurrentList.size();
        }

        public void filterList(ArrayList<FreshBedrollModel.mFbedroll> filteredList) {
            mCurrentList.clear();
            mCurrentList.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void resetFilter() {
            mCurrentList.clear();
            mCurrentList.addAll(filterList);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index, tv1, tv2, tv3, tv4, tv5, tv6;
            TextView iv_view,tv5_,tv6_,tv7,tv8,tv9,tv10,tv11,tv12;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv1 = itemView.findViewById(R.id.tv_date);
                tv2 = itemView.findViewById(R.id.tv_laundry);
                tv3 = itemView.findViewById(R.id.tv_train_no);
                tv4 = itemView.findViewById(R.id.tv_coach);
                tv5 = itemView.findViewById(R.id.tv_total);
                tv6 = itemView.findViewById(R.id.tv_status);
                iv_view = itemView.findViewById(R.id.iv_view);


                tv5_ = (TextView) itemView.findViewById(R.id.tv_coach1);
                tv6_ = (TextView) itemView.findViewById(R.id.tv_no_of_bag);
                tv7 = (TextView) itemView.findViewById(R.id.tv_bed_sheet);
                tv8 = (TextView) itemView.findViewById(R.id.tv_pillow_cover);
                tv9 = (TextView) itemView.findViewById(R.id.tv_bath_towel);
                tv10 = (TextView) itemView.findViewById(R.id.tv_blanket_cover);
                tv11 = (TextView) itemView.findViewById(R.id.tv_face_towel);
                tv12 = (TextView) itemView.findViewById(R.id.tv_blanket);
            }
        }
    }

//    private void checkVarify(TextView iv_view) {
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
//            srl.setRefreshing(true); // Start refreshing animation
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        final String requestBody = jsonObject.toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, BEDROLL_API, response -> {
//            Log.d("VarifyAPI", "Full Response: " + response);
//
//            try {
//                JSONObject jsonResponse = new JSONObject(response);
//
//                if (jsonResponse.has("Getreceived_data")) {
//                    JSONArray dataArray = jsonResponse.getJSONArray("Getreceived_data");
//                    for (int i = 0; i < dataArray.length(); i++) {
//                        Log.e("Loop_Length", String.valueOf(dataArray.length()));
//
//                        JSONObject jsonObject1 = dataArray.getJSONObject(i);
//                        if (jsonObject1.has("status")) {
//
//                            String status = jsonObject1.getString("status");
//                            Log.e("kadflkahdkf", status);
//
//                            if (status.equals("1")) {
//                                iv_view.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
//                                iv_view.setEnabled(false);
//                                iv_view.setText("Verify");
//                                iv_view.setOnClickListener(null);
//                                notify();
//                            }else
//
//
//                            if (status.equals("0"))  {
//                                iv_view.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
//                                iv_view.setEnabled(true);
//                                iv_view.setText("Verified");
//                                notify();
//                            }
//
//
//
//
//                        } else {
//                            Log.e("VarifyAPI", "'status' key not found in Getreceived_data at index " + i);
//                        }
//                    }
//                } else {
//                    Log.e("VarifyAPI", "delivery_status key not found in the response");
//                }
//
//            } catch (Exception e) {
//                Log.e("VarifyAPI2", "Parsing error: " + e.getMessage(), e);
//            } finally {
//                srl.setRefreshing(false); // Stop refreshing animation
//            }
//        },
//                error -> {
//                    Log.e("VarifyAPI3", "Error Response: " + error.getMessage(), error);
//                    srl.setRefreshing(false); // Stop refreshing animation
//                }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    Log.e("VarifyAPI4", "Encoding error: " + uee.getMessage(), uee);
//                    return null;
//                }
//            }
//        };
//
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(stringRequest);
//    }


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
        final Dialog dialog = new Dialog(BFreshBedrollReceiptFromLaundryActivity.this);
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
                Intent intent = new Intent(BFreshBedrollReceiptFromLaundryActivity.this, BFreshBedrollReceiptFromLaundryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}
