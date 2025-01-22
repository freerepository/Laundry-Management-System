package com.example.laundrymanagementghy.LaundryActivity;

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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.EditReceived;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.Bmodel.GetReceiveddailogStoreList;
import com.example.laundrymanagementghy.Bmodel.SoiledModelList;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FreshBedrollReceiptfromLaundry extends AppCompatActivity {
    private final static String received_from_store_list_Api = "http://lmskyq.projectrailway.in/api/soiled_bedroll_received_from_depot";
    private final static String update_store_status = "http://lmskyq.projectrailway.in/Api/update_store_status";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    TextView tv_empty_data, tv_tittle;
    String deport_code = "", id;
    AlertDialog dialog;
    ReceivedFromStoreAdapter adapter;
    View supOfficer_layout, train_no_layout;
    UserDataModel userdataModel;
    private ArrayList<GetReceiveddailogStoreList.ReceivedDataStore> ListData;
    UiModeManager uiModeManager;
    private ArrayList<SoiledModelList.mSupItem> stockDataItemList = new ArrayList<>();
    EditText et_dateFrom, et_dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fresh_bedroll_received_from_depot);
        id = getIntent().getStringExtra("id");
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

        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);
        train_no_layout = findViewById(R.id.train_no_layout);
        supOfficer_layout = findViewById(R.id.supOfficer_layout);

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            supOfficer_layout.setBackgroundResource(R.drawable.shape_white);
            train_no_layout.setBackgroundResource(R.drawable.shape_white);

        } else {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }

        tv_tittle = findViewById(R.id.tv_tittle);
        tv_tittle.setText(userdataModel.mUserItems.get(0).mHeader);
        tv_empty_data = findViewById(R.id.tv_empty_data);

        setupRecyclerView();
//        recyclerView = (RecyclerView) findViewById(R.id.view_received);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new ReceivedFromStoreAdapter(new ArrayList<>());
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(FreshBedrollReceiptfromLaundry.this)) {
                ReceivedFromStoreActivityList();
            } else {
                srl.setRefreshing(false);
            }
        });
        ReceivedFromStoreActivityList();


        setupDatePickers(uiModeManager);

        et_dateFrom.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });
        et_dateTo.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });


    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.view_received);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
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
            DatePickerDialog dpd = new DatePickerDialog(FreshBedrollReceiptfromLaundry.this, journeyDateFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(FreshBedrollReceiptfromLaundry.this, journeyDateTo, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
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
                ArrayList<SoiledModelList.mSupItem> filteredList = new ArrayList<>();

                for (SoiledModelList.mSupItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mDate);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("FilterDebug", "Filtered List Size: " + filteredList.size());

                if (filteredList.size() == 0 || filteredList.size() < 0 || filteredList == null) {
                    recyclerView.setVisibility(View.GONE);
                    tv_empty_data.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_empty_data.setVisibility(View.GONE);
                }


                if (adapter != null) {
                    adapter.filterList(filteredList);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (adapter != null) {
                adapter.resetFilter();
            }
        }
    }


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

    private void ReceivedFromStoreActivityList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, received_from_store_list_Api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            SoiledModelList supplyList = new Gson().fromJson(response.toString(), SoiledModelList.class);
//                            recyclerView.setAdapter(new ReceivedFromStoreAdapter(mReceivedStore.mLists));
                            stockDataItemList.clear();
                            stockDataItemList.addAll(supplyList.mLists);
                            if (adapter == null) {
                                adapter = new ReceivedFromStoreAdapter(stockDataItemList);
                                recyclerView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }


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

    public class ReceivedFromStoreAdapter extends RecyclerView.Adapter<FreshBedrollReceiptfromLaundry.ViewHolder> {
        private ArrayList<SoiledModelList.mSupItem> list;
        private ArrayList<SoiledModelList.mSupItem> mListFull;


        public ReceivedFromStoreAdapter(ArrayList<SoiledModelList.mSupItem> list) {
            this.list = new ArrayList<>(list);
            this.mListFull = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public FreshBedrollReceiptfromLaundry.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_receiced_from_store_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull FreshBedrollReceiptfromLaundry.ViewHolder holder, int pos) {
            final int position = pos;
            holder.tv_index.setText((position + 1) + "");
            holder.tv.setText(list.get(position).mDate);
            holder.tv1.setText(list.get(position).mDepot_code);
            holder.tv_train_n.setText(list.get(position).mTrain_no);
            holder.btn_status_verify.setText(list.get(position).mDelivery_status);
//            holder.btn_status_verify.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
            holder.iv_ivew_status_verify.setText("Mark Cleaned");

            if (list.get(position).mIs_verified.equals("1")){
                holder.btn_status_verify.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorGray));
                holder.iv_ivew_status_verify.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorGreen));
                holder.iv_ivew_status_verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(FreshBedrollReceiptfromLaundry.this, R.style.Dialog);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        //dialog.setCancelable(false);
                        dialog.setContentView(R.layout.diolog_update_delivery);
                        final TextView tv_update = dialog.findViewById(R.id.tv_update);
                        tv_update.setText("Are You Want to Mark Cleaned?");

                        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DataUpdated(dialog, list.get(position).mSent_id, "http://lmskyq.projectrailway.in/api/verify_cleaned_status");
                            }


                            public void DataUpdated(Dialog dialog1, String rowId, String url) {
                                final JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("row_id", rowId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                final String requestBody = jsonObject.toString();
                                Log.e("reqbody", requestBody);
                                showLoading("Please wait...");

                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        url, new Response.Listener<String>() {
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

                                                showConfirmationDialog(message,uiModeManager);
                                            } else {
                                                showConfirmationDialog(response,uiModeManager);
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
                                RequestQueue requestQueue = Volley.newRequestQueue(FreshBedrollReceiptfromLaundry.this);
                                requestQueue.add(stringRequest);
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
            }else{
                holder.btn_status_verify.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorRed));
                holder.iv_ivew_status_verify.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorGray));
            }

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }


//            holder.btn_status_verify.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    UpdateDeliveryAlertDialog();
//
//                }
//
//                private void UpdateDeliveryAlertDialog() {
//                    final Dialog dialog = new Dialog(FreshBedrollReceiptfromLaundry.this, R.style.Dialog);
//                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                            WindowManager.LayoutParams.MATCH_PARENT);
//                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    //dialog.setCancelable(false);
//                    dialog.setContentView(R.layout.diolog_update_delivery);
//                    final TextView tv_update = dialog.findViewById(R.id.tv_update);
//
//                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            if (TextUtils.isEmpty(et_date.getText().toString())) {
////                                Toast.makeText(LaundryReceived.this, "Select Date", Toast.LENGTH_SHORT).show();
////                            } else {
//                            try {
//                                 OkDataSave(dialog,list.get(position).mSent_id);
//                            } catch (Exception e) {
//                                throw new RuntimeException(e);
//
//                            }
//                        }
//
//                    });
//
//                    dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//
//
//                }
//                private void OkDataSave(Dialog dialog, String item_id) {
//                    final JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("item_id", item_id);
//                        jsonObject.put("table_name", "lms_save_storesent");
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    final String requestBody = jsonObject.toString();
//                    Log.e("reqbody", requestBody);
//                    showLoading("Please wait...");
//
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                            update_store_status, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            hideLoading();
//                            try {
//                                Log.e("response", response);
//
//                                JSONObject jsonResponse = null;
//                                try {
//                                    jsonResponse = new JSONObject(response);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                if (jsonResponse != null && jsonResponse.has("message")) {
//                                    String message = jsonResponse.getString("message");
//
//                                    showConfirmationDialog(message);
//                                } else {
//                                    showConfirmationDialog(response);
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            hideLoading();
//                        }
//                    }) {
//                        @Override
//                        public String getBodyContentType() {
//                            return "application/json; charset=utf-8";
//                        }
//
//                        @Override
//                        public byte[] getBody() throws com.android.volley.AuthFailureError {
//                            try {
//                                return requestBody == null ? null : requestBody.getBytes("utf-8");
//                            } catch (UnsupportedEncodingException uee) {
//                                return null;
//                            }
//                        }
//                    };
//                    RequestQueue requestQueue = Volley.newRequestQueue(FreshBedrollReceiptfromLaundry.this);
//                    requestQueue.add(stringRequest);
//                }
//            });

            holder.iv_ivew_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAleartDialog(
                            list.get(position).mPillow,
                            list.get(position).mPillow_return,
                            list.get(position).mSent_id,
                            list.get(position).mDepot_name,
                            list.get(position).mDate,
                            list.get(position).mDepot_code,
                            list.get(position).mTrain_no,
                            list.get(position).mCoach,

                            list.get(position).mBs,
                            list.get(position).mBlanket,
                            list.get(position).mPc,
//                            list.get(position).mBath_towel,
                            list.get(position).mBlanket_cover,
                            list.get(position).mFt,
                            list.get(position).mStatus,
                            list.get(position).mLaundry_id,


                            list.get(position).mBs_return,
                            list.get(position).mPc_return,
                            list.get(position).mFt_return,
                            list.get(position).mBlk_return,
//                            list.get(position).mBathtowel_return,
                            list.get(position).mBlanket_return,
                            list.get(position).mRemark,

                            list.get(position).mBs_unsed,
                            list.get(position).mPillow_unused,
                            list.get(position).mPc_unused,
                            list.get(position).mBlanket_unused,
                            list.get(position).mBlc_unused,
                            list.get(position).mHt_unused
                    );


//                    Intent intent = new Intent(FreshBedrollReceiptfromLaundry.this, FreshBedrollReceipViewLaundry.class);
//                    intent.putExtra("id", list.get(position).mSent_id);
//                    startActivity(intent);
                }
                private void setTextOrDefault(EditText editText, String value) {
                    editText.setText(value != null ? value : "");
                }
                private void showAleartDialog(
                        String pillow,
                        String pillowReturn,
                        String rowId,
                        String depotName,
                        String mDate,
                        String mDepot_code,
                        String mTrain_no,
                        String mCoach,
                        String mBs,
                        String mBlanket,
                        String mPc,
//                        String mBath_towel,
                        String mBlanket_cover,
                        String mFt,
                        String mStatus,
                        String mLaundry_id,
                        String mBsReturn,
                        String mPcReturn,
                        String mFtReturn,
                        String mBlanketCoverRetun,
//                        String mBathTowelReturn,
                        String mBlanketReturn,
                        String mRemark,

                        String mbs_u,
                        String mpillow_u,
                        String mpillowcover_u,
                        String mblanket_u,
                        String mblanketcover_u,
                        String mht_u

                ) {



                    final Dialog dialog = new Dialog(FreshBedrollReceiptfromLaundry.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.setContentView(R.layout.diolog_edit_received3);



                    final View colorTop = dialog.findViewById(R.id.rl);
                    colorTop.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
                    final TextView tv_date = dialog.findViewById(R.id.tv_dated);
                    final TextView tv_title = dialog.findViewById(R.id.tv_title);
                    final TextView tv_select_train = dialog.findViewById(R.id.tv_train_Nod);
                    final TextView tv_select_coach = dialog.findViewById(R.id.tv_coachd);
                    final TextView tv_select_laundry = dialog.findViewById(R.id.et_laundryd);
                    final TextView tv_bed_sheet = dialog.findViewById(R.id.qty_bed_sheet_d);
                    final TextView tv_pillow_cover = dialog.findViewById(R.id.qty_pillow_cover_d);
                    final TextView tv_pillow = dialog.findViewById(R.id.qty_pillow_d);
                    final TextView pillowtv = dialog.findViewById(R.id.pillowtv);

//                    tv_pillow.setVisibility(View.GONE);
//                    pillowtv.setVisibility(View.GONE);

                    tv_title.setText("Soiled Bedroll Received from Depot");

                    final TextView tv_face_towel = dialog.findViewById(R.id.qty_face_towel_d);
                    final TextView tv_blanket_cover = dialog.findViewById(R.id.qty_blanket_cover_d);
//                    final TextView tv_bath_towel = dialog.findViewById(R.id.qty_bath_towel_d);
                    final TextView tv_blanket = dialog.findViewById(R.id.qty_blanket_d);
                    final TextView tv_remark = dialog.findViewById(R.id.tvremarkd);
                    tv_remark.setVisibility(View.GONE);
                    final EditText et_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet);
                    et_BedSheet.setEnabled(false);
                    final EditText et_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d);
                    et_pillowCover.setEnabled(false);
                    final EditText et_pillow = dialog.findViewById(R.id.et_qty_pillow_d); //yaha data jab bhi submit hoga tab pillow return mai jayega
//                    et_pillow.setVisibility(View.GONE);
                    et_pillow.setEnabled(false);
                    final EditText et_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d);
                    et_FaceTowel.setEnabled(false);
                    final EditText et_Blanket = dialog.findViewById(R.id.et_qty_blanket_d);
                    et_Blanket.setEnabled(false);
                    final EditText et_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d);
                    et_BlanketCover.setEnabled(false);
//                    final EditText et_BathTowel = dialog.findViewById(R.id.et_qty_bath_towel_d);
//                    et_BathTowel.setEnabled(false);
                    final EditText et_remark = dialog.findViewById(R.id.et_remarkd);
                    et_remark.setVisibility(View.GONE);

                    //unused item editText
                    final EditText et_unused_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet_unUsed);
                    final EditText et_unused_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d_unUsed);
                    final EditText et_unused_pillow = dialog.findViewById(R.id.et_qty_pillow_d_unUsed); //yaha data jab bhi submit hoga tab pillow return mai jayega
                    final EditText et_unused_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d_unUsed);
                    final EditText et_unused_Blanket = dialog.findViewById(R.id.et_qty_blanket_d_unUsed);
                    final EditText et_unused_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d_unUsed);



                    /////////////////// DIALOG TEXT /////////////////////////////////////////////////////////////////////////////////////////////////////
                    //////// PILLOW & PILLOW RETURN ////////////////////////

                    if (list.get(position).mPillow != null) {
                        tv_pillow.setText(list.get(position).mPillow);
                    } else {
                        tv_pillow.setText("");
                    }

                    if (list.get(position).mPillow_return != null) {
                        et_pillow.setText(list.get(position).mPillow_return);
                    } else {
                        et_pillow.setText("");
                    }

                    //////// PILLOW & PILLOW RETURN ////////////////////////

                    //////////// BED SHEET & BED SHEET RETURN ////////////////////
                    if (list.get(position).mBs != null) {
                        tv_bed_sheet.setText(list.get(position).mBs);
                    } else {
                        tv_bed_sheet.setText("");
                    }

                    if (list.get(position).mBs_return != null) {
                        et_BedSheet.setText(list.get(position).mBs_return);
                    } else {
                        et_BedSheet.setText("");
                    }
                    //////////// BED SHEET & BED SHEET RETURN ////////////////////


                    //////// pillow & pillow return /////////////
                    if (list.get(position).mPillow != null) {
                        tv_pillow.setText(list.get(position).mPillow);
                    } else {
                        tv_pillow.setText("");
                    }

                    if (list.get(position).mPillow_return != null) {
                        et_pillow.setText(list.get(position).mPillow_return);
                    } else {
                        et_pillow.setText("");
                    }
                    //////// pillow & pillow return /////////////


                    /////////// pillow cover and pillow cover return /////////////////
                    if (list.get(position).mPc != null) {
                        tv_pillow_cover.setText(list.get(position).mPc);
                    } else {
                        tv_pillow_cover.setText("");
                    }

                    if (list.get(position).mPc_return != null) {
                        et_pillowCover.setText(list.get(position).mPc_return);
                    } else {
                        et_pillowCover.setText("");
                    }
                    /////////// pillow cover and pillow cover return /////////////////


                    ///////////////////// blanket & blanket   return ///////////////
                    if (list.get(position).mBlanket != null) {
                        tv_blanket.setText(list.get(position).mBlanket);
                    } else {
                        tv_blanket.setText("");

                    }
                    if (list.get(position).mBlk_return != null) {
                        et_Blanket.setText(list.get(position).mBlanket_return);
                    } else {
                        et_Blanket.setText("");

                    }
                    ///////////////////// blanket & blanket   return ///////////////


                    ///////////////////// blanket cover & blanket cover return ///////////////
                    if (list.get(position).mBlanket_cover != null) {
                        tv_blanket_cover.setText(list.get(position).mBlanket_cover);
                    } else {
                        tv_blanket_cover.setText("");
                    }

                    if (list.get(position).mBlk_return != null) {
                        et_BlanketCover.setText(list.get(position).mBlk_return);
                    } else {
                        et_BlanketCover.setText("");
                    }


                    ///////////////////// blanket cover & blanket cover return ///////////////


                    //////////hand towel & hand towell return //////////////////////
                    if (list.get(position).mFt != null) {
                        tv_face_towel.setText(list.get(position).mFt);
                    } else {
                        tv_face_towel.setText("");
                    }

                    if (list.get(position).mFt_return != null) {
                        et_FaceTowel.setText(list.get(position).mFt_return);
                    } else {
                        et_FaceTowel.setText("");
                    }

                    //////////hand towel & hand towell return //////////////////////


                    if (list.get(position).mRemark != null) {
                        et_remark.setText(list.get(position).mRemark);
                    } else {
                        et_remark.setText("");
                    }


                    setTextOrDefault(et_unused_BlanketCover, list.get(position).mBlc_unused);
                    setTextOrDefault(et_unused_Blanket, list.get(position).mBlanket_unused);
                    setTextOrDefault(et_unused_BedSheet, list.get(position).mBs_unsed);
                    setTextOrDefault(et_unused_pillowCover, list.get(position).mPc_unused);
                    setTextOrDefault(et_unused_pillow, list.get(position).mPillow_unused);
                    setTextOrDefault(et_unused_FaceTowel, list.get(position).mHt_unused);


                    tv_date.setText(mDate);
                    tv_select_train.setText(mTrain_no);
                    tv_select_laundry.setText(mDepot_code);
                    tv_select_coach.setText(mCoach);
                    tv_bed_sheet.setText(mBs);
                    tv_pillow_cover.setText(mPc);
                    tv_face_towel.setText(mFt);
                    tv_blanket_cover.setText(mBlanket_cover);
                    tv_blanket.setText(mBlanket);
//                    tv_bath_towel.setText(mBath_towel);
//                    tv_remark.setText(mRemark);


//------------------> ye ! ki sign laga diya hai check karane ke liye
                    if (!mStatus.equals("1")) {
//                        dialog.findViewById(R.id.v_positive).setVisibility(View.GONE);
                    } else {
                        dialog.findViewById(R.id.v_positive).setVisibility(View.VISIBLE);

                        TextView textView = dialog.findViewById(R.id.v_positive);
                        textView.setText("Update");

                        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final  JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("row_id",rowId);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                final String requestBody = jsonObject.toString();
                                Log.e("reqbody", requestBody);
                                showLoading("Please wait...");

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://lmskyq.projectrailway.in/api/verify_solied_bedroll_status", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();
                                        try {
                                            Log.e("responserespo", response);

                                            JSONObject jsonResponse = null;
                                            try {
                                                jsonResponse = new JSONObject(response);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (jsonResponse != null && jsonResponse.has("message")) {
                                                String message = jsonResponse.getString("message");

                                                showConfirmationDialog(message,uiModeManager);
                                            } else {
                                                showConfirmationDialog(response,uiModeManager);
                                            }
                                            Type listType = new TypeToken<List<EditReceived>>() {
                                            }.getType();
                                            ArrayList<EditReceived> getList = new Gson().fromJson(response.toString(), listType);

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
                                RequestQueue requestQueue = Volley.newRequestQueue(FreshBedrollReceiptfromLaundry.this);
                                requestQueue.add(stringRequest);
                            }
                        });
                    }






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

        private void EditDataSave(String rowId, String mLaundry_id, String depotName, String mDate, String toString, String toString1, String toString2, String toString3, String toString4, String toString5, String toString6) {


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void filterList(ArrayList<SoiledModelList.mSupItem> filteredList) {
            list.clear();
            list.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void resetFilter() {
            list.clear();
            list.addAll(mListFull);
            notifyDataSetChanged();
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv, tv1, tv_train_n;
        TextView btn_status_verify;
        ImageView iv_ivew_status;
        TextView iv_ivew_status_verify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
            tv = (TextView) itemView.findViewById(R.id.tv_date);
            tv1 = (TextView) itemView.findViewById(R.id.tv_store_name);
            tv_train_n = (TextView) itemView.findViewById(R.id.tv_train_n);
            btn_status_verify = (TextView) itemView.findViewById(R.id.tv_status_verify);
            iv_ivew_status = (ImageView) itemView.findViewById(R.id.iv_ivew_status);
            iv_ivew_status_verify = (TextView) itemView.findViewById(R.id.iv_ivew_status_verify);


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
        builder.setCancelable(false); // if you want ushser to wait for some process to finish,
        builder.setView(ll);
        dialog = builder.create();
        dialog.show();
    }


    protected void hideLoading() {
        dialog.dismiss();
    }

    public void showConfirmationDialog(String strMessage,UiModeManager uiModeManager) {
        final Dialog dialog = new Dialog(FreshBedrollReceiptfromLaundry.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvtitle = dialog.findViewById(R.id.confirmMessageTitle);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);

        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES){
            tvtitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.whiteTextColor));
            tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.whiteTextColor));
        }


        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(FreshBedrollReceiptfromLaundry.this, FreshBedrollReceiptfromLaundry.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}