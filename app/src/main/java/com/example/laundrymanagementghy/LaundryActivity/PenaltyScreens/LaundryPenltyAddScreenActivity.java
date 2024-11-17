package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.laundrymanagementghy.Amodel.GetLaundryItem;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QanswerData;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaundryPenltyAddScreenActivity extends AppCompatActivity {

    ImageView v_back;
    private final static String SubmitPenaltyData = "http://lmsguwahati.projectrailway.in/api/save_laundry_penalties";
    private final static String questionAPI = "http://lmsguwahati.projectrailway.in/api/penalty_questions";
    private final static String GET_DEPOT_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_depots";
    UserDataModel userDataModel;
    UiModeManager uiModeManager;
    public final static String STORING_Image = "http://lmsguwahati.projectrailway.in/Api/upload_signature";

    Spinner sp_depot;
    public String selectedDepot = "", depot;
    UserDataModel userdataModel;
    AlertDialog dialog;
    ProgressDialog mProgressDialog;
    ArrayList<String> depot_list = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayAdapter<String> adapter_depot;
    ImageView signclick1, iv_sign1, signclick2, iv_sign2;
    public String strSignatureFilePath1 = "", signatureresponse1 = null;
    public String strSignatureFilePath2 = "", signatureresponse2 = null;

    public static final int SIGNATURE_ACTIVITY = 1;
    public static final int SIGNATURE_ACTIVITY2 = 2;


    ImageView iv_backView;
    EditText et_remark_panalty;
    Button submit;
    RecyclerView recyclerView;
    TextView tvRate;
    String requestBody;
    String message;
    SwipeRefreshLayout srl;
    LaundryPenltyAddScreenActivity.PenalityAdapter adapter;
    public JSONArray questionArray;

    public HashMap<String, QanswerData> qmap = new HashMap<>();
    QanswerData qanswerData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_penlty_add_screen);

        try {
            depot = getIntent().getStringExtra("depot_code");
            userDataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        IdFindingInit(uiModeManager);
        recyclerView = findViewById(R.id.rv);
        callTab();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PenalityAdapter(LaundryPenltyAddScreenActivity.this, questionArray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (qanswerData == null) {
                } else if (adapter == null || adapter.list == null || adapter.list.length() == 0) {
                    callTab();
                } else {
                    srl.setRefreshing(false);
                }
                callTab();
                srl.setRefreshing(false);
            }
        });


//        v_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        try {
            depot = getIntent().getStringExtra("depot_code");

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GetDepotType();
        depot_list.add(0, "Select Depot...");
        adapter_depot = new ArrayAdapter<String>(LaundryPenltyAddScreenActivity.this, android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_depot.setAdapter(adapter_depot);
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depot_list.get(i);
                    Log.e("selectedDepot", selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String status = bundle.getString("status");
            switch (requestCode) {
                case SIGNATURE_ACTIVITY:
                    Log.d("SwitchCase", "SIGNATURE_ACTIVITY called");
                    if (status.equalsIgnoreCase("done")) {
                        strSignatureFilePath1 = bundle.getString("signature_image_url");
                        iv_sign1.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath1));
                        uploadsignature(strSignatureFilePath1, 1);
                    }
                    break;

                case SIGNATURE_ACTIVITY2:
                    Log.d("SwitchCase", "SIGNATURE_ACTIVITY2 called");
                    if (status.equalsIgnoreCase("done")) {
                        strSignatureFilePath2 = bundle.getString("signature_image_url");
                        iv_sign2.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath2));
                        uploadsignature(strSignatureFilePath2, 2);
                    }
                    break;
            }
        }
    }

    private void IdFindingInit(UiModeManager uiModeManager) {
        // Checking for Night Mode or Light Mode

        et_remark_panalty = findViewById(R.id.et_remark_panalty);
        srl = findViewById(R.id.srl);
        iv_backView = findViewById(R.id.iv_backView);

        signclick1 = findViewById(R.id.click1);
        iv_sign1 = findViewById(R.id.img_sign1);
        signclick2 = findViewById(R.id.click2);
        iv_sign2 = findViewById(R.id.img_sign2);

        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaundryPenltyAddScreenActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        signclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaundryPenltyAddScreenActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY2);
            }
        });

        submit = findViewById(R.id.bt_add_panalty);
        sp_depot = findViewById(R.id.sp_depot);
        v_back = findViewById(R.id.v_back);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if Laundry is selected
                if (sp_depot.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select Laundry", Toast.LENGTH_SHORT).show();
                }
                else if (signatureresponse1 == null) {
                    Toast.makeText(getApplicationContext(), "Supervisor Signature is mandatory", Toast.LENGTH_SHORT).show();
                }
                else if (signatureresponse2 == null) {
                    Toast.makeText(getApplicationContext(), "SSE/JE Signature is mandatory", Toast.LENGTH_SHORT).show();
                }
                else if (signatureresponse1 == null && signatureresponse2 == null) {
                    Toast.makeText(getApplicationContext(), "Supervisor and SSE/JE signature are mandatory", Toast.LENGTH_SHORT).show();
                } else {
                    // Show loading indicator
                    showLoading("Uploading...");
                    submit.setEnabled(false);  // Disable submit button to prevent multiple clicks
                    submit.setBackgroundResource(R.drawable.button_green_bg);

                    // Prepare JSON request body
                    JSONArray jsonArray = new JSONArray();
                    for (QanswerData qanswerData : qmap.values()) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("item_id", qanswerData.quest_id);
                            jo.put("item_name", qanswerData.questionname);
                            jo.put("qty", qanswerData.quantity_receipt);
                            jo.put("rate", qanswerData.price_rate);
                            jo.put("amount", qanswerData.penlatyamount);
                            jsonArray.put(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
                        jsonObject.put("remark", et_remark_panalty.getText().toString());
                        jsonObject.put("signature", signatureresponse1);
                        jsonObject.put("signature1", signatureresponse2);
                        jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
                        jsonObject.put("Penalty_Data", jsonArray);

                        requestBody = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v("requestBody", requestBody);

                    // Make API request using Volley
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideLoading();  // Hide loading indicator
                                    Log.e("msg", response);
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        message = jsonObject.getString("message");
                                        Log.e("msg1", message);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // Clear the data map after the request
                                    qmap.clear();

                                    // Show success dialog
                                    if (!isFinishing() && !isDestroyed()) {
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LaundryPenltyAddScreenActivity.this);
                                        builder.setMessage("Penalty data saved")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int a) {
//                                                        finish();
//                                                        onBackPressed();
                                                        startActivity(new Intent(getApplicationContext(), LaundryPenltryScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    }
                                                });
                                        android.app.AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideLoading();  // Hide loading indicator
                                    submit.setEnabled(true);  // Re-enable submit button
                                    submit.setBackgroundResource(R.drawable.button_blue_bg);
                                    Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
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

                    // Add the request to the Volley request queue
                    RequestQueue requestQueue = Volley.newRequestQueue(LaundryPenltyAddScreenActivity.this);
                    requestQueue.add(stringRequest);
                }
            }
        });

    }

    private void GetDepotType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GET_DEPOT_TYPE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("depot_data");
                            depot_list.clear();
                            depot_list.add(0, "Select Depot");
                            depot_id_list.clear();
                            depot_id_list.add(0, "Select Depot");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                depot_list.add(obj.getString("depot_code"));
                                depot_id_list.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_depot.setAdapter(new ArrayAdapter<String>(LaundryPenltyAddScreenActivity.this, android.R.layout.simple_spinner_dropdown_item, depot_list));
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
                            questionArray = response.getJSONArray("GetPenaltyQuestions");
                            adapter.list = questionArray;
                            adapter.notifyDataSetChanged();
                            srl.setRefreshing(false);

                        } catch (Exception e) {
                            Log.e("Ex",e.toString());
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


    public class PenalityAdapter extends RecyclerView.Adapter<LaundryPenltyAddScreenActivity.PenalityAdapter.PenViewHolder> {
        private Context context;
        private JSONArray list;
        private String amountValue;
        private boolean check=false;

        //price rate jo field hai usame multiply karake result show hota hai second wale editText mai

        public PenalityAdapter(Context context, JSONArray list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public LaundryPenltyAddScreenActivity.PenalityAdapter.PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_bedroll_stocking, parent, false);
            LaundryPenltyAddScreenActivity.PenalityAdapter.PenViewHolder vh = new LaundryPenltyAddScreenActivity.PenalityAdapter.PenViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final LaundryPenltyAddScreenActivity.PenalityAdapter.PenViewHolder holder, final int pos) {
            holder.setIsRecyclable(false);
            final int position = pos;

            holder.et_quantity_receipt.setInputType(InputType.TYPE_CLASS_NUMBER);
            holder.et_ponumber.setVisibility(View.GONE);
            MyThemeTextColor(holder);

            try {
                final JSONObject jsonObject = list.getJSONObject(position);

                GetPanaltyQuestionItem.QuestionItem item = new GetPanaltyQuestionItem.QuestionItem();
                item.id = jsonObject.getString("id");
                item.questionName = jsonObject.getString("question_name");
                item.penaltyAmount = jsonObject.getString("penalty_amount");


//                if (!qmap.containsKey(item.id)) {
//                    qmap.put(item.id, new QanswerData().SetItemName(item.questionName).setQuestId(item.id));
//                }

                holder.tv_index.setText((position + 1) + "");
                holder.tv_index.setVisibility(View.GONE);
                holder.tv_ques.setText(item.questionName);

                // qmap mein agar item already hai toh uski values set karenge
                QanswerData qanswerData = qmap.get(item.id);
                holder.et_quantity_receipt.setText(qanswerData != null ? qanswerData.quantity_receipt : "");
                holder.et_price_rate.setText(qanswerData != null ? qanswerData.penlatyamount : "");

                holder.et_price_rate.setEnabled(false);
                holder.et_quantity_receipt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        handleTextChange(item.penaltyAmount,holder.et_quantity_receipt.getText().toString().trim(), holder, jsonObject, position);
                        int total = holder.et_quantity_receipt.length();
                        if (total==0){
                            holder.et_price_rate.setText("0");
                        }else{
                            holder.et_quantity_receipt.setSelection(s.length());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                srl.setRefreshing(false);
            }

        }

        private void MyThemeTextColor(PenViewHolder holder) {

            UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                holder.et_quantity_receipt.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setHintTextColor(getResources().getColor(R.color.black));

                holder.et_quantity_receipt.setTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setTextColor(getResources().getColor(R.color.black));
            } else {
                holder.et_quantity_receipt.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setHintTextColor(getResources().getColor(R.color.black));
                holder.et_quantity_receipt.setTextColor(getResources().getColor(R.color.black));
                holder.et_price_rate.setTextColor(getResources().getColor(R.color.black));
            }

        }

//        public boolean areAllFieldsFilled() {
//            for (int i = 0; i < list.length(); i++) {
//                try {
//                    JSONObject jsonObject = list.getJSONObject(i);
//                    QanswerData qanswerData = qmap.get(jsonObject.getString("id"));
//                    if (qanswerData == null || qanswerData.quantity_receipt.isEmpty() || qanswerData.price_rate.isEmpty()) {
//                        return false;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return true;
//        }

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

        private void handleTextChange(String penaltyAmount, String trim, PenViewHolder holder, JSONObject jsonObject, int position) {
            try {
                String ques_id = jsonObject.getString("id");
                String item_name = jsonObject.getString("question_name");
                String item_amount = jsonObject.getString("penalty_amount");

                String quantity_receipt = holder.et_quantity_receipt.getText().toString().trim();
                int total = Integer.parseInt(quantity_receipt) * Integer.parseInt(item_amount);
                if (!trim.isEmpty()) {
                    itemselect(new QanswerData().setQuestId(ques_id)
                            .SetQuestionName(item_name)
                            .SetPenaltyAmount(String.valueOf(total) == "0" ? item_amount : String.valueOf(total))
                            .SetQuantityReceipt(quantity_receipt)
                            .SetPriceRate(item_amount));
                } else {
                    // Agar text empty hai, toh item unselect karte hain
                    itemUnSelect(ques_id);
                }
                adapter.notifyItemChanged(position, jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
            EditText et_quantity_receipt, et_price_rate,et_ponumber;
            View linearView;

            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                et_quantity_receipt = itemView.findViewById(R.id.et_quantity_receipt);
                et_price_rate = itemView.findViewById(R.id.et_price_rate);
                et_ponumber = itemView.findViewById(R.id.et_po_number);
                linearView = itemView.findViewById(R.id.linearLayou3);


            }
        }
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
                        LaundryPenltyAddScreenActivity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        qmap.clear();
        super.onDestroy();
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
                        else if (n == 2)
                            signatureresponse2 = s.substring(s.indexOf("/") + 1);
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
        RequestQueue rQueue = Volley.newRequestQueue(LaundryPenltyAddScreenActivity.this);
        rQueue.add(volleyMultipartRequest);
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


    public void showConfirmationDialog(String strMessage, UiModeManager uiModeManager) {
        final Dialog dialog = new Dialog(LaundryPenltyAddScreenActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvtitle = dialog.findViewById(R.id.confirmMessageTitle);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);

        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            tvtitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
            tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
        }

        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(LaundryPenltyAddScreenActivity.this, LaundryPenltryScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}