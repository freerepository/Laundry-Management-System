package com.example.laundrymanagementghy.LaundryActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.CaptureSignatureActivity;
import com.example.laundrymanagementghy.Activity.VolleyMultipartRequest;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QtestCheckanswerData;
import com.example.laundrymanagementghy.util.GPSTracker;
import com.example.laundrymanagementghy.util.MyLocation;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeCameraLaundry extends AppCompatActivity {
    public final static String STORING_Image = "http://lmskyq.projectrailway.in/Api/upload_image";
    public final static String STORING_Sing = "http://lmskyq.projectrailway.in/Api/upload_signature";
    public final static String SubmitTestCheckData = "http://lmskyq.projectrailway.in/Api/save_testcheck";

    Button btnsubmit;
    RequestQueue rQueue;
    String message;
    boolean isSubmited = false;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 = 400;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE3 = 101;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE4 = 401;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE5 = 102;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE6 = 402;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE7 = 103;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE8 = 403;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE9 = 104;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE10 = 405;
    String train_no, user_type,jdate,depot_code, remark,penalty,requestBody;
    String imageresponse1 = "", imageresponse2="", imageresponse3="", imageresponse4="",  imageresponse5="",
            imageresponse6="", imageresponse7="", imageresponse8="", imageresponse9="", imageresponse10="",
            signatureresponse1="",strSignatureFilePath1="", fileUri="";
    LinearLayout signaturelayout;
    ImageView signclick1, iv_sign1;
    public static final int SIGNATURE_ACTIVITY = 1;
    ImageView iv_image1, iv_image2,iv_image3,iv_image4,iv_image5,iv_image6,iv_image7,iv_image8,iv_image9,iv_image10;
    LinearLayout cameralayout,cameralayout1,cameralayout2,cameralayout3,cameralayout4;
    String file_path1, file_path2,file_path3,file_path4,file_path5,file_path6,file_path7,file_path8,file_path9,file_path10;
    GPSTracker gps;
    Boolean image1 = false, image2 = false,image3 = false,image4 = false,image5 = false,image6 = false,image7 = false,image8 = false,image9 = false,image10 = false;
    ProgressDialog mProgressDialog;
    public HashMap<String, QtestCheckanswerData> qmaps=new HashMap<>();
    public static Map<String,String> map=new HashMap<>();
    UserDataModel userdataModel=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_camera_laundry);
        qmaps= (HashMap<String, QtestCheckanswerData>) getIntent().getSerializableExtra("qdata");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        cameralayout = (LinearLayout) findViewById(R.id.camera_layout);
        cameralayout1 = (LinearLayout) findViewById(R.id.camera_layout1);
        cameralayout2 = (LinearLayout) findViewById(R.id.camera_layout2);
        cameralayout3 = (LinearLayout) findViewById(R.id.camera_layout3);
        cameralayout4 = (LinearLayout) findViewById(R.id.camera_layout4);

        iv_image1 = findViewById(R.id.iv_train1);
        iv_image2 = findViewById(R.id.iv_train2);
        iv_image3 = findViewById(R.id.iv_train3);
        iv_image4 = findViewById(R.id.iv_train4);
        iv_image5 = findViewById(R.id.iv_train5);
        iv_image6 = findViewById(R.id.iv_train6);
        iv_image7 = findViewById(R.id.iv_train7);
        iv_image8 = findViewById(R.id.iv_train8);
        iv_image9 = findViewById(R.id.iv_train9);
        iv_image10 = findViewById(R.id.iv_train10);
        signaturelayout = findViewById(R.id.signature_layout);
        signclick1 = findViewById(R.id.click1);
        iv_sign1 = findViewById(R.id.img_sign1);
        btnsubmit = findViewById(R.id.btn_submit);

        iv_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image1 = true;
                image2 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

            }
        });
        iv_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image1 = true;
                image2 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE2);

            }
        });
        iv_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image3 = true;
                image4 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE3);

            }
        });
        iv_image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image3 = true;
                image4 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE4);

            }
        });
        iv_image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image5 = true;
                image6 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE5);

            }
        });
        iv_image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image5 = true;
                image6 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE6);

            }
        });
        iv_image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image7 = true;
                image8 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE7);

            }
        });
        iv_image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image7 = true;
                image8 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE8);

            }
        });
        iv_image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image9 = true;
                image10 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE9);

            }
        });
        iv_image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image9 = true;
                image10 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE10);

            }
        });

        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakeCameraLaundry.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });



        Intent intent = getIntent();
        train_no = intent.getStringExtra("train_no");
        user_type = intent.getStringExtra("user_type");
        depot_code = intent.getStringExtra("depot_code");
        remark = intent.getStringExtra("remark");
        penalty = intent.getStringExtra("penalty");
        jdate = intent.getStringExtra("jdate");


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSubmited) {
                    if (TextUtils.isEmpty(signatureresponse1)) {
                        Toast.makeText(TakeCameraLaundry.this, "Please both take image & signature", Toast.LENGTH_SHORT).show();

                    } else {
                        JSONArray jsonArray=new JSONArray();
                        for (QtestCheckanswerData qtestCheckData : qmaps.values()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("item_id",qtestCheckData.quest_id);
                                jsonObject.put("item_no",qtestCheckData.item_no);
                                jsonObject.put("wmi",qtestCheckData.wmi);
                                jsonObject.put("item_name",qtestCheckData.item_name);

                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            JSONObject obj1 = new JSONObject();
                            obj1.put("user_type",user_type);
                            obj1.put("laundry",userdataModel.mUserItems.get(0).mLaundryID);
                            obj1.put("received_type","laundry");
                            obj1.put("train_no",train_no);
                            obj1.put("depot_code",depot_code);
                            obj1.put("image",imageresponse1);
                            obj1.put("image1",imageresponse2);
                            obj1.put("image2",imageresponse3);
                            obj1.put("image3",imageresponse4);
                            obj1.put("image4",imageresponse5);
                            obj1.put("image5",imageresponse6);
                            obj1.put("image6",imageresponse7);
                            obj1.put("image7",imageresponse8);
                            obj1.put("image8",imageresponse9);
                            obj1.put("image9",imageresponse10);
                            obj1.put("signature", signatureresponse1);
                            obj1.put("remark", remark);
                            obj1.put("penalty",penalty);
                            obj1.put("status", "0");


                            obj1.put("Testcheck_Items", jsonArray);

                            requestBody = obj1.toString();
                        }catch (Exception e){

                        }
                        Log.v("requestBody",requestBody);


                        StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitTestCheckData,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();

                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            message = jsonObject.getString("message");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        map.clear();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TakeCameraLaundry.this);
                                        //  builder.setTitle("Message")
                                        builder.setMessage(response)
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int a) {
                                                        Intent i = new Intent(TakeCameraLaundry.this, BufferStockIssuetoDepot.class);
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

                                Toast.makeText(TakeCameraLaundry.this, "Error" + error, Toast.LENGTH_SHORT).show();

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
                        RequestQueue requestQueue = Volley.newRequestQueue(TakeCameraLaundry.this);
                        requestQueue.add(stringRequest);
                    }
                }
            }
        });
    }
    private void checkPermission(final int REQUEST_CODE) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileUri = O.cameraProcess(TakeCameraLaundry.this, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
        }
    }

    private void uploadsignature(String path, int n) {
        showLoading("uploading sign" + n);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Sing,
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
        RequestQueue rQueue = Volley.newRequestQueue(TakeCameraLaundry.this);
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

        //...................CAMERA_CAPTURE_IMAGE_REQUEST_CODE1......................
        Log.e("IntentData", "" + data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(35);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path1 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap1(file_path1);
                        iv_image1.setImageBitmap(BitmapFactory.decodeFile(file_path1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
            //...................CAMERA_CAPTURE_IMAGE_REQUEST_CODE2......................
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap1 = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap1);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap1.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap1.getHeight() - 35, paint);
                    try {
                        file_path2 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap1, 80);
                        uploadBitmap2(file_path2);
                        iv_image2.setImageBitmap(BitmapFactory.decodeFile(file_path2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }

            //...................CAMERA_CAPTURE_IMAGE_REQUEST_CODE3......................
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE3 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path3 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap3(file_path3);
                        iv_image3.setImageBitmap(BitmapFactory.decodeFile(file_path3));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
            //...................CAMERA_CAPTURE_IMAGE_REQUEST_CODE4......................

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE4 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path4 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap4(file_path4);
                        iv_image4.setImageBitmap(BitmapFactory.decodeFile(file_path4));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE5 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path5 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap5(file_path5);
                        iv_image5.setImageBitmap(BitmapFactory.decodeFile(file_path5));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE6 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path6 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap6(file_path6);
                        iv_image6.setImageBitmap(BitmapFactory.decodeFile(file_path6));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE7 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path7 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap7(file_path7);
                        iv_image7.setImageBitmap(BitmapFactory.decodeFile(file_path7));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE8 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path8 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap8(file_path8);
                        iv_image8.setImageBitmap(BitmapFactory.decodeFile(file_path8));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE9 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path9 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap9(file_path9);
                        iv_image9.setImageBitmap(BitmapFactory.decodeFile(file_path9));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE10 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCameraLaundry.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path10 = O.savefile(TakeCameraLaundry.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap10(file_path10);
                        iv_image10.setImageBitmap(BitmapFactory.decodeFile(file_path10));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakeCameraLaundry.this);
            }
        }
    }
    private void uploadBitmap1(final String filepath1) {
        showLoading("Uploading Image1...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse1 = new String(response.data);
                        Log.e("imageresponse1", new String(response.data));
                        rQueue.getCache().clear();
                    }

                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath1)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap2(final String filepath2) {
        showLoading("Uploading Image2...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse2 = new String(response.data);
                        Log.e("imageresponse2", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath2)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap3(final String filepath3) {
        showLoading("Uploading Image3...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse3 = new String(response.data);
                        Log.e("imageresponse3", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath3)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap4(final String filepath4) {
        showLoading("Uploading Image4...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse4 = new String(response.data);
                        Log.e("imageresponse4", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath4)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap5(final String filepath5) {
        showLoading("Uploading Image5...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse5 = new String(response.data);
                        Log.e("imageresponse5", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath5)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap6(final String filepath6) {
        showLoading("Uploading Image6...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse6 = new String(response.data);
                        Log.e("imageresponse6", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath6)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap7(final String filepath7) {
        showLoading("Uploading Image7...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse7 = new String(response.data);
                        Log.e("imageresponse7", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath7)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap8(final String filepath8) {
        showLoading("Uploading Image8...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse8 = new String(response.data);
                        Log.e("imageresponse8", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath8)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap9(final String filepath9) {
        showLoading("Uploading Image9...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse9 = new String(response.data);
                        Log.e("imageresponse9", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath9)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }
    private void uploadBitmap10(final String filepath) {
        showLoading("Uploading Image10...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse10 = new String(response.data);
                        Log.e("imageresponse10", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }

            }
        }
    }

    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(TakeCameraLaundry.this);
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
