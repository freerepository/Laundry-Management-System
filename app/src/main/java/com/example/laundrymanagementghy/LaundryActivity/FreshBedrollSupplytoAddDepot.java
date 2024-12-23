package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.Bmodel.SaveDateSupply;
import com.example.laundrymanagementghy.R;
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
import java.util.List;
import java.util.Locale;

public class FreshBedrollSupplytoAddDepot extends AppCompatActivity {
    private final static String GET_DEPOT_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_depots";
    private final static String GET_COACH_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_coach";
    private final static String GET_TRAIN_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_trains";
    private final static String SAVE_API = "http://lmsguwahati.projectrailway.in/Api/save_supply_to_laundry";
    EditText et_no_of_bag, et_bed_sheet, et_pillow_cover, et_face_towel,
            et_blanket_cover, et_bath_towel, et_blanket, et_total_packets, et_remark, et_date, et_pillow;
    TextView btn_submit;
    ImageView ic_calender, v_negative;
    AlertDialog dialog;
    ProgressDialog mProgressDialog;
    Spinner sp_depot, sp_train, sp_coach;
    public String selectedDepot = "", selectedTrain = "", selectedCoach = "", depot;
    final Calendar myCalendar = Calendar.getInstance();
    UserDataModel userDataModel;
    ArrayList<String> depot_list = new ArrayList<>(), depot_id_list = new ArrayList<>();

    ArrayList<String> train_list = new ArrayList<>(), train_id_list = new ArrayList<>();
    ArrayList<String> coach_list = new ArrayList<>(), coach_id_list = new ArrayList<>();
    UiModeManager uiModeManager;
    ArrayAdapter<String> adapter_depot, adapter_train, adapter_coach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_bedroll_add_supply_depot);
        try {
            depot = getIntent().getStringExtra("depot_code");

            userDataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView tv1 = findViewById(R.id.txt1);
        TextView tv2 = findViewById(R.id.txt2);
        TextView tv3 = findViewById(R.id.txt3);
        TextView tv4 = findViewById(R.id.txt4);
        TextView tv5 = findViewById(R.id.txt5);
        TextView tv6 = findViewById(R.id.txt6);
//        TextView tv7 = findViewById(R.id.txt7);
        TextView tv8 = findViewById(R.id.txt8);
        TextView tv9 = findViewById(R.id.txt9);
        TextView tv10 = findViewById(R.id.txt10);


        et_no_of_bag = findViewById(R.id.et_no_of_bag);//
        et_bed_sheet = findViewById(R.id.et_bed_sheet);//
        et_pillow_cover = findViewById(R.id.et_pillow_cover);
        et_face_towel = findViewById(R.id.et_face_towel);
        et_blanket_cover = findViewById(R.id.et_blanket_cover);
//        et_bath_towel = findViewById(R.id.et_bath_towel);//
        et_blanket = findViewById(R.id.et_blanket);//
        et_total_packets = findViewById(R.id.et_total_packets);//


        et_remark = findViewById(R.id.et_remark); //
        et_date = findViewById(R.id.et_date);//
        et_pillow = findViewById(R.id.et_pillow);//
        ic_calender = findViewById(R.id.ic_calender);
        btn_submit = findViewById(R.id.v_positives);
        sp_depot = findViewById(R.id.sp_depot);
        sp_train = findViewById(R.id.sp_select_train);
        sp_coach = findViewById(R.id.sp_select_coach);

        v_negative = findViewById(R.id.v_negative);
        v_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            et_bed_sheet.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_bed_sheet.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));

            et_pillow.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_pillow.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_pillow.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));


//            et_bath_towel.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
//            et_bath_towel.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.shape_white));
            et_blanket.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_blanket.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_no_of_bag.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_no_of_bag.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_total_packets.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_total_packets.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            sp_coach.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            sp_train.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_date.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            findViewById(R.id.rlv2).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            findViewById(R.id.dev1).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));

//            et_date.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.shape_white));
            et_blanket_cover.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_blanket_cover.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_face_towel.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_face_towel.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_remark.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_remark.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));
            et_pillow_cover.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_pillow_cover.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_white));

            et_bed_sheet.setTextColor(getResources().getColor(R.color.whiteTextColor));
//            et_bath_towel.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_blanket.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_no_of_bag.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_total_packets.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_date.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_blanket_cover.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_face_towel.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_remark.setTextColor(getResources().getColor(R.color.whiteTextColor));
            et_pillow_cover.setTextColor(getResources().getColor(R.color.whiteTextColor));


            tv1.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv2.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv3.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv4.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv5.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv6.setTextColor(getResources().getColor(R.color.whiteTextColor));
//            tv7.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv8.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv9.setTextColor(getResources().getColor(R.color.whiteTextColor));
            tv10.setTextColor(getResources().getColor(R.color.whiteTextColor));


        } else {
            et_pillow.setHintTextColor(getResources().getColor(R.color.black));
            et_pillow.setTextColor(getResources().getColor(R.color.black));


            et_bed_sheet.setHintTextColor(getResources().getColor(R.color.black));
//            et_bath_towel.setHintTextColor(getResources().getColor(R.color.black));
            et_blanket.setHintTextColor(getResources().getColor(R.color.black));
            et_no_of_bag.setHintTextColor(getResources().getColor(R.color.black));
            et_total_packets.setHintTextColor(getResources().getColor(R.color.black));
            et_date.setHintTextColor(getResources().getColor(R.color.black));
            et_blanket_cover.setHintTextColor(getResources().getColor(R.color.black));
            et_face_towel.setHintTextColor(getResources().getColor(R.color.black));
            et_remark.setHintTextColor(getResources().getColor(R.color.black));
            et_pillow_cover.setHintTextColor(getResources().getColor(R.color.black));

            et_bed_sheet.setTextColor(getResources().getColor(R.color.black));
//            et_bath_towel.setTextColor(getResources().getColor(R.color.black));
            et_blanket.setTextColor(getResources().getColor(R.color.black));
            et_no_of_bag.setTextColor(getResources().getColor(R.color.black));
            et_total_packets.setTextColor(getResources().getColor(R.color.black));
            et_date.setTextColor(getResources().getColor(R.color.black));
            et_blanket_cover.setTextColor(getResources().getColor(R.color.black));
            et_face_towel.setTextColor(getResources().getColor(R.color.black));
            et_remark.setTextColor(getResources().getColor(R.color.black));
            et_pillow_cover.setTextColor(getResources().getColor(R.color.black));


        }


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
                DatePickerDialog dpd = new DatePickerDialog(FreshBedrollSupplytoAddDepot.this, journeyDate1, myCalendar
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
                //callTab();

            }
        });
        GetDepotType();
        GetCoachType();
        depot_list.add(0, "Select Depot...");
        adapter_depot = new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_depot.setAdapter(adapter_depot);
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depot_list.get(i);
                    GetTrainType(selectedDepot);

                    Log.e("selectedDepot", selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        train_list.add(0, "Select Train.");
        adapter_train = new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_item, train_list);
        adapter_train.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_train.setAdapter(adapter_train);
        sp_train.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedTrain = "";

                } else {
                    selectedTrain = train_list.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        coach_list.add(0, "Select Coach.");
        adapter_coach = new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_item, coach_list);
        adapter_coach.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_coach.setAdapter(adapter_coach);
        sp_coach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedCoach = "";

                } else {
                    selectedCoach = coach_list.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Select Date", Toast.LENGTH_LONG).show();
                } else if (sp_depot.getSelectedItemPosition() == 0) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Select Depot ", Toast.LENGTH_LONG).show();
                } else if (sp_train.getSelectedItemPosition() == 0) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Select Train", Toast.LENGTH_LONG).show();
                } else if (sp_coach.getSelectedItemPosition() == 0) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Select Coach", Toast.LENGTH_LONG).show();
                } else if (et_no_of_bag.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Number of bags is empty", Toast.LENGTH_LONG).show();
                } else if (et_bed_sheet.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Bed Sheet is empty", Toast.LENGTH_LONG).show();
                } else if (et_pillow.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Pillow is empty", Toast.LENGTH_LONG).show();
                } else if (et_pillow_cover.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Pillow cover is empty", Toast.LENGTH_LONG).show();
                } else if (et_blanket.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Blanket is empty", Toast.LENGTH_LONG).show();
                } else if (et_blanket_cover.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Blanket Cover is empty", Toast.LENGTH_LONG).show();
                } else if (et_face_towel.getText().toString().isEmpty()) {
                    Toast.makeText(FreshBedrollSupplytoAddDepot.this, "Hand Towel is empty", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        SaveLaundrySupply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private void SaveLaundrySupply() {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("laundry_id", userDataModel.mUserItems.get(0).mLaundryID);
                    jsonObject.put("laundry_supply_id", userDataModel.mUserItems.get(0).mLogin_id);
                    jsonObject.put("supply_date", et_date.getText().toString());
                    jsonObject.put("depot_id", depot_id_list.get(sp_depot.getSelectedItemPosition()));
                    jsonObject.put("train_no", selectedTrain);
                    jsonObject.put("coach", selectedCoach);
                    jsonObject.put("no_of_bag", et_no_of_bag.getText().toString());
                    jsonObject.put("bs_first_ac", "");
                    jsonObject.put("pc_first_ac", "");
                    jsonObject.put("bs", et_bed_sheet.getText().toString());
                    jsonObject.put("pillow", et_pillow.getText().toString().trim());
                    jsonObject.put("pc", et_pillow_cover.getText().toString());
                    jsonObject.put("ft", et_face_towel.getText().toString());
                    jsonObject.put("blanket_cover", et_blanket_cover.getText().toString());
                    jsonObject.put("bath_towel", "");
                    jsonObject.put("no_blanket", et_blanket.getText().toString());
                    jsonObject.put("packet_count", et_total_packets.getText().toString());
                    jsonObject.put("remark", et_remark.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String requestBody = jsonObject.toString();
                Log.e("responseAa", requestBody);
                showLoading("Please wait...");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SAVE_API, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoading();
                        try {
                            Log.e("responseAdd", response);
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (jsonResponse != null && jsonResponse.has("message")) {
//                                String message = jsonResponse.getString("message");

                                showConfirmationDialog("Data Saved Successfully", uiModeManager);
                            } else {
                                showConfirmationDialog("Data Saved Successfully", uiModeManager);
                            }
                            Type listType = new TypeToken<List<SaveDateSupply>>() {
                            }.getType();
                            ArrayList<SaveDateSupply> getList = new Gson().fromJson(response.toString(), listType);
                            //this is user data

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        Log.e("API Error", error.toString());
                        if (error.networkResponse != null) {
                            Log.e("Error Code", String.valueOf(error.networkResponse.statusCode));
                            Log.e("Error Data", new String(error.networkResponse.data));
                        }
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
                RequestQueue requestQueue = Volley.newRequestQueue(FreshBedrollSupplytoAddDepot.this);
                requestQueue.add(stringRequest);
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
                        sp_depot.setAdapter(new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_dropdown_item, depot_list));
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

    private void GetTrainType(String selectedDepot) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", selectedDepot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, GET_TRAIN_TYPE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("train_data");
                            train_list.clear();
                            train_list.add(0, "Select Train");
                            train_id_list.clear();
                            train_id_list.add(0, "Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                train_list.add(obj.getString("train_no"));
                                train_id_list.add(obj.getString("id"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_train.setAdapter(new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_dropdown_item, train_list));
                        sp_train.setSelected(false);


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

    private void GetCoachType() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GET_COACH_TYPE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("array", response.toString());
                            org.json.JSONArray array = response.getJSONArray("getCoach");
                            coach_list.clear();
                            coach_list.add(0, "Select Coach");
                            coach_id_list.clear();
                            coach_id_list.add(0, "Select Coach");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                coach_list.add(obj.getString("coach_type"));
                                coach_id_list.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_coach.setAdapter(new ArrayAdapter<String>(FreshBedrollSupplytoAddDepot.this, android.R.layout.simple_spinner_dropdown_item, coach_list));
                        sp_coach.setSelected(false);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(FreshBedrollSupplytoAddDepot.this);
        requestQueue.add(objectRequest);
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
        final Dialog dialog = new Dialog(FreshBedrollSupplytoAddDepot.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);


        TextView tvTitle = dialog.findViewById(R.id.confirmMessageTitle);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
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
                Intent intent = new Intent(FreshBedrollSupplytoAddDepot.this, FreshBedrollSupplytoDepot.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


    }
}