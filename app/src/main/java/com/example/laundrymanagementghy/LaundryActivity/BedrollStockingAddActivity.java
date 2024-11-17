package com.example.laundrymanagementghy.LaundryActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.GetLaundryItem;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QanswerData;
import com.example.laundrymanagementghy.util.O;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BedrollStockingAddActivity extends AppCompatActivity {
    private final static String SubmitPenaltyData = "http://lmsguwahati.projectrailway.in/api/save_stockitems";
    private final static String questionAPI = "http://lmsguwahati.projectrailway.in/api/getLaundryItems";

    ImageView iv_backView, iv_calender;
    EditText et_date;
    Button submit;

    RecyclerView recyclerView;
    String requestBody;
    String message;
    SwipeRefreshLayout srl;
    TabLayout tabLayout;
    PenalityAdapter adapter;
    public JSONArray questionArray;
    final Calendar myCalendar = Calendar.getInstance();
    ProgressDialog mProgressDialog;
    int shortfall_focus_position = 0;
    int et_position = 0;

    View signature_layout;
    public HashMap<String, QanswerData> qmap = new HashMap<>();
    GetLaundryItem getLaundryItem = null;
    UiModeManager uiModeManager;
    UserDataModel userdataModel = null;
    View relativelaout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroll_stocking_add);
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        relativelaout = (RelativeLayout) findViewById(R.id.rlv2);
        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES){
            relativelaout.setBackgroundResource(R.drawable.shape_white);
        }
        srl = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.rv);
        et_date = findViewById(R.id.et_select_date);
        iv_calender = findViewById(R.id.iv_calender);
        submit = findViewById(R.id.btn_next_submit);
        iv_backView = findViewById(R.id.iv_backView);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setText("Submit");
        adapter = new PenalityAdapter(BedrollStockingAddActivity.this, questionArray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Function to print all data from qmap
//               printQmapData();

                if (adapter.areAllFieldsFilled()==true){
                    /*if (qmap.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Please give atleast one Score", Toast.LENGTH_LONG).show();
                    } else*/
                    if (TextUtils.isEmpty(et_date.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Please Select Date", Toast.LENGTH_SHORT).show();

                    } else {
                        showLoading("Uploading...");
                        submit.setEnabled(false);
                        submit.setBackgroundResource(R.drawable.button_green_bg);

                        JSONArray jsonArray = new JSONArray();
                        for (QanswerData qanswerData : qmap.values()) {
                            JSONObject jo = new JSONObject();
                            if(qanswerData.quantity_receipt.equals("") && qanswerData.price_rate.equals("")){
                                Toast.makeText(BedrollStockingAddActivity.this, "Quantity Reciept && Price rate are mandatory", Toast.LENGTH_SHORT).show();
                            }else{
                                try {

                                    jo.put("item_name", qanswerData.item_Name);
                                    jo.put("qty", qanswerData.quantity_receipt);
                                    jo.put("ref_no", qanswerData.po_number);
                                    jo.put("price_rate", qanswerData.price_rate);

                                    jsonArray.put(jo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("date", et_date.getText().toString());
                            jsonObject.put("supervisor_id", userdataModel.mUserItems.get(0).mLogin_id);
                            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
                            jsonObject.put("stockData", jsonArray);
                            requestBody = jsonObject.toString();
                        } catch (Exception e) {

                        }

                        Log.v("requestBody", requestBody);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();

                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            message = jsonObject.getString("message");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        qmap.clear();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(BedrollStockingAddActivity.this);
                                        //  builder.setTitle("Message")
                                        builder.setMessage("Stock data saved")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int a) {
                                                        Intent i = new Intent(BedrollStockingAddActivity.this, BedrollStocking.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                    }
                                                });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideLoading();
                                submit.setEnabled(true);
                                submit.setBackgroundResource(R.drawable.button_blue_bg);
                                Toast.makeText(BedrollStockingAddActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

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
                                    return null;
                                }
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(BedrollStockingAddActivity.this);
                        requestQueue.add(stringRequest);
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Quantity and Price are mandatory", Toast.LENGTH_SHORT).show();
                }






            }

//            public void printQmapData() {
//                if (qmap.isEmpty()) {
//                    System.out.println("The qmap is empty.");
//                } else {
//                    for (Map.Entry<String, QanswerData> entry : qmap.entrySet()) {
//                        String key = entry.getKey();
//                        QanswerData value = entry.getValue();
//                        System.out.println("Key: " + key + ", Value: " + value.toString());
//                        Log.d("ttaagg","Key: " + key + ", Value: " + value.toString());
//                    }
//                }
//            }
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
                DatePickerDialog dpd = new DatePickerDialog(BedrollStockingAddActivity.this, journeyDate1, myCalendar
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
                callTab();


            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getLaundryItem == null) {

                } else if (adapter == null || adapter.list == null || adapter.list.length() == 0) {
                    callTab();
                } else {
                    srl.setRefreshing(false);
                }
                callTab();
            }

        });
        Log.e("ResponceTab1", "in create");

    }


    private void callTab() {
        srl.setRefreshing(true);
//        JSONObject object =new JSONObject();
//        try {
//            object.put("cat_id",userdataModel.mUserItems.get(0).mLaundryID);
//
//        }catch (JSONException e){}

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, questionAPI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("q_response", "" + response);
                        try {
                            questionArray = response.getJSONArray("laundry_items");
                            adapter.list = questionArray;
                            adapter.notifyDataSetChanged();

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
                        BedrollStockingAddActivity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        qmap.clear();
        super.onDestroy();
    }

    public class PenalityAdapter extends RecyclerView.Adapter<PenalityAdapter.PenViewHolder> {
        private Context context;
        private JSONArray list;
        boolean isOnTextChanged = false;
        boolean dataFound = false;

        public PenalityAdapter(Context context, JSONArray list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public PenalityAdapter.PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_bedroll_stocking, parent, false);
            PenViewHolder vh = new PenViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final PenalityAdapter.PenViewHolder holder, final int pos) {
            holder.setIsRecyclable(false);
            final int position = pos;
            int totalIndex = list.length();


            UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                holder.et_po_number.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_quantity_receipt.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setHintTextColor(getResources().getColor(R.color.black));

                holder.et_po_number.setTextColor(getResources().getColor(R.color.black));
                holder.et_quantity_receipt.setTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setTextColor(getResources().getColor(R.color.black));


            } else {
                holder.et_po_number.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_quantity_receipt.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setHintTextColor(getResources().getColor(R.color.black));

                holder.et_po_number.setTextColor(getResources().getColor(R.color.black));
                holder.et_quantity_receipt.setTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setTextColor(getResources().getColor(R.color.black));


            }

            try {
                final JSONObject jsonObject = list.getJSONObject(position);
                holder.tv_index.setText((position + 1) + "");
                holder.tv_ques.setText(jsonObject.getString("item_name"));

                // Reset dataFound flag
                dataFound = false;

                // Loop through qmap and set data for matching quest_id
                for (QanswerData qanswerData : qmap.values()) {
                    if (qanswerData.quest_id.equalsIgnoreCase(jsonObject.getString("id"))) {

                        // Only update EditTexts if data is not the same to avoid unnecessary clearing
                        if (!qanswerData.quantity_receipt.equals(holder.et_quantity_receipt.getText().toString())) {
                            holder.et_quantity_receipt.setText(qanswerData.quantity_receipt);
                        }
                        if (!qanswerData.price_rate.equals(holder.et_price_rate.getText().toString())) {
                            holder.et_price_rate.setText(qanswerData.price_rate);
                        }
                        if (!qanswerData.po_number.equals(holder.et_po_number.getText().toString())) {
                            holder.et_po_number.setText(qanswerData.po_number);
                        }

                        // Set flag to indicate data was found
                        dataFound = true;
                        break;
                    }
                }

                // If no matching data found, keep the fields unchanged to prevent clearing valid input
                if (!dataFound) {
                    if (holder.et_quantity_receipt.getText().toString().isEmpty()) {
                        holder.et_quantity_receipt.setText("");
                    }
                    if (holder.et_price_rate.getText().toString().isEmpty()) {
                        holder.et_price_rate.setText("");
                    }
                    if (holder.et_po_number.getText().toString().isEmpty()) {
                        holder.et_po_number.setText("");
                    }
                }

                // Attach TextWatchers
                holder.et_quantity_receipt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isOnTextChanged = true;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        et_position = 0;
                        shortfall_focus_position = position;
                        handleTextChange(holder.et_quantity_receipt.getText().toString().trim(), holder, jsonObject, position,totalIndex);

                    }
                });

                holder.et_po_number.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isOnTextChanged = true;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        et_position = 1;
                        shortfall_focus_position = position;
                        handleTextChange(holder.et_po_number.getText().toString().trim(), holder, jsonObject, position,totalIndex);
                    }
                });

                holder.et_price_rate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isOnTextChanged = true;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        et_position = 2;
                        shortfall_focus_position = position;
                        handleTextChange(holder.et_price_rate.getText().toString().trim(), holder, jsonObject, position,totalIndex);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Focus handling
            if (position == shortfall_focus_position) {
                if (et_position == 0) {
                    holder.et_quantity_receipt.requestFocus();
                } else if (et_position == 1) {
                    holder.et_po_number.requestFocus();
                } else if (et_position == 2) {
                    holder.et_price_rate.requestFocus();
                }
            }

        }
        public boolean areAllFieldsFilled() {
            for (int i = 0; i < list.length(); i++) {
                try {
                    JSONObject jsonObject = list.getJSONObject(i);
                    QanswerData qanswerData = qmap.get(jsonObject.getString("id"));
                    if (qanswerData == null || qanswerData.quantity_receipt.isEmpty() || qanswerData.price_rate.isEmpty()) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

//        public boolean areAllFieldsFilled() {
//            for (int i = 0; i < list.length(); i++) {
//                try {
//                    JSONObject jsonObject = list.getJSONObject(i);
//                    // Retrieve data from qmap ya directly from EditText fields
//                    String quantityReceipt = qmap.get(jsonObject.getString("id")).quantity_receipt;
//                    String poNumber = qmap.get(jsonObject.getString("id")).po_number;
//                    String priceRate = qmap.get(jsonObject.getString("id")).price_rate;
//
//                    // Check if any field is empty
//                    if (quantityReceipt.isEmpty() || poNumber.isEmpty() || priceRate.isEmpty()) {
//                        return false;  // Agar koi field empty hai, return false
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return true;  // Sab fields filled hain
//        }


        @Override
        public int getItemCount() {
            if (list != null)
                return list.length();
            else
                return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class PenViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index, tv_ques;
            EditText et_quantity_receipt, et_po_number, et_price_rate;

            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                et_quantity_receipt = itemView.findViewById(R.id.et_quantity_receipt);
                et_price_rate = itemView.findViewById(R.id.et_price_rate);
                et_po_number = itemView.findViewById(R.id.et_po_number);


            }
        }
    }

    private void handleTextChange(String trim, PenalityAdapter.PenViewHolder holder, JSONObject jsonObject, int position, int totalIndex) { //


        try {
            String ques_id = jsonObject.getString("id");
            String item_name = jsonObject.getString("item_name");

            String po_number = holder.et_po_number.getText().toString().trim();
            String price_rate = holder.et_price_rate.getText().toString().trim();
            String quantity_receipt = holder.et_quantity_receipt.getText().toString().trim();

            if (!trim.isEmpty()) {
                itemselect(new QanswerData().setQuestId(ques_id)
                        .SetItemName(item_name)
                        .SetPonumber(po_number)
                        .SetQuantityReceipt(quantity_receipt)
                        .SetPriceRate(price_rate));
            } else {
                itemUnSelect(ques_id);
            }

            adapter.notifyItemChanged(position, jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void itemselect(QanswerData qanswerData) {
        // Insert or update the selected item in the map
        qmap.put(qanswerData.quest_id, qanswerData);

        // Find the position of the updated item in the list and notify that only this item changed
        int position = findItemPosition(qanswerData.quest_id);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }


    }

    public void itemUnSelect(String itemId) {
        // Remove the item from the map
        qmap.remove(itemId);

        // Find the position of the removed item in the list and notify the adapter
        int position = findItemPosition(itemId);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }
    }
    private int findItemPosition(String questId) {
        JSONArray list = new JSONArray();
        for (int i = 0; i < list.length(); i++) {  // Original list ko use karen
            try {
                JSONObject jsonObject = list.getJSONObject(i);
                if (jsonObject.getString("id").equals(questId)) {
                    return i;  // Return the position
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;  // Return -1 if not found
    }

//    private int findItemPosition(String questId) {
//        // Iterate through the list to find the matching quest ID and return the position
//        JSONArray list = new JSONArray();
//        for (int i = 0; i < list.length(); i++) {
//            try {
//                JSONObject jsonObject = list.getJSONObject(i);
//                if (jsonObject.getString("id").equals(questId)) {
//                    return i;  // Return the position
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return -1;  // Return -1 if not found
//    }

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

