package com.example.laundrymanagementghy.LaundryActivity;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.CatPenaltyModel;
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

public class PenaltyLaundryActivity extends AppCompatActivity {
    public final static String STORING_Image = "http://lmskyq.projectrailway.in/Api/upload_signature";
    private final static String SubmitPenaltyData = "http://lmskyq.projectrailway.in/Api/save_penalties";
    private final static String questionAPI = "http://lmskyq.projectrailway.in/Api/penalty_questions";
    ImageView iv_backView, iv_calender;
    EditText et_date, et_remark;
    Button submit;
    public TextView tv_total_penality_amount;
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

    View signature_layout;
    public HashMap<String, QanswerData> qmap = new HashMap<>();
    CatPenaltyModel catPenaltyModel=null;
    ImageView signclick1, iv_sign1;
    public String strSignatureFilePath1 = "", signatureresponse1;

    public static final int SIGNATURE_ACTIVITY = 1;
    UserDataModel userdataModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty_laundry);
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_total_penality_amount=findViewById(R.id.tv_total_penality_amount);
        signature_layout = findViewById(R.id.signature_layout);
        tabLayout=findViewById(R.id.tl);
        srl=findViewById(R.id.srl);
        recyclerView=findViewById(R.id.rv);
        et_date=findViewById(R.id.et_select_date);
        et_remark=findViewById(R.id.et_remark);
        iv_calender=findViewById(R.id.iv_calender);
        submit=findViewById(R.id.btn_next_submit);
        iv_backView=findViewById(R.id.iv_backView);
        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signclick1 = findViewById(R.id.click1);
        iv_sign1 = findViewById(R.id.img_sign1);

        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PenaltyLaundryActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        submit.setText("Submit");
        adapter = new PenalityAdapter(PenaltyLaundryActivity.this, questionArray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if (qmap.size()==0) {
                            Toast.makeText(getApplicationContext(), "Please give atleast one Score", Toast.LENGTH_LONG).show();
                        } else if (TextUtils.isEmpty(et_date.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Please Select Date", Toast.LENGTH_SHORT).show();
//                        } else if (strSignatureFilePath1.isEmpty() ) {
//                            Toast.makeText(PenaltyLaundryActivity.this, "Please take  signature.", Toast.LENGTH_SHORT).show();
                        } else {
                            showLoading("Uploading...");
                            submit.setEnabled(false);
                            submit.setBackgroundResource(R.drawable.button_orange_bg);

                            JSONArray jsonArray = new JSONArray();
                            for (QanswerData qanswerData : qmap.values()) {
                                JSONObject jo = new JSONObject();
                                try {

                                    jo.put("quest_id", qanswerData.quest_id);
                                    jo.put("cat_id", qanswerData.cat_id);
                                    jo.put("qty", "1");
                                    jo.put("amount", qanswerData.given_amount);

                                    jsonArray.put(jo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("penalty_date", et_date.getText().toString());
                                jsonObject.put("supervisor_id",userdataModel.mUserItems.get(0).mLogin_id);
                                jsonObject.put("laundry_id",userdataModel.mUserItems.get(0).mLaundryID);
                                jsonObject.put("depot_id","");
                                jsonObject.put("remark", et_remark.getText().toString());
                                jsonObject.put("penalty_from", "laundry");
                                jsonObject.put("signature","" );
                                jsonObject.put("total_penalty",tv_total_penality_amount.getText().toString());
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

                                            JSONObject jsonObject= null;
                                            try {
                                                jsonObject = new JSONObject(response);
                                                message=jsonObject.getString("message");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            qmap.clear();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PenaltyLaundryActivity.this);
                                            //  builder.setTitle("Message")
                                            builder.setMessage(response)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int a) {
                                                            Intent i = new Intent(PenaltyLaundryActivity.this, PenaltyModuleActivity.class);
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
                                    Toast.makeText(PenaltyLaundryActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

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
                            RequestQueue requestQueue = Volley.newRequestQueue(PenaltyLaundryActivity.this);
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
                DatePickerDialog dpd = new DatePickerDialog(PenaltyLaundryActivity.this, journeyDate1, myCalendar
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
                if (!TextUtils.isEmpty(et_date.getText().toString()));
                callTab();


            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(catPenaltyModel==null){

                }else if(adapter==null || adapter.list==null || adapter.list.length()==0){
                    callTab();
                }else{
                    srl.setRefreshing(false);
                }
                callTab();
            }

        });
        Log.e("ResponceTab1","in create");

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
        RequestQueue rQueue = Volley.newRequestQueue(PenaltyLaundryActivity.this);
        rQueue.add(volleyMultipartRequest);
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
                    if (status.equalsIgnoreCase("done")) {
                        strSignatureFilePath1 = bundle.getString("signature_image_url");
                        iv_sign1.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath1));
                        uploadsignature(strSignatureFilePath1, 1);
                    }
                    break;

            }
        }
    }


    private void callTab() {
        srl.setRefreshing(true);
        JSONObject object =new JSONObject();
        try {
            object.put("cat_id",userdataModel.mUserItems.get(0).mLaundryID);

        }catch (JSONException e){}

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, questionAPI, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("q_response",""+response);
                        try {
                            questionArray = response.getJSONArray("GetPenaltyQuestions");
                            adapter.list=questionArray;
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
                        PenaltyLaundryActivity.super.onBackPressed();
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
        public PenalityAdapter(Context context, JSONArray list) {
            this.context = context;
            this.list = list;
        }
        @NonNull
        @Override
        public PenalityAdapter.PenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_penalty,parent,false);
            PenViewHolder vh=new PenViewHolder(view);
            return vh;
        }
        @Override
        public void onBindViewHolder(@NonNull final PenalityAdapter.PenViewHolder holder, final int pos) {
            holder.setIsRecyclable(false);
            final int position = pos;
            try {
                final JSONObject jsonObject = list.getJSONObject(position);
                holder.tv_index.setText((position+1)+"");
                holder.tv_ques.setText(jsonObject.getString("question_name"));


                for (QanswerData qanswerData: qmap.values())
                {
                    if (qanswerData.quest_id.equalsIgnoreCase(jsonObject.getString("id")))
                    {
                        try {
                            holder.tv_shortfall.setText(qanswerData.total_penalty_amount);
                            holder.et_InPutamount.setText(qanswerData.given_amount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.et_InPutamount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        shortfall_focus_position=position;
                        try {
                            holder.et_InPutamount.requestFocus(holder.et_InPutamount.getText().length());
                            if (!holder.et_InPutamount.getText().toString().trim().isEmpty()) {
                                String cat_ID ="", ques_id = "", rate="", quantity = "",given_amount="", total_pamount = "", unit="";
                                ques_id = jsonObject.getString("id");
                                rate = jsonObject.getString("penalty_amount");
                                cat_ID = jsonObject.getString("cat_id");
                                quantity = jsonObject.getString("qty");
                                given_amount= holder.et_InPutamount.getText().toString().trim();
                                Float s = Float.parseFloat(given_amount);
                                total_pamount = String.format("%.2f",s);
                                unit = jsonObject.getString("Unit");

                                itemselect(new QanswerData().setQuestId(ques_id).setCatId(cat_ID).setQuantity(quantity).
                                        setGivenAmount(given_amount).setRate(rate).setTotalPAmount(total_pamount).setUnit(unit));

                            } else {
                                Log.e("akm ","item unselect called");
                                itemUnSelect(jsonObject.getString("id"));
                                holder.tv_shortfall.setText("");

                            }
                        }catch (Exception e){ }
                    }
                });
            }catch (Exception e){ }

            if(position==shortfall_focus_position){
                holder.et_InPutamount.requestFocus();
            }
            float sum=0;
            for (QanswerData answer : qmap.values()) {
                try {
                    sum = sum + Float.parseFloat(answer.total_penalty_amount);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Log.e("size ", String.valueOf(qmap.values().size()));
            tv_total_penality_amount.setText(getResources()
                    .getString(R.string.ruppee_text)+sum+"");

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
            TextView tv_index, tv_ques,  tv_shortfall;
            EditText et_InPutamount;
            public PenViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv_ques = (TextView) itemView.findViewById(R.id.tv_qus);
                tv_shortfall = itemView.findViewById(R.id.ammount);
                et_InPutamount = itemView.findViewById(R.id.et_amount);
            }
        }
    }

    public void itemselect(QanswerData qanswerData){

        qmap.put(qanswerData.quest_id, qanswerData);
        adapter.notifyDataSetChanged();
        Log.e("akm select", "qdata "+
                "\nquestid "+qanswerData.quest_id+" "+
                "\ntv_rate "+qanswerData.rate+" "+
                "\ntv_unit "+qanswerData.unit+" "+
                "\ngiven_amount "+qanswerData.given_amount+" "+
                "\ntv_qty "+qanswerData.quantity+" "+
                "\ncatid "+qanswerData.cat_id+" "+
                "\ntotalamount "+qanswerData.total_penalty_amount+" ");
    }

    public void itemUnSelect(String itemId) {
        qmap.remove(itemId);
        adapter.notifyDataSetChanged();
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

