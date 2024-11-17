package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.laundrymanagementghy.Bmodel.EditSupplyModel;
import com.example.laundrymanagementghy.model.GetFreshSupplyList;
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

public class LaundrySupplyUpdateActivity extends AppCompatActivity {
    private final static String received_laundry_list = "http://lmsguwahati.projectrailway.in/Api/depot_received_from_laundry";
    private final static String get_depot = "http://lmsguwahati.projectrailway.in/Api/get_depots";
    private final static String GET_COACH_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_coach";
    private final static String GET_TRAIN_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_trains";
    private final static String EDIt_API = "http://lmsguwahati.projectrailway.in/Api/save_supply_to_laundry";
    ImageView iv_add_supply,ic_calender;
    String  depot_id="",selectedDepot="",sply_id;
    Spinner sp_depot_id;
    AlertDialog dialog;
    TextView v_positive,v_negative;
    EditText et_date,et_select_depot,et_select_train,et_select_coach,et_no_of_bag,et_bed_sheet,et_pillow_cover,
            et_face_towel,et_blanket_cover,et_bath_towel,et_blanket,et_packet_count,et_remark;
    final Calendar myCalendar = Calendar.getInstance();

    UserDataModel userdataModel;

    GetFreshSupplyList.mGetItem list ;
    ArrayList<String> depot_list = new ArrayList<>(),depot_id_list = new ArrayList<>();
    ArrayList<String> train_list = new ArrayList<>(), train_id_list = new ArrayList<>();
    ArrayList<String> coach_list = new ArrayList<>(), coach_id_list = new ArrayList<>();
    ArrayAdapter<String> adapter_depot;
    String coach,train_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_supply_laundry);
        sply_id = getIntent().getStringExtra("id");
        depot_id = getIntent().getStringExtra("depot_id");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        v_negative=findViewById(R.id.v_negative);
        v_positive=findViewById(R.id.v_positive);

        findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
         et_date = findViewById(R.id.et_date);
        ic_calender=findViewById(R.id.ic_calender);
         sp_depot_id = findViewById(R.id.sp_depot_id);
         et_select_train =findViewById(R.id.et_select_train);
         et_select_coach = findViewById(R.id.et_select_coach);
         et_no_of_bag =findViewById(R.id.et_no_of_bag);
         et_bed_sheet =findViewById(R.id.et_bed_sheet);
         et_pillow_cover = findViewById(R.id.et_pillow_cover);
         et_face_towel = findViewById(R.id.et_face_towel);
         et_blanket_cover =findViewById(R.id.et_blanket_cover);
         et_bath_towel =findViewById(R.id.et_bath_towel);
         et_blanket =findViewById(R.id.et_blanket);
         et_packet_count = findViewById(R.id.et_packet_count);
         et_remark = findViewById(R.id.et_remark);

        list=(GetFreshSupplyList.mGetItem)(getIntent().getSerializableExtra("data"));

        et_date.setText(list.mSupply_date);
        setSpinnerValue(sp_depot_id, list.mDepot_code); // Custom method to set Spinner value
        et_select_train.setText(list.mTrain_no);
        et_select_coach.setText(list.mCoach);
        et_no_of_bag.setText(list.mNo_of_bag);
        et_bed_sheet.setText(list.mBs);
        et_pillow_cover.setText(list.mPc);
        et_face_towel.setText(list.mFt);
        et_blanket_cover.setText(list.mBlanket_cover);
        et_bath_towel.setText(list.mBath_towel);
        et_blanket.setText(list.mNo_blanket);
        et_packet_count.setText(list.mPacket_count);
        et_remark.setText(list.mRemark);


        v_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(LaundrySupplyUpdateActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                } else if (sp_depot_id.getSelectedItemPosition()==0) {
                    Toast.makeText(LaundrySupplyUpdateActivity.this, "Select Depot ", Toast.LENGTH_LONG).show();

                } else {
                    EditDataSave();

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
                DatePickerDialog dpd = new DatePickerDialog(LaundrySupplyUpdateActivity.this, journeyDate1, myCalendar
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

            }
        });
        GetDepotTypes();

        depot_list.add(0, "Select Depot...");
        adapter_depot = new ArrayAdapter<String>(LaundrySupplyUpdateActivity.this, android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_depot_id.setAdapter(adapter_depot);
        sp_depot_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depot_list.get(i);
                    getTrainType(selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        et_select_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LaundrySupplyUpdateActivity.this);
                    // builder.setTitle("Select Train..");
                    builder.setItems(train_list.toArray(new CharSequence[train_list.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                et_select_train.setText(train_list.get(which));
                                dialog.dismiss();
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
        getCoachType();
        et_select_coach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LaundrySupplyUpdateActivity.this);
                    // builder.setTitle("Select Coach..");
                    builder.setItems(coach_list.toArray(new CharSequence[coach_list.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                et_select_coach.setText(coach_list.get(which));
                                dialog.dismiss();
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void setSpinnerValue(Spinner spDepotId, String mDepotCode) {
        for (int i = 0; i < spDepotId.getCount(); i++) {
            if (spDepotId.getItemAtPosition(i).toString().equals(mDepotCode)) {
                spDepotId.setSelection(i);
                break;
            }
        }
    }

    private void EditDataSave() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sply_id",list.mSupply_id);
            jsonObject.put("laundry_id",userdataModel.mUserItems.get(0).mLaundryID);
            jsonObject.put("laundry_supply_id",userdataModel.mUserItems.get(0).mLogin_id);
            jsonObject.put("supply_date",et_date.getText().toString());
            jsonObject.put("depot_id",depot_id_list.get(sp_depot_id.getSelectedItemPosition()));
            jsonObject.put("train_no",et_select_train.getText().toString());
            jsonObject.put("coach",et_select_coach.getText().toString());
            jsonObject.put("no_of_bag",et_no_of_bag.getText().toString());
            jsonObject.put("bs_first_ac","");
            jsonObject.put("pc_first_ac","");
            jsonObject.put("bs",et_bed_sheet.getText().toString());
            jsonObject.put("pc",et_pillow_cover.getText().toString());
            jsonObject.put("ft",et_face_towel.getText().toString());
            jsonObject.put("blanket_cover",et_blanket_cover.getText().toString());
            jsonObject.put("bath_towel",et_bath_towel.getText().toString());
            jsonObject.put("no_blanket",et_blanket.getText().toString());
            jsonObject.put("packet_count",et_packet_count.getText().toString());
            jsonObject.put("remark",et_remark.getText().toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("reqbody", requestBody);
        showLoading("Please wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EDIt_API, new Response.Listener<String>() {
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

                        showConfirmationDialog(message);
                    } else {
                        showConfirmationDialog(response);
                    }
                    Type listType = new TypeToken<List<EditSupplyModel>>() {
                    }.getType();
                    ArrayList<EditSupplyModel> getList = new Gson().fromJson(response.toString(), listType);

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
        RequestQueue requestQueue = Volley.newRequestQueue(LaundrySupplyUpdateActivity.this);
        requestQueue.add(stringRequest);
    }

    private void GetDepotTypes() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, get_depot, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("depot_data");
                            depot_list.clear();
                            depot_list.add(0,"Select Depot");
                            depot_id_list.clear();
                            depot_id_list.add(0,"Select Depot");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                depot_list.add(obj.getString("depot_code"));
                                depot_id_list.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sp_depot_id.setAdapter(new ArrayAdapter<String>(LaundrySupplyUpdateActivity.this, android.R.layout.simple_spinner_dropdown_item, depot_list));
                            sp_depot_id.setSelected(false);
                        }
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

    private void getTrainType(String selectedDepot) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code",selectedDepot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, GET_TRAIN_TYPE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("train_data");
                            train_list.clear();
                            train_list.add(0,"Select Train");
                            train_id_list.clear();
                            train_id_list.add(0,"Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                train_list.add(obj.getString("train_no"));
                                train_id_list.add(obj.getString("id"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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

    private void getCoachType() {
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

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(LaundrySupplyUpdateActivity.this);
        requestQueue.add(objectRequest);

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
    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(LaundrySupplyUpdateActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(LaundrySupplyUpdateActivity.this, BedrollStocking.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


}

}