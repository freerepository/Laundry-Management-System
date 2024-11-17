package com.example.laundrymanagementghy.OfficerActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.example.laundrymanagementghy.Activity.LoginActivity;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfficerLogin extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    public final static String submitofficer = "http://lmsguwahati.projectrailway.in/Api/save_officer_penalty";
    private final static String DESIGNATION_API = "http://lmsguwahati.projectrailway.in/Api/officerDesignation";
    private final static String GRADE_API = "http://lmsguwahati.projectrailway.in/Api/officerGrade";
    private final static String LAUNDRY_API = "http://lmsguwahati.projectrailway.in/Api/get_all_laundry";
    private final static String GET_DEPOT_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_depots";

    ImageView vlogout;
    Spinner sp_designation,sp_grade,sp_laundry,sp_selectdepot;
    RadioGroup radioGroup;
    RadioButton r1,r2,r3,r4;
    EditText remark,amount;
    Button submit;
    String designation, grade,laundry,depot;
    TextView tvUsername;
    String amnt,remARK;
    String requestBody;
    String selection;
    String message;
    AlertDialog dialog;
    UserDataModel userdataModel=null;
    ProgressDialog mProgressDialog;
    ArrayList<String> laundry_list=new ArrayList<>(),laundry_id_list=new ArrayList<>();
    ArrayList<String> depot_list=new ArrayList<>(),depot_id_list=new ArrayList<>();

    ArrayList<String> designation_list=new ArrayList<>();
    ArrayList<String> grade_list=new ArrayList<>();

    ArrayAdapter<String> adapter_designation, adapter_grade,adapter_laundry,adapter_depot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_login);
        tvUsername=findViewById(R.id.tvUserName);
        vlogout = findViewById(R.id.iv_logout);
        sp_selectdepot=findViewById(R.id.sp_selectdepot);
        sp_laundry = findViewById(R.id.sp_laundry);
        sp_designation = findViewById(R.id.sp_designation);
        sp_grade = findViewById(R.id.sp_grade);
        amount = findViewById(R.id.amount);
        remark = findViewById(R.id.remark);
        submit = findViewById(R.id.rating);
        radioGroup = findViewById(R.id.Radio_group);
        r1 = findViewById(R.id.rb1);
        r2 = findViewById(R.id.rb2);
        r3 = findViewById(R.id.rb3);
        r4 = findViewById(R.id.rb4);
        vlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        radioGroup.setOnCheckedChangeListener(this);

        tvUsername.setText(userdataModel.mUserItems.get(0).mName);

        depot_list.add(0,"Select Your Depot");
        adapter_depot = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_selectdepot.setAdapter(adapter_depot);
        sp_selectdepot.setSelection(0, true);
        sp_selectdepot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                    depot =depot_list.get(i);
                else depot="";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        laundry_list.add(0,"Select Your Laundry");
        adapter_laundry = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, laundry_list);
        adapter_laundry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_laundry.setAdapter(adapter_laundry);
        sp_laundry.setSelection(0, true);
        sp_laundry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                    laundry =laundry_list.get(i);
                else laundry="";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });



        designation_list.add(0,"Select Your Designation");
        adapter_designation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, designation_list);
        adapter_designation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_designation.setAdapter(adapter_designation);
        sp_designation.setSelection(0, true);
        sp_designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                    designation =designation_list.get(i);
                else designation="";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        grade_list.add(0,"Select Your Grade");
        adapter_grade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grade_list);
        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_grade.setAdapter(adapter_grade);
        sp_grade.setSelection(0, true);
        sp_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                    grade=grade_list.get(i);
                else grade="";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        getDepot();
        getLaundry();
        getDesignation();
        getGrades();



        remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                remARK = s.toString();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(depot)) {
                    Toast.makeText(OfficerLogin.this, "Please select Depot", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(laundry)) {
                    Toast.makeText(OfficerLogin.this, "Please select Laundry", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(designation)) {
                    Toast.makeText(OfficerLogin.this, "Please select Designation", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(grade)) {
                    Toast.makeText(OfficerLogin.this, "Please select Grade", Toast.LENGTH_SHORT).show();
                } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(OfficerLogin.this, "Please give Rating", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(amount.getText().toString())) {
                    Toast.makeText(OfficerLogin.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else if (remark.getText().toString().isEmpty()) {
                    Toast.makeText(OfficerLogin.this, "Please give Remark", Toast.LENGTH_SHORT).show();
                } else {

                    JSONObject jObject = new JSONObject();
                    try {
                        jObject.put("officer_name", userdataModel.mUserItems.get(0).mName);
                        jObject.put("officer_id", userdataModel.mUserItems.get(0).mLogin_id);
                        jObject.put("laundry_id",laundry_id_list.get(sp_laundry.getSelectedItemPosition()));
                        jObject.put("depot",depot);
                        jObject.put("designation", designation);
                        jObject.put("grade", grade);
                        jObject.put("rating", selection);
                        jObject.put("remark", remARK);
                        jObject.put("amount",amount.getText().toString());
                        requestBody = jObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.v("requestBody", requestBody);
                    showLoading("Uploading data");
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.button_orange_bg);
                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, submitofficer, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideLoading();
                            //   Toast.makeText(OfficerActivity.this, response, Toast.LENGTH_LONG).show();
                            JSONObject jsonObject= null;
                            try {
                                jsonObject = new JSONObject(response);
                                message=jsonObject.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OfficerLogin.this);
                            //  builder.setTitle("Message")
                            builder.setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int a) {
                                            Intent i = new Intent(OfficerLogin.this, OfficerLogin.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }
                                    });

                            android.app.AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoading();
                            submit.setEnabled(true);
                            submit.setBackgroundResource(R.drawable.button_blue_bg);
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
                                //   VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("Accept", "application/json");
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(OfficerLogin.this);
                    requestQueue.add(stringRequest);
                }
            }
        });

    }
    private void getDepot() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);

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
                            depot_list.add(0,"Select Your Depot");
                            depot_id_list.clear();
                            depot_id_list.add(0,"Select Your Depot");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                depot_list.add(obj.getString("depot_name"));
                                depot_id_list.add(obj.getString("depot_id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter_depot.notifyDataSetChanged();
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


    private void getLaundry() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, LAUNDRY_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray=response.getJSONArray("laundry_data");
                            laundry_list.clear();
                            laundry_list.add(0,"Select Your Laundry");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                               // laundry_list.add(obj.getString("laundry_name"));
                                laundry_list.add(obj.getString("laundry_name"));
                                laundry_id_list.add(obj.getString("laundry_id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter_laundry.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", ""+error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    private void getGrades() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GRADE_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray=response.getJSONArray("GetGrade");
                            grade_list.clear();
                            grade_list.add(0,"Select Your Grade");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                grade_list.add(obj.getString("grade"));
                                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter_grade.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", ""+error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }
    private void getDesignation() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, DESIGNATION_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray=response.getJSONArray("GetDesignation");
                            designation_list.clear();
                            designation_list.add(0,"Select Your Designation");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                designation_list.add(obj.getString("designation"));
                                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter_designation.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", ""+error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int i) {
        if (radioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(getApplicationContext(),"Please select answer", Toast.LENGTH_LONG).show();
        }else {
            int radioButtonId = group.getCheckedRadioButtonId();
            View radio = group.findViewById(radioButtonId);
            int position = group.indexOfChild(radio);
            RadioButton butt = (RadioButton) radioGroup.getChildAt(position);
            selection = (String) butt.getText();
            Log.e("Selected", "" + selection);

        }
    }
    public void showLogoutAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        TextView tv=dialog.findViewById(R.id.tv);
        tv.setText("Logout Confirm ?");
        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                O.clearPref(OfficerLogin.this);
                Intent i=new Intent(OfficerLogin.this, LoginActivity.class);
                finishAffinity();
                startActivity(i);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
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