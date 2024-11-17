package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.Bmodel.GetCondemendList;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturnAddStockActvity;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturntViewStockActivity;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturntoLaundrytFromBufferStockActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReceiveReturnedBufferFromLaundry extends AppCompatActivity {

    //    private final static String BEDROLL_API = "http://lmsguwahati.projectrailway.in/api/get_bedroll_return_Items";
    private final static String BEDROLL_API = "http://lmsguwahati.projectrailway.in/api/get_bedroll_return_to_laundry_from_buffer_stock";
    //varify api used in below
    private final static String VRIFY_API = "http://lmsguwahati.projectrailway.in/api/verify_and_train_supply";
    private final static String VARIFY_CONFIRM_API = "http://lmsguwahati.projectrailway.in/api/VerifyStatus";

    ImageView iv_add_supply;
    EditText et_dateFrom, et_dateTo;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    AlertDialog dialog;
    final Calendar myCalendar = Calendar.getInstance();
    String depot_code = "";
    ReceiveReturnedBufferFromLaundry.BedrollStockAdapter bedrollStockAdapter;
    private ArrayList<GetCondemendList.mStoreItem> stockDataItemList = new ArrayList<>();
    UserDataModel userdataModel;
    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_returned_buffer_from_laundry);


        depot_code = getIntent().getStringExtra("deport_code");
        uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();

        DarkAndLightMode(uiModeManager);

        setupDatePickers(uiModeManager);
        setupRecyclerView();
        setupSwipeRefresh();

        GetLaundryList();


    }

    private void DarkAndLightMode(UiModeManager uiModeManager) {
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
        } else {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }
    }

    private void initializeViews() {
        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);
//        tv_tittle = findViewById(R.id.tv_toolbar_title);
//        tv_tittle.setText(userdataModel.mUserItems.get(0).mHeader);
        iv_add_supply = findViewById(R.id.iv_add_supply);
        tv_empty_data = findViewById(R.id.tv_empty_data);

        iv_add_supply = findViewById(R.id.iv_add_supply);
        tv_empty_data = findViewById(R.id.tv_empty_data);

//        iv_add_supply = findViewById(R.id.v_add_buffer);
//        iv_add_supply.findViewById(R.id.v_add_buffer).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(), BedrollReturnAddStockActvity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//            }
//        });

        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());


        et_dateFrom.addTextChangedListener(new ReceiveReturnedBufferFromLaundry.TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });

        et_dateTo.addTextChangedListener(new ReceiveReturnedBufferFromLaundry.TextWatcherAdapter() {
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(ReceiveReturnedBufferFromLaundry.this, journeyDateFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(ReceiveReturnedBufferFromLaundry.this,
//                    R.style.CustomDatePicker,
                    journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.bedroll);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(ReceiveReturnedBufferFromLaundry.this)) {
                GetLaundryList();
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

                ArrayList<GetCondemendList.mStoreItem> filteredList = new ArrayList<>();

                for (GetCondemendList.mStoreItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mSubmission_date);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (bedrollStockAdapter != null) {
                    bedrollStockAdapter.filterList(filteredList);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (bedrollStockAdapter != null) {
                bedrollStockAdapter.resetFilter();
            }
        }
    }

    private void GetLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundryID", userdataModel.mUserItems.get(0).mLaundryID);
            srl.setRefreshing(true); // Start refreshing animation
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BEDROLL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("BedrollStockingg", "Response: " + response); // Log the response
//                    String status = response.g
//                    String message = response.getString("message");

                try {
                    GetCondemendList freshBedrollModel = new Gson().fromJson(response, GetCondemendList.class);

                    // Update data list and notify adapter
                    stockDataItemList.clear();
                    stockDataItemList.addAll(freshBedrollModel.mList);

                    // Initialize adapter if not already initialized
                    if (bedrollStockAdapter == null) {
                        bedrollStockAdapter = new ReceiveReturnedBufferFromLaundry.BedrollStockAdapter(stockDataItemList);
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


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BedrollStocking", "Error Response: " + error.getMessage(), error);
                srl.setRefreshing(false);
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
                    Log.e("BedrollStocking", "Encoding error: " + uee.getMessage(), uee);
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, BEDROLL_API,response -> {
//
//                    Log.d("BedrollStockingg", "Response: " + response); // Log the response
////                    String status = response.g
////                    String message = response.getString("message");
//                    try {
//                        GetCondemendList freshBedrollModel = new Gson().fromJson(response, GetCondemendList.class);
//
//                        // Update data list and notify adapter
//                        stockDataItemList.clear();
//                        stockDataItemList.addAll(freshBedrollModel.mList);
//
//                        // Initialize adapter if not already initialized
//                        if (bedrollStockAdapter == null) {
//                            bedrollStockAdapter = new ReceiveReturnedBufferFromLaundry.BedrollStockAdapter(stockDataItemList);
//                            recyclerView.setAdapter(bedrollStockAdapter);
//                        } else {
//                            bedrollStockAdapter.notifyDataSetChanged();
//                        }
//
//                        // Check and update visibility based on data availability
//                        checkEmptyData();
//                    } catch (Exception e) {
//                        Log.e("BedrollStocking", "Parsing error: " + e.getMessage(), e);
//                    } finally {
//                        srl.setRefreshing(false); // Stop refreshing animation
//                    }
//                },
//                error -> {
//                    Log.e("BedrollStocking", "Error Response: " + error.getMessage(), error);
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
//                    Log.e("BedrollStocking", "Encoding error: " + uee.getMessage(), uee);
//                    return null;
//                }
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
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

    public class BedrollStockAdapter extends RecyclerView.Adapter<ReceiveReturnedBufferFromLaundry.BedrollStockAdapter.ViewHolder> {
        public String status;
        private ArrayList<GetCondemendList.mStoreItem> mList;
        private ArrayList<GetCondemendList.mStoreItem> mListFull;

        public BedrollStockAdapter(ArrayList<GetCondemendList.mStoreItem> mList) {
            this.mList = new ArrayList<>(mList);
            this.mListFull = new ArrayList<>(mList);
        }

        @NonNull
        @Override
        public ReceiveReturnedBufferFromLaundry.BedrollStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_condemnt_bedroll, parent, false);
            return new ReceiveReturnedBufferFromLaundry.BedrollStockAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReceiveReturnedBufferFromLaundry.BedrollStockAdapter.ViewHolder holder, int position) {
            GetCondemendList.mStoreItem item = mList.get(position);

            holder.tv_index.setText(String.valueOf(position + 1));
            holder.tv1.setText(item.mSubmission_date);
            holder.tv2.setText(item.mDepot_name);
            holder.tv3.setText(item.mDelivery_status);

            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReceiveReturnedBufferFromLaundry.this, BedrollReturntViewStockActivity.class);
                    intent.putExtra("id", item.mId);
                    startActivity(intent);
                }
            });

            if (item.mStatus.equals("1")) {
                //ye varify ho chuka hai
                holder.iv_varify.setText("Verify");
                holder.iv_varify.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));

            } else {
                //yaha varify nahi hua hai
                holder.iv_varify.setText("Verify");
                holder.iv_varify.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));

                holder.iv_varify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                        alertDialog.setTitle("Verify ");
                        alertDialog.setMessage("Are you confirm for this " + item.mDepot_name);

                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Oops, No problem", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                hitVerifyStatusApi(v.getContext(), item.mId, "lms_save_bedroll_return_from_depot", holder.iv_varify);

                            }
                        });

//                    alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();  // Just dismiss the dialog
//                        }
//                    });

                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    }
                });
            }


            checkEmptyData();

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void filterList(ArrayList<GetCondemendList.mStoreItem> filteredList) {
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
            TextView tv_index, tv1, tv2, tv3;
            TextView iv_view;
            TextView iv_varify;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv1 = itemView.findViewById(R.id.tv_date);
                tv2 = itemView.findViewById(R.id.tv_laundry);
                tv3 = itemView.findViewById(R.id.tv_status);
                iv_view = itemView.findViewById(R.id.iv_view);
                iv_varify = itemView.findViewById(R.id.iv_varify);
            }
        }
    }

    private void hitVerifyStatusApi(Context context, String rowId, String tableName, TextView iv_varify) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://lmsguwahati.projectrailway.in/api/VerifyStatus";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("row_id", rowId);
            jsonBody.put("table_name", tableName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(context, "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        try {


                            if (context instanceof ReceiveReturnedBufferFromLaundry) {
                                ((ReceiveReturnedBufferFromLaundry) context).refreshActivity();
                            }
//                            String message = response.getString("message");

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            Log.e("response", String.valueOf(response));


                            if (response != null && response.has("message")) {
                                String message = response.getString("message");
                                if (message.equals("Delivery Updated")) {
//                                showConfirmationDialog("Data Saved Successfully",uiModeManager);

//                                    if (!isFinishing() && !isDestroyed()){
//
//                                        showConfirmationDialog(message,uiModeManager);
//                                    }


                                } else {
//                                showConfirmationDialog("Something Wrong",uiModeManager);
//                                    showConfirmationDialog(response.toString(),uiModeManager);
                                    Log.e("errr", response.toString());
                                }

                            } else {
                                Log.e("errr", response.toString());

//                                showConfirmationDialog("Error : ", uiModeManager);
                            }


//                            showConfirmationDialog("Data Saved Successfully", uiModeManager);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Response parsing error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void refreshActivity() {
        recreate();
    }

    // Helper class to simplify TextWatcher implementation
    public abstract static class TextWatcherAdapter implements TextWatcher {
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

    public void showConfirmationDialog(String strMessage, UiModeManager uiModeManager) {
        // Check if the activity is in a state to show a dialog
        if (isFinishing() || isDestroyed()) {
            return; // Exit if the activity is not active
        }

        final Dialog dialog = new Dialog(ReceiveReturnedBufferFromLaundry.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvTitle = dialog.findViewById(R.id.confirmMessageTitle);

        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            tvTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
            tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
        }
        tvMessage.setText(strMessage);

        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Intent intent = new Intent(ReceiveReturnedBufferFromLaundry.this, BedrollReturntoLaundrytFromBufferStockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    // Override onDestroy to dismiss the dialog to prevent memory leaks
    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }


}