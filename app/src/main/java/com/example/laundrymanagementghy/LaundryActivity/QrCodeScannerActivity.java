package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class QrCodeScannerActivity extends AppCompatActivity {
    private final static String update_delivery_status = "http://lmsguwahati.projectrailway.in/Api/update_deliveryStatus";

    ImageView iv_qr_code,v_back;
    TextView tv_qr_code_scan;
    Button btn_verify;
    String id,Qr_code;
    UserDataModel userdataModel;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        id = getIntent().getStringExtra("id");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_verify=findViewById(R.id.btn_verify_qr_code);
        tv_qr_code_scan = findViewById(R.id.tv_qr_code_scan);
        iv_qr_code = findViewById(R.id.bt_qr_code_scan);
        v_back=findViewById(R.id.v_back);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanCode();


            }
        });
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qr_code = tv_qr_code_scan.getText().toString();
                QrcodeVerify();

            }

            private void QrcodeVerify() {
                Qr_code = tv_qr_code_scan.getText().toString();
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("item_id",tv_qr_code_scan.getText().toString());
                    jsonObject.put("laundryId",userdataModel.mUserItems.get(0).mLaundryID);
                    jsonObject.put("laundry_staff_id", userdataModel.mUserItems.get(0).mLogin_id);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String requestBody = jsonObject.toString();
                Log.e("reqbody", requestBody);
                showLoading("Please wait...");

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        update_delivery_status, new Response.Listener<String>() {
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
                RequestQueue requestQueue = Volley.newRequestQueue(QrCodeScannerActivity.this);
                requestQueue.add(stringRequest);



            }
        });
    }

    private void ScanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CaptureAct.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                tv_qr_code_scan.setText(intentResult.getContents());
                //tv1.setText(intentResult.getFormatName());
                btn_verify.setEnabled(true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }


    }
    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(QrCodeScannerActivity.this);
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
                Intent intent = new Intent(QrCodeScannerActivity.this, QrCodeScannerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
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