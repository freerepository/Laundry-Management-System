package com.example.laundrymanagementghy.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.DeportDashboard;
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.Depot_PanaltyScreen;
import com.example.laundrymanagementghy.OfficerActivity.OfficerLogin;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.SSEActvity.SSEDashboard;
import com.example.laundrymanagementghy.StoreActivity.StoreDashboard;
import com.example.laundrymanagementghy.SupervisorActivity.SupervisorDashboard;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {
    //    private final static String LOGIN_API ="http://lmsguwahati.projectrailway.in/Api/login2";
    private final static String LOGIN_API = "http://lmskyq.projectrailway.in/Api/login2";
    private boolean doubleBacktoExitpresone = false;
    Button bt_login,bt_clear;
    EditText et_uid, et_password;
    UserDataModel userdataModel=null;

    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_uid = findViewById(R.id.et_uid);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_submit);
        bt_clear = findViewById(R.id.bt_clearr);
//        bt_clear.setBackgroundResource(R.drawable.btn3);
//        bt_clear.setBackground(ContextCompat.getDrawable(this, R.drawable.btn3));

//        bt_clear.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn3));
//        bt_clear.setBackground(ContextCompat.getDrawable(this, R.drawable.btn3));


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_login.setBackgroundResource(R.drawable.button_orange_bg);
                if (TextUtils.isEmpty(et_uid.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter User ID", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(et_password.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }else{
                    if (O.checkNetwork(LoginActivity.this)) {
                        login();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.internet_connection_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Depot_PanaltyScreen.class));
            }
        });
    }

    private void login() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", et_uid.getText().toString());
            jsonObject.put("password", et_password.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("reqbody", requestBody);
        showLoading("Please wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_API, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    Log.e("response", ""+response);
                    userdataModel = new Gson().fromJson(response.toString(), UserDataModel.class);
                    if(userdataModel!=null){
                        O.savePreference(LoginActivity.this, O.USER_DATA, response);
                        if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(LoginActivity.this, DeportDashboard.class);
                            startActivity(intent);
                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("2")) {
//                            Toast.makeText(LoginActivity.this, "Laundry id is "+userdataModel.mUserItems.get(0).mLaundryID, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, IntroSliderActivity.class);
                            startActivity(intent);
//                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("2")) {
//                            Intent intent = new Intent(LoginActivity.this, LaundryDashboard.class);
//                            startActivity(intent);
                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("5")) {
                            Intent intent = new Intent(LoginActivity.this, SupervisorDashboard.class);
                            startActivity(intent);
                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("4")) {
                            Intent intent = new Intent(LoginActivity.this, OfficerLogin.class);
                            startActivity(intent);
                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("3")) {
                            Intent intent = new Intent(LoginActivity.this, SSEDashboard.class);
                            startActivity(intent);
                        } else if (userdataModel.mUserItems.get(0).mType.equalsIgnoreCase("6")) {
                            Intent intent = new Intent(LoginActivity.this, StoreDashboard.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Number or Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Invalid Number or Password", Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                hideLoading();
                error.printStackTrace();
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
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
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
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }
}
