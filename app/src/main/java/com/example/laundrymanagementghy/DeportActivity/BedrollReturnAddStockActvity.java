package com.example.laundrymanagementghy.DeportActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.CaptureSignatureActivity;
import com.example.laundrymanagementghy.Activity.VolleyMultipartRequest;
import com.example.laundrymanagementghy.Amodel.GetBufferIssueList;
import com.example.laundrymanagementghy.Amodel.QanswerData1;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.LaundryActivity.BufferStockIssuetoDepot;
import com.example.laundrymanagementghy.LaundryActivity.CondemnedBedrollActivity;
import com.example.laundrymanagementghy.LaundryActivity.CondemnedBedrollAddActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QueStoreModel;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BedrollReturnAddStockActvity extends AppCompatActivity {

    private static String storequestionAPI = "http://lmsguwahati.projectrailway.in/api/getcondemnationItems";
    private final static String getstorequestionAPI = "http://lmsguwahati.projectrailway.in/api/getbufferItems";
    private final static String REASON_API = "http://lmsguwahati.projectrailway.in/api/getReason";
    public final static String STORING_Image = "http://lmsguwahati.projectrailway.in/Api/upload_signature";
    private final static String GET_DEPOT = "http://lmsguwahati.projectrailway.in/Api/get_all_laundry";
    private final static String SubmitPenaltyData = "http://lmsguwahati.projectrailway.in/api/save_bedroll_return_from_depot";
    ImageView iv_backView, iv_calender;
    EditText et_date;
    Button submit;
    Spinner sp_depot;
    SupplyCondAddBsradapter adapter;
    RecyclerView recyclerView;
    UserDataModel userdataModel;
    String requestBody;
    String message;
    QueStoreModel queStoreModel = null;
    SwipeRefreshLayout srl;
    public JSONArray questionArray;
    final Calendar myCalendar = Calendar.getInstance();
    LinearLayout signaturelayout;
    public String laundryid = "";
    public boolean _checkVisiblity = false;
    public HashMap<String, QanswerData1> qmap = new HashMap<>();

//    HashMap<String, String> map = new HashMap<>();

    ImageView signclick1, iv_sign1;
    public String strSignatureFilePath1 = "", signatureresponse1;
    public static final int SIGNATURE_ACTIVITY = 1;
    TextView tv_toolbar_title,rqty;
    ProgressDialog mProgressDialog;
    ArrayList<String> depotList = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayAdapter<String> depotAdapter;
    public String selectedDepot = "", depot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroll_return_add_stock);
        queStoreModel = (QueStoreModel) getIntent().getSerializableExtra("qdata");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        srl = findViewById(R.id.srl);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        rqty = findViewById(R.id.rqty);
        tv_toolbar_title.setText("Buffer Return To Laundry");
//        Toast.makeText(this, "run", Toast.LENGTH_SHORT).show();
        recyclerView = findViewById(R.id.rv);
        iv_calender = findViewById(R.id.iv_calender);
        et_date = findViewById(R.id.et_select_date);
        sp_depot = findViewById(R.id.sp_get_storeLis);
        submit = findViewById(R.id.btn_next_submit);

        signaturelayout = findViewById(R.id.signature_layout);
        signclick1 = findViewById(R.id.click1);
        iv_sign1 = findViewById(R.id.img_sign1);
        iv_backView = findViewById(R.id.v_back);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BedrollReturnAddStockActvity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });


        submit.setText("Submit");
        adapter = new SupplyCondAddBsradapter(questionArray, BedrollReturnAddStockActvity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qmap.size() == 0) {
                    Log.d("submit", "qmap size: " + qmap.size());
                    Toast.makeText(getApplicationContext(), "Please give at least one Enter value and select reason", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please Select Date", Toast.LENGTH_SHORT).show();
                } else if (sp_depot.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select Launday", Toast.LENGTH_SHORT).show();
                } else {
                    showLoading("Uploading...");
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.button_orange_bg);

                    JSONArray jsonArray = new JSONArray();
                    for (QanswerData1 qanswerData : qmap.values()) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("item", qanswerData.getQuest_id());
                            jo.put("qty", qanswerData.getQuantity());
                            jo.put("reason", qanswerData.getReason());
                            jsonArray.put(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("supervisor_id", userdataModel.mUserItems.get(0).mLogin_id);
                        jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
                        jsonObject.put("laundry_id", depot_id_list.get(sp_depot.getSelectedItemPosition()));
                        jsonObject.put("date", et_date.getText().toString());
                        jsonObject.put("received_from", "laundry");
                        jsonObject.put("signature", signatureresponse1);
                        jsonObject.put("storeData", jsonArray);
                        requestBody = jsonObject.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.v("requestBody", requestBody);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideLoading();
                                    Log.d("Response", "Response: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        message = jsonObject.getString("message");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    qmap.clear();
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BedrollReturnAddStockActvity.this);
                                    builder.setMessage(response)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    submit.setEnabled(true);
                                                    submit.setBackgroundResource(R.drawable.button_orange_bg);
                                                    // Refresh or navigate
//                                                    onBackPressed();
                                                    startActivity(new Intent(getApplicationContext(), BedrollReturntoLaundrytFromBufferStockActivity.class));
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideLoading();
                                    submit.setEnabled(true);
                                    submit.setBackgroundResource(R.drawable.button_orange_bg);
                                    Log.e("Error", "Error: " + error.getMessage());
                                    Toast.makeText(BedrollReturnAddStockActvity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/json; charset=utf-8");
                            return params;
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return requestBody.getBytes();
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(BedrollReturnAddStockActvity.this);
                    requestQueue.add(stringRequest);
                }
            }
        });


        final DatePickerDialog.OnDateSetListener journeyDate1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy", Locale.US);
                String dt = "" + dayOfMonth;
                if (dt.length() == 1) dt = "0" + dt;
                String mnth = "" + (monthOfYear + 1);
                if (mnth.length() == 1) mnth = "0" + mnth;
                et_date.setText(year + "-" + mnth + "-" + dt);
            }
        };
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(BedrollReturnAddStockActvity.this, journeyDate1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                dpd.show();


            }
        });
        et_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(et_date.getText().toString())) ;
                callTab2();

            }
        });
        GetStoreType();
        callTab();
        depotList.add(0, "Select Laundry");
        depotAdapter = new ArrayAdapter<String>(BedrollReturnAddStockActvity.this, android.R.layout.simple_spinner_dropdown_item, depotList);
        sp_depot.setAdapter(depotAdapter);
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";
                    _checkVisiblity = false;
                    submit.setVisibility(View.GONE);
                    rqty.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    storequestionAPI = "http://lmsguwahati.projectrailway.in/api/getbufferItems";
                    submit.setVisibility(View.VISIBLE);
                    rqty.setVisibility(View.VISIBLE);
                    selectedDepot = depotList.get(i);
                    _checkVisiblity = true;
                    callTab2();
                }
                srl.setRefreshing(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                srl.setRefreshing(false);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (queStoreModel == null) {
                } else if (adapter == null || adapter.list == null || adapter.list.length() == 0) {
                    callTab2();
                } else {
                    srl.setRefreshing(false);
                }
                callTab2();
                srl.setRefreshing(false);
            }
        });
        Log.e("ResponceTab1", "in create");

    }

    private void callTab() {
        srl.setRefreshing(true);
        JSONObject object = new JSONObject();

        srl.setRefreshing(true);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, storequestionAPI, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("question_response", "" + response);
                        try {
                            questionArray = response.getJSONArray("laundry_items");
                            adapter.list = questionArray;
                            recyclerView.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srl.setRefreshing(false);
            }
        });
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        requestQueue1.add(objectRequest);
    }
    private void callTab2() {
        JSONObject object = new JSONObject();
        try {
            object.put("laundry_id", depot_id_list.get(sp_depot.getSelectedItemPosition()));
            object.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);

//            Toast.makeText(this, "id is "+depot_id_list.get(sp_depot.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "code "+userdataModel.mUserItems.get(0).mDepot_code, Toast.LENGTH_SHORT).show();
            srl.setRefreshing(true);

//            object.put("laundry_id","6");
//            object.put("depot_code", "GHY");

        } catch (JSONException e) {
            srl.setRefreshing(false);

            e.printStackTrace();
        }finally {
            srl.setRefreshing(false);
        }
//        srl.setRefreshing(true);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, storequestionAPI, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("question_response", "" + response);
                        try {
                            questionArray = response.getJSONArray("Summary_items");
                            adapter.list = questionArray;
                            recyclerView.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.d("TAG", "onResponse: "+e.getMessage());
                            srl.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srl.setRefreshing(false);
            }
        });
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        requestQueue1.add(objectRequest);
        srl.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Exit ? All data & progress will be lost!")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        qmap.clear();
                        BedrollReturnAddStockActvity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        qmap.clear();
        super.onDestroy();
    }

    public class SupplyCondAddBsradapter extends RecyclerView.Adapter<SupplyCondAddBsradapter.PenViewHolder> {

        private Context context;
        private JSONArray list;
        private List<String> reason_list = new ArrayList<>();
        private ArrayAdapter<String> reasonAdapter;
        private int shortfall_focus_position = -1;
        private boolean isReasonsFetched = false;

        public SupplyCondAddBsradapter(JSONArray list, Context context) {
            this.context = context;
            this.list = list;
            initializeReasonList(); // Initialize the reason list with default values
            fetchReasons(); // Fetch reasons once during initialization
        }

        @NonNull
        @Override
        public PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_condemation_bedroll, parent, false);
            return new PenViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PenViewHolder holder, final int position) {
            submit.setEnabled(false);
            holder.setIsRecyclable(false);

            try {
                final JSONObject jsonObject = list.getJSONObject(position);
                holder.tv_index.setText(String.valueOf(position + 1));
                holder.tv_ques.setText(jsonObject.getString("item_name"));

                if (holder.check == false){
                    holder.tv_item_qty.setVisibility(View.GONE);
                }else{
                    holder.tv_item_qty.setVisibility(View.VISIBLE);
                }

                holder.sp_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        String selectedItem = reason_list.get(pos);
//                        callTab2();
                        srl.setRefreshing(false);

                        if (_checkVisiblity){
                            holder.tv_item_qty.setVisibility(View.VISIBLE);
                            srl.setRefreshing(false);

                        }

                        try {

                            final JSONObject jsonObject = list.getJSONObject(position);
//                            holder.tv_item_qty.setVisibility(View.VISIBLE);
                            holder.tv_ques.setText(jsonObject.getString("item_name"));
                            holder.tv_item_qty.setText(String.valueOf(jsonObject.optInt("buffer_received")));

//                            adapter.notifyDataSetChanged();
                            String ques_id = jsonObject.getString("id");
                            QanswerData1 qanswerData = qmap.get(ques_id);
                            if (qanswerData != null) {
                                qanswerData.setReason(selectedItem);
                            } else {
                                String quantity = holder.et_quantity.getText().toString().trim();
                                QanswerData1 newQanswerData = new QanswerData1()
                                        .setQuestId(ques_id)
                                        .setQuantity(quantity)
                                        .setBuffer(jsonObject.optInt("buffer_received"))
                                        .setReason(selectedItem);
                                itemSelect(newQanswerData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            srl.setRefreshing(false);
                        }finally {
                            srl.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        srl.setRefreshing(false);
                    }
                });

                for (QanswerData1 qanswerData : qmap.values()) {
                    if (qanswerData.getQuest_id().equalsIgnoreCase(jsonObject.getString("id"))) {
                        holder.et_quantity.setText(qanswerData.getQuantity());
                        int position1 = reason_list.indexOf(qanswerData.getReason());
                        holder.sp_item.setSelection(position1 != -1 ? position1 : 0);
                    }
                }
                srl.setRefreshing(false);

                holder.et_quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (jsonObject == null) {
                            // Handle the case where jsonObject is null
                            submit.setEnabled(false);
                            return;
                        }

                        // Get buffer_received value safely
                        int myApiValue = jsonObject.optInt("buffer_received", -1); // Default to 0 if key is missing

                        String reason = holder.sp_item.getSelectedItem() != null ? holder.sp_item.getSelectedItem().toString().trim() : "0"; // Default value if null

                        // Try parsing reason to int
                        int reasonValue = 0;
                        try {
                            reasonValue = Integer.parseInt(reason);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            // Show error or handle gracefully
                        }

                        shortfall_focus_position = position;

                        // Get the quantity entered in EditText and handle empty input
                        String quantityStr = editable.toString().trim();
                        int enteredQuantity = 0; // Default value if empty
                        if (!quantityStr.isEmpty()) {
                            try {
                                enteredQuantity = Integer.parseInt(quantityStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                srl.setRefreshing(false);
                                // Handle invalid input gracefully
                            }finally {
                                srl.setRefreshing(false);

                            }
                        }

                        // Compare values
                        Log.d("ValueCheckMyApiValue", "myApiValue: " + myApiValue + ", reasonValue: " + reasonValue);
                        Log.d("ValueCheck2reason", "reason: " + reason);

                        if (myApiValue < enteredQuantity) {
                            Log.d("ComparisonCheck", "Condition Met: " + myApiValue + " < " + enteredQuantity);
                            submit.setEnabled(false);
                            Toast.makeText(context, "Maximum Quantity Not Allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            submit.setEnabled(true);
                            Log.d("ComparisonCheck", "Condition Not Met: " + myApiValue + " >= " + enteredQuantity);
                            try {
                                String ques_id = jsonObject.getString("id");
                                QanswerData1 qanswerData = new QanswerData1().setQuestId(ques_id).setQuantity(String.valueOf(enteredQuantity)).setReason(reason);
                                itemSelect(qanswerData);
                            } catch (Exception e) {
                                e.printStackTrace();
                                srl.setRefreshing(false);

                            }
                        }
                    }
                });

//                holder.et_quantity.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//
//
//                        if (jsonObject == null) {
//                            // Handle the case where jsonObject is null
//                            return;
//                        }
//
//                        // Get buffer_received value safely
//                        int myApiValue = jsonObject.optInt("buffer_received"); // Default to 0 if key is missing
//
//                        String reason = holder.sp_item.getSelectedItem() != null ? holder.sp_item.getSelectedItem().toString().trim() : "0"; // Default value if null
//
//                        // Try parsing reason to int
//                        int reasonValue = 0;
//                        try {
//                            reasonValue = Integer.parseInt(reason);
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                            // Show error or handle gracefully
//                        }
//
//                        shortfall_focus_position = position;
//
//                        // Compare values
//                        Log.d("ValueCheckMyApiValue", "myApiValue: " + myApiValue + ", reasonValue: " + reasonValue);
//                        Log.d("ValueCheck2reason", "reason: " + reason);
////                        Log.d("ValueCheck3Editable", "Editable Text: " + );
//
//
//                        if (myApiValue < Integer.parseInt(editable.toString().trim())) {
//                            Log.d("ComparisonCheck", "Condition Met: " + myApiValue + " > " + reasonValue);
//                            submit.setEnabled(false);
//                            Toast.makeText(context, "Wrong Input", Toast.LENGTH_SHORT).show();
//                        } else {
//                            submit.setEnabled(true);
//                            Log.d("ComparisonCheck", "Condition Not Met: " + myApiValue + " <= " + Integer.parseInt(editable.toString().trim()));
//                            try {
//                                String quantity = editable.toString().trim();
//                                String ques_id = jsonObject.getString("id");
//                                QanswerData1 qanswerData = new QanswerData1().setQuestId(ques_id).setQuantity(quantity).setReason(reason);
//                                itemSelect(qanswerData);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                });

                if (position == shortfall_focus_position) {
                    holder.et_quantity.requestFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
                srl.setRefreshing(false);

            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.length() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class PenViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index, tv_ques, tv_item_qty;
            EditText et_quantity;
            Spinner sp_item;

            private boolean check = false;

            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = itemView.findViewById(R.id.tv_qus);
                tv_item_qty = itemView.findViewById(R.id.tv_item_qty);
                et_quantity = itemView.findViewById(R.id.et_quantity);
                sp_item = itemView.findViewById(R.id.sp_item);

                reasonAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, reason_list);
                reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_item.setAdapter(reasonAdapter);
            }
        }

        public void itemSelect(QanswerData1 qanswerData) {
            qmap.put(qanswerData.getQuest_id(), qanswerData);
//            notifyDataSetChanged();
            Log.d("ItemSelect", "qmap size: " + qmap.size());
            Log.d("ItemSelect", "Item: " + qanswerData.getQuest_id() + ", Quantity: " + qanswerData.getQuantity() + ", Reason: " + qanswerData.getReason());
        }

        public void itemUnSelect(String itemId) {
            if (qmap.containsKey(itemId)) {
                qmap.remove(itemId);
                Log.d("ItemUnselect", "Removed from qmap: " + itemId);
            } else {
                Log.d("ItemUnselect", "Item ID not found: " + itemId);
            }
            notifyDataSetChanged();
        }

        private void initializeReasonList() {
            reason_list.add(0, "Select Reason");
        }

        private void fetchReasons() {
            if (isReasonsFetched) return;

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, REASON_API, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray array = response.getJSONArray("ReasonList");
                                reason_list.clear();
                                reason_list.add(0, "Select Reason");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    reason_list.add(obj.getString("reason"));
                                }

                                if (reasonAdapter != null) {
                                    reasonAdapter.notifyDataSetChanged();
                                }
                                isReasonsFetched = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error response
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(objectRequest);
        }


    }





    private void GetStoreType() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GET_DEPOT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("laundry_data");
                            if (array.length() > 0) {
                                depotList.clear();
                                depotList.add(0, "Select Laundry");
                                depot_id_list.clear();
                                depot_id_list.add(0, "Select Laundry");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    depotList.add(obj.getString("laundry_name"));
                                    depot_id_list.add(obj.getString("laundry_id"));
                                    laundryid = obj.getString("laundry_id");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_depot.setAdapter(new ArrayAdapter<String>(BedrollReturnAddStockActvity.this, android.R.layout.simple_spinner_dropdown_item, depotList));
                        sp_depot.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    private void uploadsignature(String path, int n) {
        showLoading("uploading sign" + n);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        String s = new String(response.data);
                        if (n == 1)
                            signatureresponse1 = s.substring(s.indexOf("/") + 1);
//                            else if (n == 2)
//                                signatureresponse2 = s.substring(s.indexOf("/")+1);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("sign", new DataPart(imagename + ".png", O.getBytes(path)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(BedrollReturnAddStockActvity.this);
        rQueue.add(volleyMultipartRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:
                Bundle bundle = data.getExtras();
                String status = bundle.getString("status");
                if (status.equalsIgnoreCase("done")) {
                    strSignatureFilePath1 = bundle.getString("signature_image_url");
                    iv_sign1.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath1));
                    uploadsignature(strSignatureFilePath1, 1);
                }
                break;
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
