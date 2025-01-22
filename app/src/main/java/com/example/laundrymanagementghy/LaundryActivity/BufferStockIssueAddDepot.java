package com.example.laundrymanagementghy.LaundryActivity;

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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.GetBufferIssueList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
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

public class BufferStockIssueAddDepot extends AppCompatActivity {
    private final static String get_depot = "http://lmskyq.projectrailway.in/Api/get_depots";
    private final static String SAVE_API = "http://lmskyq.projectrailway.in/api/save_bufferstock";
    EditText et_date, et_bed_sheet, et_pillow, et_pillow_cover, et_blanket, et_blanket_cover, et_hand_towel, et_reason;
    Button btn_next_submit;
    ImageView v_back;
    Spinner sp_depot;
    public String selectedDepot = "", depot;
    final Calendar myCalendar = Calendar.getInstance();
    UserDataModel userdataModel;
    AlertDialog dialog;
    ProgressDialog mProgressDialog;
    ArrayList<String> depot_list = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayAdapter<String> adapter_depot;
    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_stock_issue_add_depot);
        et_date = findViewById(R.id.et_date);
        et_bed_sheet = findViewById(R.id.et_bed_sheet);
        et_pillow = findViewById(R.id.et_pillow);
        et_pillow_cover = findViewById(R.id.et_pillow_cover);
        et_blanket = findViewById(R.id.et_blanket);
        et_blanket_cover = findViewById(R.id.et_blanket_cover);
        et_hand_towel = findViewById(R.id.et_hand_towel);
        et_reason = findViewById(R.id.et_reason);
        btn_next_submit = findViewById(R.id.btn_next_submit);
        v_back = findViewById(R.id.v_back);
        sp_depot = findViewById(R.id.sp_depot);
        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        try {
            depot = getIntent().getStringExtra("depot_code");

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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


//        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            et_date.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_date.setTextColor(getResources().getColor(R.color.whiteTextColor));

            et_bed_sheet.setHintTextColor(getResources().getColor(R.color.black));
            et_bed_sheet.setTextColor(getResources().getColor(R.color.black));

            et_blanket.setHintTextColor(getResources().getColor(R.color.black));
            et_blanket.setTextColor(getResources().getColor(R.color.black));

            et_blanket_cover.setHintTextColor(getResources().getColor(R.color.black));
            et_blanket_cover.setTextColor(getResources().getColor(R.color.black));

            et_pillow.setHintTextColor(getResources().getColor(R.color.black));
            et_pillow.setTextColor(getResources().getColor(R.color.black));

            et_pillow_cover.setHintTextColor(getResources().getColor(R.color.black));
            et_pillow_cover.setTextColor(getResources().getColor(R.color.black));

            et_hand_towel.setHintTextColor(getResources().getColor(R.color.black));
            et_hand_towel.setTextColor(getResources().getColor(R.color.black));
            et_reason.setHintTextColor(getResources().getColor(R.color.black));
            et_reason.setTextColor(getResources().getColor(R.color.black));


        } else {
//            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
//            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(BufferStockIssueAddDepot.this, journeyDate1, myCalendar
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
        depot_list.add(0, "Select Depot...");
        adapter_depot = new ArrayAdapter<String>(BufferStockIssueAddDepot.this, android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_depot.setAdapter(adapter_depot);
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depot_list.get(i);
                    //GetTrainType(selectedDepot);

                    Log.e("selectedDepot", selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btn_next_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Select Date", Toast.LENGTH_LONG).show();
                } else if (sp_depot.getSelectedItemPosition() == 0) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Select Depot ", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_bed_sheet.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter bed sheet", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_pillow.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter Pillow", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_pillow_cover.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter Pillow Cover", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_blanket.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter Blanket", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_blanket_cover.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter Blanket Cover", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_hand_towel.getText().toString())) {
                    Toast.makeText(BufferStockIssueAddDepot.this, "Enter Hand Towel", Toast.LENGTH_LONG).show();

                } else {
                    try {
                        SaveBuffer();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void SaveBuffer() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
            jsonObject.put("supervisor_id", userdataModel.mUserItems.get(0).mLogin_id);
            jsonObject.put("submission_date", et_date.getText().toString());
            jsonObject.put("depot_id", depot_id_list.get(sp_depot.getSelectedItemPosition()));
            jsonObject.put("bed_sheet", et_bed_sheet.getText().toString());
            jsonObject.put("pillow", et_pillow.getText().toString());
            jsonObject.put("pillow_cover", et_pillow_cover.getText().toString());
            jsonObject.put("blanket", et_blanket.getText().toString());
            jsonObject.put("blanket_cover", et_pillow_cover.getText().toString());
            jsonObject.put("hand_towel", et_hand_towel.getText().toString());
            jsonObject.put("reason", et_reason.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString(); //isane sare data ko string mai convert kar diya hai
        Log.e("response", requestBody);
        showLoading("Please wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                SAVE_API, new Response.Listener<String>() {
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
                        String message = jsonResponse.getString("message");
                        showConfirmationDialog(message,uiModeManager);
                    } else {
                        showConfirmationDialog(response,uiModeManager);
                    }
                    Type listType = new TypeToken<List<GetBufferIssueList>>() {
                    }.getType();
                    ArrayList<GetBufferIssueList> getList = new Gson().fromJson(response.toString(), listType);
                    //this is user data


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
        RequestQueue requestQueue = Volley.newRequestQueue(BufferStockIssueAddDepot.this);
        requestQueue.add(stringRequest);

    }

    private void GetDepotType() {
        JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, get_depot, null,
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
                        sp_depot.setAdapter(new ArrayAdapter<String>(BufferStockIssueAddDepot.this, android.R.layout.simple_spinner_dropdown_item, depot_list));
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


    public void showConfirmationDialog(String strMessage,UiModeManager uiModeManager) {
        final Dialog dialog = new Dialog(BufferStockIssueAddDepot.this);
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
                Intent intent = new Intent(BufferStockIssueAddDepot.this, BufferStockIssuetoDepot.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


    }
}

