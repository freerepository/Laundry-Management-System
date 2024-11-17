package com.example.laundrymanagementghy.StoreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QanswerData;
import com.example.laundrymanagementghy.resoures.QueStoreModel;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CondemnetBsrDepotActivity extends AppCompatActivity {
    private final static String storequestionAPI = "http://lmsguwahati.projectrailway.in/Api/get_storeItems";

    public final static String STORING_Image = "http://lmsguwahati.projectrailway.in/Api/upload_signature";
    private final static String get_depots = "http://lmsguwahati.projectrailway.in/Api/get_depots";
    private final static String SubmitPenaltyData = "http://lmsguwahati.projectrailway.in/Api/save_storeSent";
    ImageView iv_backView,iv_calender;
    EditText et_date;
    Button submit;
    Spinner sp_depot;
   CondemendDepotadapter adapter;
    RecyclerView recyclerView;
    UserDataModel userdataModel;
    String requestBody;
    String message;
    QueStoreModel queStoreModel=null;
    SwipeRefreshLayout srl;
    public JSONArray questionArray;
    final Calendar myCalendar = Calendar.getInstance();
    LinearLayout signaturelayout;
    public HashMap<String, QanswerData> qmap = new HashMap<>();
    ImageView signclick1, iv_sign1;
    public String  strSignatureFilePath1 = "", signatureresponse1;
    public static final int SIGNATURE_ACTIVITY = 1;

    ProgressDialog mProgressDialog;
    int shortfall_focus_position=0;
    ArrayList<String> depotList = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayAdapter<String> depotAdapter;
    public String selectedDepot="",depot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condemnet_bsr_depot);
        queStoreModel=(QueStoreModel) getIntent().getSerializableExtra("qdata");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        srl=findViewById(R.id.srl);
        recyclerView=findViewById(R.id.rv);
        et_date=findViewById(R.id.et_select_date);
        iv_calender=findViewById(R.id.iv_calender);
        submit=findViewById(R.id.btn_next_submit);

        signaturelayout = findViewById(R.id.signature_layout);
        signclick1 = findViewById(R.id.click1);
        iv_sign1 = findViewById(R.id.img_sign1);
        sp_depot=findViewById(R.id.sp_depot);

        iv_backView=findViewById(R.id.iv_backView);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CondemnetBsrDepotActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });


        submit.setText("Submit");
        adapter = new CondemendDepotadapter(questionArray, CondemnetBsrDepotActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (qmap.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please give atleast one Score", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please Select Date", Toast.LENGTH_SHORT).show();
                } else if (sp_depot.getSelectedItemPosition() == 0) {
                    Toast.makeText(CondemnetBsrDepotActivity.this, "Select Depot", Toast.LENGTH_LONG).show();

                } else {
                    showLoading("Uploading...");
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.button_orange_bg);

                    JSONArray jsonArray = new JSONArray();
                    for (QanswerData qcmableanswerData : qmap.values()) {
                        JSONObject jo = new JSONObject();
                        try {

                            jo.put("item_description", qcmableanswerData.quest_id);
                            jo.put("pl_no", qcmableanswerData.pl_number);
                            jo.put("qty", qcmableanswerData.quantity);

                            jsonArray.put(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("mt_id","14");
                        jsonObject.put("store_id", userdataModel.mUserItems.get(0).mLogin_id);
                        jsonObject.put("date", et_date.getText().toString());
                        jsonObject.put("received_from",selectedDepot);
                        jsonObject.put("signature", signatureresponse1);
                        jsonObject.put("storeData", jsonArray);
                        requestBody = jsonObject.toString();
                    } catch (Exception e) {

                    }

                    Log.v("requestBody", requestBody);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitPenaltyData,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideLoading();

                                    JSONObject jsonObject= null;
                                    try {
                                        jsonObject = new JSONObject(response);
                                        message=jsonObject.getString("message");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    qmap.clear();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CondemnetBsrDepotActivity.this);
                                    //  builder.setTitle("Message")
                                    builder.setMessage(response)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int a) {
                                                    Intent i = new Intent(CondemnetBsrDepotActivity.this, StoreDashboard.class);
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
                            Toast.makeText(CondemnetBsrDepotActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

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
                    RequestQueue requestQueue = Volley.newRequestQueue(CondemnetBsrDepotActivity.this);
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
                DatePickerDialog dpd = new DatePickerDialog(CondemnetBsrDepotActivity.this, journeyDate1, myCalendar
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

        GetDepotType();
        depotList.add(0, "Select Train.");
        depotAdapter = new ArrayAdapter<String>(CondemnetBsrDepotActivity.this, android.R.layout.simple_spinner_dropdown_item, depotList);
        sp_depot.setAdapter(depotAdapter);
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depotList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(queStoreModel==null){
                }else if(adapter==null || adapter.list==null || adapter.list.length()==0){
                    callTab();

                }else{

                    srl.setRefreshing(false);
                }
                callTab();
                srl.setRefreshing(false);
            }
        });
        Log.e("ResponceTab1","in create");

    }

    private void GetDepotType() {
        JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, get_depots, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("depot_data");
                            if (array.length() > 0) {
                                depotList.clear();
                                depotList.add(0, "Select Depot");
                                depot_id_list.clear();
                                depot_id_list.add(0, "Select Depot");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    depotList.add(obj.getString("depot_name"));
                                    depot_id_list.add(obj.getString("depot_id"));

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_depot.setAdapter(new ArrayAdapter<String>(CondemnetBsrDepotActivity.this, android.R.layout.simple_spinner_dropdown_item, depotList));
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
        JSONObject object =new JSONObject();

        srl.setRefreshing(true);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, storequestionAPI, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("q_response",""+response);
                        try {
                            questionArray = response.getJSONArray("store_items");
                            adapter.list=questionArray;
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }catch (Exception e){}

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
                        CondemnetBsrDepotActivity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        qmap.clear();
        super.onDestroy();
    }

    public class CondemendDepotadapter extends RecyclerView.Adapter<CondemendDepotadapter.PenViewHolder> {

        private Context context;
        private JSONArray list;

        public CondemendDepotadapter(JSONArray list, Context context) {
            this.context = context;
            this.list = list;

        }

        @NonNull
        @Override
        public CondemendDepotadapter.PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_gsd_depot,parent,false);
          PenViewHolder vh=new PenViewHolder(view);
            return vh;
        }


        @Override
        public void onBindViewHolder(@NonNull final CondemendDepotadapter.PenViewHolder holder, int pos) {
            holder.setIsRecyclable(false);
            final int position = pos;
            try {
                final JSONObject jsonObject = list.getJSONObject(position);
                holder.tv_index.setText((position + 1) + "");
                holder.tv_ques.setText(jsonObject.getString("item_name"));


                for (QanswerData qcmableanswerData : qmap.values()) {
                    if (qcmableanswerData.quest_id.equalsIgnoreCase(jsonObject.getString("id"))) {
                        try {
                            holder.et_quantity.setText(qcmableanswerData.quantity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.et_quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        shortfall_focus_position = position;
                        try {
                            holder.et_quantity.requestFocus(holder.et_quantity.getText().length());
                            if (!holder.et_quantity.getText().toString().trim().isEmpty()) {
                                String  ques_id = "",pl_number="", quantity = "";
                                ques_id = jsonObject.getString("id");
                                pl_number = jsonObject.getString("pl_number");
                                quantity = holder.et_quantity.getText().toString().trim();

                                itemselect(new QanswerData().setQuestId(ques_id).setPlnumber(pl_number).setQuantity(quantity));

                            } else {
                                Log.e("akm ", "item unselect called");
                                itemUnSelect(jsonObject.getString("id"));

                            }
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }

            if (position == shortfall_focus_position) {
                holder.et_quantity.requestFocus();

            }

        }
        @Override
        public int getItemCount() {
            if (list!=null)
                return list.length();
            else
                return 0;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class PenViewHolder extends RecyclerView.ViewHolder{
            TextView tv_index, tv_ques;
            EditText et_quantity;
            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                et_quantity = itemView.findViewById(R.id.et_quantity);


            }
        }
    }

    public void itemselect(QanswerData qcmableanswerData){

        qmap.put(qcmableanswerData.quest_id, qcmableanswerData);
        adapter.notifyDataSetChanged();
        Log.e("akm select", "qdata "+
                "\nquestid "+qcmableanswerData.quest_id+" "+
                "\npl_number "+qcmableanswerData.pl_number+" "+
                "\ntv_qty "+qcmableanswerData.quantity+" ");
    }


    public void itemUnSelect(String itemId) {
        qmap.remove(itemId);
        adapter.notifyDataSetChanged();
    }
    private void uploadsignature(String path, int n) {
        showLoading("uploading sign" + n);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        String s=new String(response.data);
                        if (n == 1)
                            signatureresponse1 = s.substring(s.indexOf("/")+1);
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
        RequestQueue rQueue = Volley.newRequestQueue(CondemnetBsrDepotActivity.this);
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
