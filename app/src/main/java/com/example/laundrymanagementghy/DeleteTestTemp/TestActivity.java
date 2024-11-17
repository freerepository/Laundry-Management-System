package com.example.laundrymanagementghy.DeleteTestTemp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.Depot_PanaltyScreen;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QanswerData;
import com.example.laundrymanagementghy.util.O;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    ImageView v_back;
    private final static String SubmitPenaltyData = "http://lmsguwahati.projectrailway.in/api/save_penalties";
    private final static String questionAPI = "http://lmsguwahati.projectrailway.in/api/getLaundryItems";
    private final static String GET_DEPOT = "http://lmsguwahati.projectrailway.in/Api/get_all_laundry";
    UserDataModel userDataModel;
    UiModeManager uiModeManager;
    public final static String STORING_Image = "http://lmsguwahati.projectrailway.in/Api/upload_signature";

    Spinner sp_depot;
    public String selectedDepot = "", depot;
    UserDataModel userdataModel;
    AlertDialog dialog;
    ProgressDialog mProgressDialog;
    ArrayList<String> depotList = new ArrayList<>(), depot_id_list = new ArrayList<>();
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
    String requestBody;
    String message;
    SwipeRefreshLayout srl;
    TestActivity.PenalityAdapter adapter;
    public JSONArray questionArray;

    public HashMap<String, QanswerData> qmap = new HashMap<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_test);



    }
    public class PenalityAdapter extends RecyclerView.Adapter<TestActivity.PenalityAdapter.PenViewHolder> {
        private Context context;
        private JSONArray list;
        boolean isOnTextChanged = false;
        boolean dataFound = false;

        List<GetLaundryItem.CatItem> catItemList = new ArrayList<>();


        public PenalityAdapter(Context context, JSONArray list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public TestActivity.PenalityAdapter.PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_bedroll_stocking, parent, false);
            TestActivity.PenalityAdapter.PenViewHolder vh = new TestActivity.PenalityAdapter.PenViewHolder(view);
            return vh;
        }

        // Method to calculate and set the result in et_po_number based on quantity and rate fields
        private void getCalculation(String qtyText, String rateText, EditText amountField) {
            try {
                // Check if both fields are not empty, else set them to 0
                int qty = qtyText.isEmpty() ? 0 : Integer.parseInt(qtyText);
                int rate = rateText.isEmpty() ? 0 : Integer.parseInt(rateText);

                // Calculate the total amount
                int result = qty * rate;

                // Set the calculated result to the amount field (et_po_number)
                amountField.setText(String.valueOf(result));
            } catch (NumberFormatException e) {
                // If invalid input is encountered, set default value 0 to the amount field
                amountField.setText("0");
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final TestActivity.PenalityAdapter.PenViewHolder holder, final int pos) {
            holder.setIsRecyclable(false);
            final int position = pos;
            int totalIndex = list.length();

            holder.et_quantity_receipt.setInputType(InputType.TYPE_CLASS_NUMBER);
            holder.et_po_number.setInputType(InputType.TYPE_CLASS_NUMBER);




            // Setting color based on UI mode (day/night)
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
                // Create an instance of CatItem and populate it with JSON data
                final JSONObject jsonObject = list.getJSONObject(position);
                GetLaundryItem.CatItem item = new GetLaundryItem.CatItem();
                item.mCatId = jsonObject.getString("id");
                item.mCat_title = jsonObject.getString("item_name");
                item.mQty = jsonObject.getString("qty");
                item.mPrice_rate = jsonObject.getString("price_rate");

                // Displaying item name in the ViewHolder
                holder.tv_index.setText((position + 1) + "");
                holder.tv_index.setVisibility(View.GONE);
                holder.tv_ques.setText(item.mCat_title);

                // Set up EditText fields based on previous data
                dataFound = false;
                for (QanswerData qanswerData : qmap.values()) {
                    if (qanswerData.quest_id.equalsIgnoreCase(item.mCatId)) {
                        if (!qanswerData.quantity_receipt.equals(holder.et_quantity_receipt.getText().toString())) {
                            holder.et_quantity_receipt.setText(qanswerData.quantity_receipt);
                        }

                        if (!qanswerData.price_rate.equals(holder.et_price_rate.getText().toString())) {
                            holder.et_price_rate.setText(qanswerData.price_rate);
                        }
                        if (!qanswerData.po_number.equals(holder.et_po_number.getText().toString())) {
                            holder.et_po_number.setText(qanswerData.po_number);
                        }
                        dataFound = true;

//                        holder.et_quantity_receipt.setText(qanswerData.quantity_receipt);
//                        holder.et_quantity_receipt.setText(qanswerData.quantity_receipt);
//                        holder.et_quantity_receipt.setText(qanswerData.quantity_receipt);
//
////                        holder.et_quantity_receipt.setText(qanswerData.quantity_receipt.isEmpty() ? "0" : qanswerData.quantity_receipt);
////                        holder.et_price_rate.setText(qanswerData.price_rate.isEmpty() ? "0" : qanswerData.price_rate);
////                        holder.et_po_number.setText(qanswerData.po_number.isEmpty() ? "0" : qanswerData.po_number);
////                        dataFound = true;


                        break;
                    }
                }

                if (!dataFound) {
                    holder.et_quantity_receipt.setText("");
                    holder.et_price_rate.setText("");
                    holder.et_po_number.setText("");
                }

                // Attach TextWatchers for quantity and rate fields
                holder.et_price_rate.setEnabled(false);
                holder.et_quantity_receipt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isOnTextChanged = true;
                        getCalculation(holder.et_quantity_receipt.getText().toString().trim(), holder.et_po_number.getText().toString().trim(), holder.et_price_rate);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        handleTextChange(holder.et_quantity_receipt.getText().toString().trim(), holder, jsonObject, position, totalIndex);
                    }
                });
                holder.et_po_number.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        getCalculation(holder.et_quantity_receipt.getText().toString().trim(), holder.et_po_number.getText().toString().trim(), holder.et_price_rate);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        handleTextChange(holder.et_price_rate.getText().toString().trim(), holder, jsonObject, position, totalIndex);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Removed focus-related code to prevent automatic focusing on any EditText
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

        private void handleTextChange(String trim, TestActivity.PenalityAdapter.PenViewHolder holder, JSONObject jsonObject, int position, int totalIndex) { //


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
            View linearView;

            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                et_quantity_receipt = itemView.findViewById(R.id.et_quantity_receipt);
                et_price_rate = itemView.findViewById(R.id.et_price_rate);
                et_po_number = itemView.findViewById(R.id.et_po_number);
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
                        TestActivity.super.onBackPressed();
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
        RequestQueue rQueue = Volley.newRequestQueue(TestActivity.this);
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
        final Dialog dialog = new Dialog(TestActivity.this);
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
                Intent intent = new Intent(TestActivity.this, Depot_PanaltyScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


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
                Intent intent = new Intent(TestActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        signclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, CaptureSignatureActivity.class);
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

                // Function to print all data from qmap
//               printQmapData();

                if (adapter.areAllFieldsFilled() != true) {
                    if (sp_depot.getSelectedItemPosition()==0) {
                        Toast.makeText(getApplicationContext(), "Please Select Laundry", Toast.LENGTH_SHORT).show();
                    } else if(signatureresponse1 == null){
                        Toast.makeText(TestActivity.this, "Supervisor Signature is mandatory", Toast.LENGTH_SHORT).show();
                    }else if (signatureresponse2 == null){
                        Toast.makeText(TestActivity.this, "SSE/JE Signature is mandatory", Toast.LENGTH_SHORT).show();
                    }else if (signatureresponse1 == null  && signatureresponse2 == null){
                        Toast.makeText(TestActivity.this, "Supervisor and SSE/JE signature is mandatory", Toast.LENGTH_SHORT).show();
                    }else{
                        showLoading("Uploading...");
                        submit.setEnabled(false);
                        submit.setBackgroundResource(R.drawable.button_green_bg);
//
//                        Toast.makeText(TestActivity.this, "Signature 1 "+signatureresponse1, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(TestActivity.this, "Signature 2 "+signatureresponse2, Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = new JSONArray();
                        for (QanswerData qanswerData : qmap.values()) {
                            JSONObject jo = new JSONObject();
                            if (qanswerData.quantity_receipt.equals("") && qanswerData.price_rate.equals("")) {
                                Toast.makeText(TestActivity.this, "Quantity Reciept && Price rate are mandatory", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    jo.put("item_id", qanswerData.cat_id); //ye change karana bad m
                                    jo.put("item_name", qanswerData.item_Name);
                                    jo.put("qty", qanswerData.quantity_receipt);
                                    jo.put("rate", qanswerData.po_number);
                                    jo.put("amount", qanswerData.price_rate);
                                    Log.e("dataak", "data : " + qanswerData.item_Name + " " + qanswerData.quantity_receipt + " " + qanswerData.po_number + " " + qanswerData.price_rate);
                                    jsonArray.put(jo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                        } catch (Exception e) {

                        }

                        Log.v("requestBody", requestBody);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();
                                        Log.e("msg", response);
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            message = jsonObject.getString("message");
                                            Log.e("msg1", message);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        qmap.clear();
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TestActivity.this);
                                        //  builder.setTitle("Message")
                                        builder.setMessage("Stock data saved")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int a) {
                                                        Intent i = new Intent(TestActivity.this, Depot_PanaltyScreen.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                    }
                                                });

                                        android.app.AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideLoading();
                                submit.setEnabled(true);
                                submit.setBackgroundResource(R.drawable.button_blue_bg);
                                Toast.makeText(TestActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

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
                        RequestQueue requestQueue = Volley.newRequestQueue(TestActivity.this);
                        requestQueue.add(stringRequest);
                    }

                } else {
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

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_depot.setAdapter(new ArrayAdapter<String>(TestActivity.this, android.R.layout.simple_spinner_dropdown_item, depotList));
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

}