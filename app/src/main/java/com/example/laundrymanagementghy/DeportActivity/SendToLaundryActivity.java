package com.example.laundrymanagementghy.DeportActivity;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.EditPackage;
import com.example.laundrymanagementghy.Amodel.LaundryList;
import com.example.laundrymanagementghy.Amodel.SaveLaundry;
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

public class SendToLaundryActivity extends AppCompatActivity {
    private final static String Getlaundrylist = "http://lmskyq.projectrailway.in/Api/depot_sent_to_laundry";
    private final static String Save_Laundry = "http://lmskyq.projectrailway.in/Api/save_sent_to_laundry";
    private final static String Laundry_Type = "http://lmskyq.projectrailway.in/Api/get_all_laundry";
    private final static String GET_COACH_TYPE = "http://lmskyq.projectrailway.in/Api/get_coach";
    private final static String Train_List = "http://lmskyq.projectrailway.in/Api/get_trains";
    private final static String Update_API = "http://lmskyq.projectrailway.in/Api/save_sent_to_laundry";
    ImageView iv_add_laundry;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    AlertDialog dialog;
    String deport_code = "";
    final Calendar myCalendar = Calendar.getInstance();
    LaundryAdapter laundryAdapter;
    UserDataModel userdataModel;
    ArrayList<String> comp_list = new ArrayList<>(), comp_id_list = new ArrayList<>();
    ArrayList<String> coach_list = new ArrayList<>(), coach_id_list = new ArrayList<>();

    ArrayList<String> train_list = new ArrayList<>(), train_id_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_laundry);
        deport_code = getIntent().getStringExtra("deport_code");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.view_laundry);
        tv_empty_data = findViewById(R.id.tv_empty_data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        laundryAdapter = new LaundryAdapter(new ArrayList<>());
        recyclerView.setAdapter(laundryAdapter);
        laundryAdapter.notifyDataSetChanged();

        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(SendToLaundryActivity.this)) {
                    SentLaundryList();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });
        SentLaundryList();

        iv_add_laundry = findViewById(R.id.iv_add_laundry);
        iv_add_laundry.findViewById(R.id.iv_add_laundry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLogoutAlertDialog();
            }
        });

    }

    private void showLogoutAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.diolog_add_package);
        final ImageView iv_calenders = dialog.findViewById(R.id.iv_calender);
        final EditText et_date = dialog.findViewById(R.id.et_date);
        final EditText et_select_laundry = dialog.findViewById(R.id.et_select_laundry);
        final EditText et_select_coach = dialog.findViewById(R.id.et_select_coach);
        final EditText et_select_train = dialog.findViewById(R.id.et_select_train);
        final EditText et_no_of_bag = dialog.findViewById(R.id.et_no_of_bag);
        final EditText et_bed_sheet = dialog.findViewById(R.id.et_bed_sheet);
        final EditText et_pillow_cover = dialog.findViewById(R.id.et_pillow_cover);
        final EditText et_face_towel = dialog.findViewById(R.id.et_face_towel);
        final EditText et_blanket_cover = dialog.findViewById(R.id.et_blanket_cover);
        final EditText et_bath_towel = dialog.findViewById(R.id.et_bath_towel);
        final EditText et_blanket = dialog.findViewById(R.id.et_blanket);
        final TextView et_remark = dialog.findViewById(R.id.et_remark);
        final EditText et_total_packet = dialog.findViewById(R.id.et_total_packet);


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
                DatePickerDialog dpd = new DatePickerDialog(SendToLaundryActivity.this, journeyDate1, myCalendar
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
        GetLaundryType();
        et_select_laundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                    // builder.setTitle("Select Laundry...");
                    builder.setItems(comp_list.toArray(new CharSequence[comp_list.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                et_select_laundry.setText(comp_list.get(which));
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
        GetTrainType();
        et_select_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                    // builder.setTitle("Select Train No...");
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
        GetCoachType();
        et_select_coach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                    // builder.setTitle("Select Train No...");
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
        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_date.getText().toString())) {
                    Toast.makeText(SendToLaundryActivity.this, "Select Date",
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_select_laundry.getText().toString())) {
                    Toast.makeText(SendToLaundryActivity.this, "Select Laundry",
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_select_coach.getText().toString())) {
                    Toast.makeText(SendToLaundryActivity.this, "Select Coach",
                            Toast.LENGTH_LONG).show();

                } else {
                    try {
                        String laundry_id = comp_id_list.get(comp_list.indexOf(et_select_laundry.getText().toString()));
                        SaveLaundryPackage(dialog, et_date.getText().toString(), laundry_id,
                                et_select_train.getText().toString(),
                                et_select_coach.getText().toString(),
                                et_no_of_bag.getText().toString(),
                                et_bed_sheet.getText().toString(),
                                et_pillow_cover.getText().toString(),
                                et_face_towel.getText().toString(),
                                et_blanket_cover.getText().toString(),
                                et_bath_towel.getText().toString(),
                                et_blanket.getText().toString(),
                                et_remark.getText().toString(),
                                et_total_packet.getText().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

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

    private void SaveLaundryPackage(Dialog dialog, String date, String laundry_id, String train_no,
                                    String coach, String no_of_bag, String bs, String pc, String ft, String blanket_cover,
                                    String bath_towel, String blanket, String remark, String total) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("date", date);
            jsonObject.put("laundry_id", laundry_id);
            jsonObject.put("depot_id", userdataModel.mUserItems.get(0).mLogin_id);
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
            jsonObject.put("train_no", train_no);
            jsonObject.put("coach", coach);
            jsonObject.put("no_of_bag", no_of_bag);
            jsonObject.put("bs_first_ac", "");
            jsonObject.put("pc_first_ac", "");
            jsonObject.put("bs", bs);
            jsonObject.put("pc", pc);
            jsonObject.put("ft", ft);
            jsonObject.put("blanket_cover", blanket_cover);
            jsonObject.put("bath_towel", bath_towel);
            jsonObject.put("blanket", blanket);
            jsonObject.put("remark", remark);
            jsonObject.put("total", total);


        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("response", requestBody);
        showLoading("Please wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Save_Laundry, new Response.Listener<String>() {
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

                        showConfirmationDialog(message);
                    } else {
                        showConfirmationDialog(response);
                    }
                    Type listType = new TypeToken<List<SaveLaundry>>() {
                    }.getType();
                    ArrayList<SaveLaundry> getList = new Gson().fromJson(response.toString(), listType);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SendToLaundryActivity.this);
        requestQueue.add(stringRequest);
    }


    private void GetLaundryType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Laundry_Type, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("laundry_data");
                            comp_list.clear();
                            comp_list.add(0, "Select Laundry");
                            comp_id_list.clear();
                            comp_id_list.add(0, "Select Laundry");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                comp_list.add(obj.getString("laundry_name"));
                                comp_id_list.add(obj.getString("laundry_id"));


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

    private void GetTrainType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Train_List, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString()); ////
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


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(SendToLaundryActivity.this);
        requestQueue.add(objectRequest);
    }

    private void SentLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
//            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
            jsonObject.put("depot_code","KYQ");
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Getlaundrylist,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            LaundryList laundryList = new Gson().fromJson(response.toString(), LaundryList.class);
                            recyclerView.setAdapter(new LaundryAdapter(laundryList.mLaundryList));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }


            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class LaundryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<LaundryList.LaundryItem> list;

        public LaundryAdapter(ArrayList<LaundryList.LaundryItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_add_laundry_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
            final int position = pos;
            holder.tv_index.setText((position + 1) + "");
            holder.tv.setText(list.get(position).mDate);
            holder.tv1.setText(list.get(position).mLaundry_name);
            holder.tv2.setText(list.get(position).mTotal);
            holder.tv3.setText(list.get(position).mDepot_code);
            holder.tv4.setText(list.get(position).mCoach);
            holder.tv5.setText(list.get(position).mBs);
            holder.tv6.setText(list.get(position).mBlanket);
            holder.tv7.setText(list.get(position).mPc);
            holder.tv8.setText(list.get(position).mFt);
            holder.tv9.setText(list.get(position).mBlanket_cover);
            holder.tv10.setText(list.get(position).mBath_towel);

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }


            holder.bt_status.setText(list.get(position).mDelivery_status);
            holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogoutAlertDialog();
                }

                private void showLogoutAlertDialog() {
                    final Dialog dialog = new Dialog(SendToLaundryActivity.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_edit_package);
                    final ImageView iv_calender = dialog.findViewById(R.id.iv_calender);
                    final EditText et_date = dialog.findViewById(R.id.et_date);
                    final EditText et_select_laundry = dialog.findViewById(R.id.et_select_laundry);
                    final EditText et_select_coach = dialog.findViewById(R.id.et_select_coach);
                    final EditText et_select_train = dialog.findViewById(R.id.et_select_train);
                    final EditText et_no_of_bag = dialog.findViewById(R.id.et_no_of_bag);
                    final EditText et_bed_sheet = dialog.findViewById(R.id.et_bed_sheet);
                    final EditText et_pillow_cover = dialog.findViewById(R.id.et_pillow_cover);
                    final EditText et_face_towel = dialog.findViewById(R.id.et_face_towel);
                    final EditText et_blanket_cover = dialog.findViewById(R.id.et_blanket_cover);
                    final EditText et_bath_towel = dialog.findViewById(R.id.et_bath_towel);
                    final EditText et_blanket = dialog.findViewById(R.id.et_blanket);
                    final TextView et_remark = dialog.findViewById(R.id.et_remark);
                    final EditText et_total_packet = dialog.findViewById(R.id.et_total_packet);

                    et_date.setText(list.get(position).mDate);
                    et_select_laundry.setText(list.get(position).mLaundry_name);
                    et_select_train.setText(list.get(position).mTrain_no);
                    et_select_coach.setText(list.get(position).mCoach);
                    et_no_of_bag.setText(list.get(position).mNo_of_bag);
                    et_bed_sheet.setText(list.get(position).mBs);
                    et_pillow_cover.setText(list.get(position).mPc);
                    et_face_towel.setText(list.get(position).mFt);
                    et_blanket_cover.setText(list.get(position).mBlanket_cover);
                    et_bath_towel.setText(list.get(position).mBath_towel);
                    et_blanket.setText(list.get(position).mBlanket);
                    et_remark.setText(list.get(position).mRemark);
                    et_total_packet.setText(list.get(position).mTotal);

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
                            DatePickerDialog dpd = new DatePickerDialog(SendToLaundryActivity.this, journeyDate1, myCalendar
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
                    GetLaundryType();
                    et_select_laundry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                                // builder.setTitle("Select Laundry...");
                                builder.setItems(comp_list.toArray(new CharSequence[comp_list.size()]), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {

                                            et_select_laundry.setText(comp_list.get(which));
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

                    GetLaundryType();
                    et_select_train.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                                //builder.setTitle("Select Train No..");
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
                    et_select_coach.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SendToLaundryActivity.this);
                                //builder.setTitle("Select Train No..");
                                builder.setItems(train_list.toArray(new CharSequence[train_list.size()]), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {

                                            et_select_coach.setText(train_list.get(which));
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


                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(et_date.getText().toString())) {
                                Toast.makeText(SendToLaundryActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    String laundry_id = comp_id_list.get(comp_list.indexOf(et_select_laundry.getText().toString()));
                                    EditDataSave(list.get(position).mSent_id, laundry_id, et_date.getText().toString(),
                                            et_select_train.getText().toString(),
                                            et_select_coach.getText().toString(),
                                            et_no_of_bag.getText().toString(),
                                            et_bed_sheet.getText().toString(),
                                            et_pillow_cover.getText().toString(),
                                            et_face_towel.getText().toString(),
                                            et_blanket_cover.getText().toString(),
                                            et_bath_towel.getText().toString(),
                                            et_blanket.getText().toString(),
                                            et_remark.getText().toString(),
                                            et_total_packet.getText().toString());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                            }
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


                private void EditDataSave(String stl_id, String laundry_id, String date, String train_no, String coach,
                                          String no_of_bag, String bs_id, String pc_id, String ft_id, String blanket_cover, String bath_towel, String blanket, String remark, String total_id) {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("stl_id", stl_id);
                        jsonObject.put("date", date);
                        jsonObject.put("laundry_id", laundry_id);
                        jsonObject.put("depot_id", userdataModel.mUserItems.get(0).mLogin_id);
                        jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
                        jsonObject.put("train_no", train_no);
                        jsonObject.put("coach", coach);
                        jsonObject.put("no_of_bag", no_of_bag);
                        jsonObject.put("bs_first_ac", "");
                        jsonObject.put("pc_first_ac", "");
                        jsonObject.put("bs", bs_id);
                        jsonObject.put("pc", pc_id);
                        jsonObject.put("ft", ft_id);
                        jsonObject.put("blanket_cover", blanket_cover);
                        jsonObject.put("bath_towel", bath_towel);
                        jsonObject.put("blanket", blanket);
                        jsonObject.put("remark", remark);
                        jsonObject.put("total", total_id);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonObject.toString();
                    Log.e("reqbody", requestBody);
                    showLoading("Please wait...");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Update_API, new Response.Listener<String>() {
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
                                Type listType = new TypeToken<List<EditPackage>>() {
                                }.getType();
                                ArrayList<EditPackage> getList = new Gson().fromJson(response.toString(), listType);

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
                    RequestQueue requestQueue = Volley.newRequestQueue(SendToLaundryActivity.this);
                    requestQueue.add(stringRequest);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv, tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv10;
        ImageView iv_edit, iv_delete;
        Button bt_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
            tv = (TextView) itemView.findViewById(R.id.tv_date);
            tv1 = (TextView) itemView.findViewById(R.id.tv_laundry);
            tv2 = (TextView) itemView.findViewById(R.id.tv_Total_Items);
            tv3 = (TextView) itemView.findViewById(R.id.tv_depot);

            tv4 = (TextView) itemView.findViewById(R.id.tv_coach);
            tv5 = (TextView) itemView.findViewById(R.id.tv_bed_sheet);
            tv6 = (TextView) itemView.findViewById(R.id.tv_blanket);
            tv7 = (TextView) itemView.findViewById(R.id.tv_pillow_cover);
            tv8 = (TextView) itemView.findViewById(R.id.tv_face_towel);
            tv9 = (TextView) itemView.findViewById(R.id.tv_blanket_cover);
            tv10 = (TextView) itemView.findViewById(R.id.tv_bath_towel);
            bt_status = (Button) itemView.findViewById(R.id.tv_status);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);

        }

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
        final Dialog dialog = new Dialog(SendToLaundryActivity.this);
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
                Intent intent = new Intent(SendToLaundryActivity.this, SendToLaundryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
}
