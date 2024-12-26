package com.example.laundrymanagementghy.DeportActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.UiModeManager;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.EditReceived;

import com.example.laundrymanagementghy.Amodel.SaveReceived;
import com.example.laundrymanagementghy.Amodel.SoildedReturnToLaundryUpdateModel;
import com.example.laundrymanagementghy.Amodel.SupplyList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.LaundryActivity.BedrollStocking;
import com.example.laundrymanagementghy.LaundryActivity.FreshBedrollReceiptfromLaundry;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.model.GetStockModel;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceivedFromTrainActivity extends AppCompatActivity {
    private final static String Received_from_train = "http://lmsguwahati.projectrailway.in/api/soiled_return_to_laundry";
    private final static String Train_List = "http://lmsguwahati.projectrailway.in/Api/get_trains";
    private final static String GET_COACH_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_coach";
    private final static String ADD_RECEIVE_RROM_TRAIN = "http://lmsguwahati.projectrailway.in/Api/save_received_from_train";
    private final static String Update_API = "http://lmsguwahati.projectrailway.in/api/save_soiled_return_to_laundry";
    private final static String update_store_status = "http://lmsguwahati.projectrailway.in/api/verify_and_send_to_laundry";
    private final static String LAUNDRY_API = "http://lmsguwahati.projectrailway.in/Api/get_all_laundry";
    ImageView iv_add_received;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    String deport_code = "";
    AlertDialog dialog;
    final Calendar myCalendar = Calendar.getInstance();
    ReceivedAapter receivedAapter;
    String depot_code;
    UserDataModel userDataModel;
    ArrayList<String> train_list = new ArrayList<>(), train_id_list = new ArrayList<>();
    ArrayList<String> coach_list = new ArrayList<>(), coach_id_list = new ArrayList<>();
    ArrayList<String> launddy_list = new ArrayList<>(), laundry_id_list = new ArrayList<>();
    ArrayAdapter<String> adapter_laundry;
    UiModeManager uiModeManager;
    //search filter ke liye
    private ArrayList<SupplyList.TrainItem> stockDataItemList = new ArrayList<>();
    EditText et_dateFrom, et_dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_from_train);
        deport_code = getIntent().getStringExtra("deport_code");
        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        try {

            userDataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);

        uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));

        } else {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }


//yaha ye adapter ki problem thi sari ki sari

//        recyclerView = (RecyclerView) findViewById(R.id.view1AR);
        tv_empty_data = findViewById(R.id.tv_empty_data);
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        receivedAapter = new ReceivedAapter(stockDataItemList);
        setupRecyclerView();
//        recyclerView.setAdapter(receivedAapter);
//        receivedAapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                if (O.checkNetwork(ReceivedFromTrainActivity.this)) {
                    SentLaundryList();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });
        SentLaundryList();

        iv_add_received = findViewById(R.id.iv_add_received);
//        iv_add_received.findViewById(R.id.iv_add_received).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //showLogoutAlertDialog();
//            }
//        });

        setupDatePickers(uiModeManager);

        et_dateFrom.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });
        et_dateTo.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });

    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.view1AR);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupDatePickers(UiModeManager uiModeManager) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        final DatePickerDialog.OnDateSetListener journeyDateFrom = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateFrom.setText(dateFormat.format(calendar.getTime()));

            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                et_dateFrom.setTextColor(getResources().getColor(R.color.whiteTextColor));
                et_dateTo.setTextColor(getResources().getColor(R.color.whiteTextColor));

            } else {
                et_dateTo.setTextColor(getResources().getColor(R.color.black));
                et_dateFrom.setTextColor(getResources().getColor(R.color.black));
            }

        };

        final DatePickerDialog.OnDateSetListener journeyDateTo = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateTo.setText(dateFormat.format(calendar.getTime()));
        };

        et_dateFrom.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(ReceivedFromTrainActivity.this, journeyDateFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(ReceivedFromTrainActivity.this, journeyDateTo, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void filterBedrollStock() {
        String fromDateString = et_dateFrom.getText().toString();
        String toDateString = et_dateTo.getText().toString();
        if (!TextUtils.isEmpty(fromDateString) && !TextUtils.isEmpty(toDateString)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fromDate = dateFormat.parse(fromDateString);
                Date toDate = dateFormat.parse(toDateString);
                ArrayList<SupplyList.TrainItem> filteredList = new ArrayList<>();

                for (SupplyList.TrainItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mSupply_date);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("FilterDebug", "Filtered List Size: " + filteredList.size());
                if (receivedAapter != null) {
                    receivedAapter.filterList(filteredList);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (receivedAapter != null) {
                receivedAapter.resetFilter();
            }
        }
    }

    public abstract class TextWatcherAdapter implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed
        }
    }


    private void showLogoutAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.diolog_add_received);
        final ImageView iv_calender = dialog.findViewById(R.id.iv_calender);
        final EditText et_date = dialog.findViewById(R.id.et_date);
        final EditText et_select_train = dialog.findViewById(R.id.et_select_train);
        final EditText et_select_coach = dialog.findViewById(R.id.et_select_coach);
        final EditText et_no_of_bag = dialog.findViewById(R.id.et_no_of_bag);
        final EditText et_bed_sheet = dialog.findViewById(R.id.et_bed_sheet);
        final EditText et_pillow_cover = dialog.findViewById(R.id.et_pillow_cover);
        final EditText et_face_towel = dialog.findViewById(R.id.et_face_towel);
        final EditText et_blanket_cover = dialog.findViewById(R.id.et_blanket_cover);
        final EditText et_bath_towel = dialog.findViewById(R.id.et_bath_towel);
        final EditText et_blanket = dialog.findViewById(R.id.et_blanket);
        final EditText et_total_packet = dialog.findViewById(R.id.et_total_packet);
        final EditText et_unused_packet = dialog.findViewById(R.id.et_unused_packet);
        final TextView et_remark = dialog.findViewById(R.id.et_remark);


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
                DatePickerDialog dpd = new DatePickerDialog(ReceivedFromTrainActivity.this, journeyDate1, myCalendar
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

        GetTrainType();
        et_select_train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceivedFromTrainActivity.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceivedFromTrainActivity.this);
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
                    Toast.makeText(ReceivedFromTrainActivity.this, "Select Date",
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_select_train.getText().toString())) {
                    Toast.makeText(ReceivedFromTrainActivity.this, "Select Train",
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_select_coach.getText().toString())) {
                    Toast.makeText(ReceivedFromTrainActivity.this, "Select Coach",
                            Toast.LENGTH_LONG).show();

                } else {
                    try {
                        String train_id = train_id_list.get(train_list.indexOf(et_select_train.getText().toString()));
                        SaveReceivedPackage(dialog, et_date.getText().toString(), train_id,
                                et_select_coach.getText().toString(),
                                et_no_of_bag.getText().toString(),
                                et_bed_sheet.getText().toString(),
                                et_pillow_cover.getText().toString()
                                , et_face_towel.getText().toString(),
                                et_blanket_cover.getText().toString(),
                                et_bath_towel.getText().toString(),
                                et_blanket.getText().toString(),
                                et_total_packet.getText().toString(),
                                et_unused_packet.getText().toString(),
                                et_remark.getText().toString());
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

    private void SaveReceivedPackage(Dialog dialog, String date, String train_id, String coach, String no_of_bag,
                                     String bs, String pc, String ft, String blanket_cover, String bath_towel,
                                     String blanket, String total, String unused_packet, String et_remark) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("date", date);
            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);
            jsonObject.put("train_id", train_id);
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
            jsonObject.put("total", total);
            jsonObject.put("unused_packet", unused_packet);
            jsonObject.put("et_remark", et_remark);


        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("response", requestBody);
        showLoading("Please wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ADD_RECEIVE_RROM_TRAIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    Log.e("responseAdd", response);
                    Log.e("responserespo2", response);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (jsonResponse != null && jsonResponse.has("message")) {
                        String message = jsonResponse.getString("message");

                        showConfirmationDialog(message, uiModeManager);
                    } else {
                        showConfirmationDialog(response, uiModeManager);
                    }
                    Type listType = new TypeToken<List<SaveReceived>>() {
                    }.getType();
                    ArrayList<SaveReceived> getList = new Gson().fromJson(response.toString(), listType);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
        requestQueue.add(stringRequest);
    }

    private void GetTrainType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);
            showLoading("Loading question...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Train_List, jsonObject,
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
        requestQueue.add(objectRequest);
    }

    private void SentLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Received_from_train,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
//                        Log.d("response_req", response);
                        Log.e("responserespo1", response);
                        try {
                            SupplyList supplyList = new Gson().fromJson(response.toString(), SupplyList.class);

                            stockDataItemList.clear();
                            stockDataItemList.addAll(supplyList.mSupplyList);
                            if (receivedAapter == null) {
                                receivedAapter = new ReceivedAapter(stockDataItemList);
                                recyclerView.setAdapter(receivedAapter);
                            } else {
                                receivedAapter.notifyDataSetChanged();
                            }

//                            recyclerView.setAdapter(new ReceivedAapter(supplyList.mSupplyList));
                            checkEmptyData();
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

    private void checkEmptyData() {
        if (stockDataItemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tv_empty_data.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_empty_data.setVisibility(View.GONE);
        }
    }

    public class ReceivedAapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<SupplyList.TrainItem> list;
        private ArrayList<SupplyList.TrainItem> mListFull;


        private ArrayList<SoildedReturnToLaundryUpdateModel.ReturnedItemFromSoilde> updateList;

        public ReceivedAapter(ArrayList<SupplyList.TrainItem> list) {
            this.list = new ArrayList<>(list);
            this.mListFull = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_rama_received_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
            final int position = pos;
            holder.tv_index.setText((position + 1) + "");

            holder.tv.setText(list.get(position).mSupply_date);
            holder.tv1.setText(list.get(position).mTrain_no);
            holder.tv2.setText(list.get(position).mLaundry);


            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }

            if (list.get(position).mStatus.equals("0")) {
                holder.tv_verify.setText("UnVerify");
                holder.tv_verify.setTextSize(10);

                holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLogoutAlertDialog(
                                list.get(position).mPillow,
                                list.get(position).mStatus,
                                list.get(position).mSupply_id,
                                list.get(position).mLaundryId,
                                list.get(position).mDepot_code,
                                list.get(position).mSupply_date,
                                list.get(position).mTrain_no,
                                list.get(position).mCoach,
                                list.get(position).mLaundry,
                                list.get(position).mBs,
                                list.get(position).mPc,
                                list.get(position).mFt,
                                list.get(position).mBlanket_cover,
                                list.get(position).mBath_towel,
                                list.get(position).mBlanket,
                                list.get(position).mDepot_Remark,
// unused item
                                list.get(position).mBs_unsed,
                                list.get(position).mPillow_unused,
                                list.get(position).mPc_unused,
                                list.get(position).mBlanket_unused,
                                list.get(position).mBlc_unused,
                                list.get(position).mHt_unused

                        );
                    }


                    private void showLogoutAlertDialog(
                            String pillow,
                            String status,
                            String rowId,
                            String laundryId,
                            String depot_code,
                            String mDate,
                            String mTrain_no,
                            String mCoach,
                            String mLaundry_name,
                            String mBs,
                            String mPc,
                            String mFt,
                            String mBlanket_cover,
                            String mBath_towel,
                            String mBlanket,
                            String mRemark,

                            //unused item
                            String mbs_u,
                            String mpillow_u,
                            String mpillowcover_u,
                            String mblanket_u,
                            String mblanketcover_u,
                            String mht_u
                    ) {
                        final Dialog dialog = new Dialog(ReceivedFromTrainActivity.this, R.style.Dialog);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        //dialog.setCancelable(false);
                        dialog.setContentView(R.layout.diolog_edit_received3);

                        final TextView tv_date = dialog.findViewById(R.id.tv_dated);
                        final TextView tv_select_train = dialog.findViewById(R.id.tv_train_Nod);
                        final TextView tv_select_coach = dialog.findViewById(R.id.tv_coachd);
                        final TextView tv_select_laundry = dialog.findViewById(R.id.et_laundryd);
                        final TextView tv_bed_sheet = dialog.findViewById(R.id.qty_bed_sheet_d);
                        final TextView tv_pillow_cover = dialog.findViewById(R.id.qty_pillow_cover_d);
                        final TextView tv_pillow = dialog.findViewById(R.id.qty_pillow_d);
                        final TextView tv_face_towel = dialog.findViewById(R.id.qty_face_towel_d);
                        final TextView tv_blanket_cover = dialog.findViewById(R.id.qty_blanket_cover_d);
//                        final TextView et_BathTowel = dialog.findViewById(R.id.qty_bath_towel_d);
                        final TextView tv_blanket = dialog.findViewById(R.id.qty_blanket_d);
                        final TextView tv_remark = dialog.findViewById(R.id.et_remarkd);

                        final EditText et_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet);
                        final EditText et_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d);
                        final EditText et_pillow = dialog.findViewById(R.id.et_qty_pillow_d); //yaha data jab bhi submit hoga tab pillow return mai jayega
                        final EditText et_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d);
                        final EditText et_Blanket = dialog.findViewById(R.id.et_qty_blanket_d);
                        final EditText et_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d);
//                        final EditText et_BathTowel = dialog.findViewById(R.id.et_qty_bath_towel_d);
                        final EditText et_remark = dialog.findViewById(R.id.et_remarkd);

                        //unused item editText
                        final EditText et_unused_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet_unUsed);
                        final EditText et_unused_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d_unUsed);
                        final EditText et_unused_pillow = dialog.findViewById(R.id.et_qty_pillow_d_unUsed); //yaha data jab bhi submit hoga tab pillow return mai jayega
                        final EditText et_unused_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d_unUsed);
                        final EditText et_unused_Blanket = dialog.findViewById(R.id.et_qty_blanket_d_unUsed);
                        final EditText et_unused_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d_unUsed);

                        setTextOrDefault(et_BedSheet, list.get(position).bsReturn);
                        setTextOrDefault(et_pillowCover, list.get(position).pcReturn);
                        setTextOrDefault(et_pillow, list.get(position).mPillow_return);
                        setTextOrDefault(et_FaceTowel, list.get(position).ftReturn);
                        setTextOrDefault(et_BlanketCover, list.get(position).blkReturn);
                        setTextOrDefault(et_Blanket, list.get(position).blanketReturn);

                        setTextOrDefault(et_unused_BlanketCover, list.get(position).mBlc_unused);
                        setTextOrDefault(et_unused_Blanket, list.get(position).mBlanket_unused);
                        setTextOrDefault(et_unused_BedSheet, list.get(position).mBs_unsed);
                        setTextOrDefault(et_unused_pillowCover, list.get(position).mPc_unused);
                        setTextOrDefault(et_unused_pillow, list.get(position).mPillow_unused);
                        setTextOrDefault(et_unused_FaceTowel, list.get(position).mHt_unused);

//                        if (list.get(position).bathTowelReturn != null) {
////                            et_BathTowel.setText(list.get(position).bathTowelReturn);
//                        } else {
//                            et_BathTowel.setText("");
//                        }

                        tv_date.setText(mDate);
                        tv_select_train.setText(mTrain_no);
                        tv_select_coach.setText(mCoach);
                        tv_select_laundry.setText(mLaundry_name);
                        tv_bed_sheet.setText(mBs);
                        tv_pillow_cover.setText(mPc);
                        tv_pillow.setText(pillow);
                        tv_face_towel.setText(mFt);
                        tv_blanket_cover.setText(mBlanket_cover);
                        tv_blanket.setText(mBlanket);
//                        tv_bath_towel.setText(mBath_towel);
                        tv_remark.setText(mRemark);

                        if (status.equals("1")) {
                            dialog.findViewById(R.id.v_positive).setVisibility(View.GONE);
                        } else {
                            dialog.findViewById(R.id.v_positive).setVisibility(View.VISIBLE);
                            dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (
                                            et_BedSheet.getText().toString().isEmpty() ||
                                                    et_Blanket.getText().toString().isEmpty() ||
                                                    et_BlanketCover.getText().toString().isEmpty() ||
                                                    et_FaceTowel.getText().toString().isEmpty() ||
                                                    et_pillow.getText().toString().isEmpty() ||
                                                    et_pillowCover.getText().toString().isEmpty()

                                                    /*|| et_unused_pillow.getText().toString().isEmpty() ||
                                                    et_unused_BedSheet.getText().toString().isEmpty() ||
                                                    et_unused_pillowCover.getText().toString().isEmpty() ||
                                                    et_unused_FaceTowel.getText().toString().isEmpty() ||
                                                    et_unused_BlanketCover.getText().toString().isEmpty() ||
                                                    et_unused_Blanket.getText().toString().isEmpty()*/


                                    ) {
                                        Toast.makeText(ReceivedFromTrainActivity.this, "All Fields are mandatory", Toast.LENGTH_SHORT).show();
                                    } else {
                                        int bedSheetValue = safeParseInt(et_BedSheet.getText().toString());
                                        int unusedBedSheetValue = safeParseInt(et_unused_BedSheet.getText().toString());
                                        int pillowValue = safeParseInt(et_pillow.getText().toString());
                                        int unusedPillowValue = safeParseInt(et_unused_pillow.getText().toString());
                                        int pillowCoverValue = safeParseInt(et_pillowCover.getText().toString());
                                        int unusedPillowCoverValue = safeParseInt(et_unused_pillowCover.getText().toString());
                                        int blanketValue = safeParseInt(et_Blanket.getText().toString());
                                        int unusedBlanketValue = safeParseInt(et_unused_Blanket.getText().toString());
                                        int blanketCoverValue = safeParseInt(et_BlanketCover.getText().toString());
                                        int unusedBlanketCoverValue = safeParseInt(et_unused_BlanketCover.getText().toString());
                                        int faceTowelValue = safeParseInt(et_FaceTowel.getText().toString());
                                        int unusedFaceTowelValue = safeParseInt(et_unused_FaceTowel.getText().toString());

                                        // Retrieve list values safely
                                        int listBedSheetValue = safeParseInt(list.get(position).mBs);
                                        int listPillowValue = safeParseInt(list.get(position).mPillow);
                                        int listPillowCoverValue = safeParseInt(list.get(position).mPc);
                                        int listBlanketValue = safeParseInt(list.get(position).mBlanket);
                                        int listBlanketCoverValue = safeParseInt(list.get(position).mBlanket_cover);
                                        int listFaceTowelValue = safeParseInt(list.get(position).mFt);


                                        if (listBedSheetValue < bedSheetValue ||
                                                unusedBedSheetValue > subtractValues(listBedSheetValue, bedSheetValue)) {

                                            if (listBedSheetValue < bedSheetValue) {
                                                Toast.makeText(getApplicationContext(), "BedSheet value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused BedSheet value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (listPillowValue < pillowValue ||
                                                unusedPillowValue > subtractValues(listPillowValue, pillowValue)) {

                                            if (listPillowValue < pillowValue) {
                                                Toast.makeText(getApplicationContext(), "Pillow value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused Pillow value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (listPillowCoverValue < pillowCoverValue ||
                                                unusedPillowCoverValue > subtractValues(listPillowCoverValue, pillowCoverValue)) {

                                            if (listPillowCoverValue < pillowCoverValue) {
                                                Toast.makeText(getApplicationContext(), "Pillow Cover value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused Pillow Cover value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (listBlanketValue < blanketValue ||
                                                unusedBlanketValue > subtractValues(listBlanketValue, blanketValue)) {

                                            if (listBlanketValue < blanketValue) {
                                                Toast.makeText(getApplicationContext(), "Blanket value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused Blanket value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (listBlanketCoverValue < blanketCoverValue ||
                                                unusedBlanketCoverValue > subtractValues(listBlanketCoverValue, blanketCoverValue)) {

                                            if (listBlanketCoverValue < blanketCoverValue) {
                                                Toast.makeText(getApplicationContext(), "Blanket Cover value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused Blanket Cover value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (listFaceTowelValue < faceTowelValue ||
                                                unusedFaceTowelValue > subtractValues(listFaceTowelValue, faceTowelValue)) {

                                            if (listFaceTowelValue < faceTowelValue) {
                                                Toast.makeText(getApplicationContext(), "Hand Towel value is too large", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Unused Hand Towel value is too large", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                            try {
                                                EditDataSave(
                                                        rowId,
                                                        laundryId,
                                                        mLaundry_name,
                                                        mDate,
                                                        et_pillow.getText().toString(),
                                                        et_BedSheet.getText().toString(),
                                                        et_pillowCover.getText().toString(),
                                                        et_FaceTowel.getText().toString(),
                                                        et_BlanketCover.getText().toString(),
                                                        et_Blanket.getText().toString(),//ok
                                                        et_remark.getText().toString(),

                                                        //unused item
                                                        et_unused_BedSheet.getText().toString(),
                                                        et_unused_pillow.getText().toString(),
                                                        et_unused_pillowCover.getText().toString(),
                                                        et_unused_FaceTowel.getText().toString(),
                                                        et_unused_BlanketCover.getText().toString(),
                                                        et_unused_Blanket.getText().toString()

                                                );//ok


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }
                                private int safeParseInt(String value) {
                                    if (value == null || value.trim().isEmpty()) {
                                        return 0;
                                    } else {
                                        try {
                                            return Integer.parseInt(value.trim());
                                        } catch (NumberFormatException e) {
                                            return 0;
                                        }
                                    }
                                }
                                private int subtractValues(int mbs, int bedsheet) {
                                    return mbs - bedsheet;
                                }

                            });
                        }
                        dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (!isFinishing()) {
                            dialog.show();
                        }


                    }

                    private void setTextOrDefault(EditText editText, String value) {
                        editText.setText(value != null ? value : "");
                    }
//                private void getLaundryType() {
//                    final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, LAUNDRY_API, null,
//                            new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
//                                        Log.e("response", response.toString());
//                                        JSONArray jsonArray = response.getJSONArray("laundry_data");
//                                        launddy_list.clear();
//                                        launddy_list.add(0, "Select Your Laundry");
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            JSONObject obj = jsonArray.getJSONObject(i);
//                                            // laundry_list.add(obj.getString("laundry_name"));
//                                            launddy_list.add(obj.getString("laundry_name"));
//                                            laundry_id_list.add(obj.getString("laundry_id"));
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    //  adapter_laundry.notifyDataSetChanged();
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Log.e("error", "" + error.toString());
//                                }
//                            });
//                    RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
//                    requestQueue.add(objectRequest);
//
//                }


                    private void EditDataSave(
                            String row_id,
                            String laundryId,
                            String laundryname,
                            String date,
                            String pillow,
                            String bs,
                            String pc,
                            String ft,
                            String blanket_cover,
//                            String bath_towel,
                            String blanket,
                            String remark,

                            //unused
                            String ubs,
                            String upillow,
                            String upillocover,
                            String ufacetowel,
                            String ublanketcover,
                            String ublanket
                            ) {

                        final JSONObject jsonObject = new JSONObject();
                        try {
//                            Toast.makeText(ReceivedFromTrainActivity.this, "Laundry id "+laundryId + " "+row_id, Toast.LENGTH_SHORT).show();
                            jsonObject.put("row_id", row_id);
                            jsonObject.put("date", date);
                            jsonObject.put("laundry_id", laundryId); //yaha check karana dobara
                            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);
                            jsonObject.put("bs_return", bs);
                            jsonObject.put("pillow_return", pillow);
                            jsonObject.put("pc_return", pc);
                            jsonObject.put("ft_return", ft);
                            jsonObject.put("blk_return", blanket_cover);
                            jsonObject.put("blanket_return", blanket);
                            jsonObject.put("bathtowel_return", "");
                            jsonObject.put("remark", remark);

                            jsonObject.put("bs_unused", ubs.isEmpty() ? "0" : ubs);
                            jsonObject.put("pillow_unused", upillow.isEmpty() ? "0" : upillow);
                            jsonObject.put("pc_unused", upillocover.isEmpty() ? "0" : upillocover);
                            jsonObject.put("blanket_unused", ublanket.isEmpty() ? "0" : ublanket);
                            jsonObject.put("blc_unused", ublanketcover.isEmpty() ? "0" : ublanketcover);
                            jsonObject.put("ht_unused", ufacetowel.isEmpty() ? "0" : ufacetowel);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String requestBody = jsonObject.toString();
                        Log.e("reqbody", requestBody);
                        showLoading("Please wait...");

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Update_API, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                hideLoading();
                                try {
                                    Log.e("responserespo", response);

                                    JSONObject jsonResponse = null;
                                    try {
                                        jsonResponse = new JSONObject(response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (jsonResponse != null && jsonResponse.has("message")) {
                                        String message = jsonResponse.getString("message");

                                        showConfirmationDialog(message, uiModeManager);
                                    } else {
                                        showConfirmationDialog(response, uiModeManager);
                                    }
                                    Type listType = new TypeToken<List<EditReceived>>() {
                                    }.getType();
                                    ArrayList<EditReceived> getList = new Gson().fromJson(response.toString(), listType);

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
                        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
                        requestQueue.add(stringRequest);
                    }
                });

//                holder.tv_verify.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // UpdateDeliveryAlertDialog();
//
//                    }
//
//                    private void UpdateDeliveryAlertDialog() {
//                        final Dialog dialog = new Dialog(ReceivedFromTrainActivity.this, R.style.Dialog);
//                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                                WindowManager.LayoutParams.MATCH_PARENT);
//                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                        //dialog.setCancelable(false);
//                        dialog.setContentView(R.layout.diolog_update_delivery);
//                        final TextView tv_update = dialog.findViewById(R.id.tv_update);
//
//                        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
////                            if (TextUtils.isEmpty(et_date.getText().toString())) {
////                                Toast.makeText(LaundryReceived.this, "Select Date", Toast.LENGTH_SHORT).show();
////                            } else {
//                                try {
//                                    OkDataSave(dialog, list.get(position).mSupply_id);
//                                } catch (Exception e) {
//                                    throw new RuntimeException(e);
//
//                                }
//                            }
//
//                        });
//
//                        dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//
//
//                    }
//
//                    private void OkDataSave(Dialog dialog, String item_id) {
//                        final JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("row_id", item_id);
//                            jsonObject.put("table_name", "lms_received_from_train");
//                            jsonObject.put("verify_by", userDataModel.mUserItems.get(0).mDepot_code);
//
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        final String requestBody = jsonObject.toString();
//                        Log.e("reqbody", requestBody);
//                        showLoading("Please wait...");
//
//                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                                update_store_status, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                hideLoading();
//                                try {
//                                    Log.e("response", response);
//
//                                    JSONObject jsonResponse = null;
//                                    try {
//                                        jsonResponse = new JSONObject(response);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    if (jsonResponse != null && jsonResponse.has("message")) {
//                                        String message = jsonResponse.getString("message");
//
//                                        showConfirmationDialog(message);
//                                    } else {
//                                        showConfirmationDialog(response);
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                hideLoading();
//                            }
//                        }) {
//                            @Override
//                            public String getBodyContentType() {
//                                return "application/json; charset=utf-8";
//                            }
//
//                            @Override
//                            public byte[] getBody() throws com.android.volley.AuthFailureError {
//                                try {
//                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                                } catch (UnsupportedEncodingException uee) {
//                                    return null;
//                                }
//                            }
//                        };
//                        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
//                        requestQueue.add(stringRequest);
//                    }
//                });
            } else {
                holder.tv_verify.setText("Verified");
                holder.iv_edit.setImageResource(R.drawable.icon_view);
                holder.tv_verify.setTextColor(R.color.colorAccent);
                holder.tv_verify.setTextSize(12);

                holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLogoutAlertDialog(
                                list.get(position).mPillow,
                                list.get(position).mStatus,
                                list.get(position).mSupply_id,
                                list.get(position).mLaundryId,
                                list.get(position).mDepot_code,
                                list.get(position).mSupply_date,
                                list.get(position).mTrain_no,
                                list.get(position).mCoach,
                                list.get(position).mLaundry,
                                list.get(position).mBs,
                                list.get(position).mPc,
                                list.get(position).mFt,
                                list.get(position).mBlanket_cover,
                                list.get(position).mBath_towel,
                                list.get(position).mBlanket,
                                list.get(position).mDepot_Remark,

//                                list.get(position).mUnuseditem

                                list.get(position).mBs_unsed,
                                list.get(position).mPillow_unused,
                                list.get(position).mPc_unused,
                                list.get(position).mBlanket_unused,
                                list.get(position).mBlc_unused,
                                list.get(position).mHt_unused
                        );
                    }

                    private void showLogoutAlertDialog(
                            String pillow,
                            String status,
                            String rowId,
                            String laundryId,
                            String depot_code,
                            String mDate,
                            String mTrain_no,
                            String mCoach,
                            String mLaundry_name,
                            String mBs,
                            String mPc,
                            String mFt,
                            String mBlanket_cover,
                            String mBath_towel,
                            String mBlanket,
                            String mRemark,
//                            String unuseditem
                            String mbs_u,
                            String mpillow_u,
                            String mpillowcover_u,
                            String mblanket_u,
                            String mblanketcover_u,
                            String mht_u
                    ) {
                        final Dialog dialog = new Dialog(ReceivedFromTrainActivity.this, R.style.Dialog);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        //dialog.setCancelable(false);
                        dialog.setContentView(R.layout.diolog_edit_received3);

                        final TextView tv_date = dialog.findViewById(R.id.tv_dated);
                        final TextView tv_select_train = dialog.findViewById(R.id.tv_train_Nod);
                        final TextView tv_select_coach = dialog.findViewById(R.id.tv_coachd);
                        final TextView tv_select_laundry = dialog.findViewById(R.id.et_laundryd);
                        final TextView tv_bed_sheet = dialog.findViewById(R.id.qty_bed_sheet_d);
                        final TextView tv_pillow_cover = dialog.findViewById(R.id.qty_pillow_cover_d);
                        final TextView tv_pillow = dialog.findViewById(R.id.qty_pillow_d);
                        final TextView tv_face_towel = dialog.findViewById(R.id.qty_face_towel_d);
                        final TextView tv_blanket_cover = dialog.findViewById(R.id.qty_blanket_cover_d);
//                        final TextView tv_bath_towel = dialog.findViewById(R.id.qty_bath_towel_d);
                        final TextView tv_blanket = dialog.findViewById(R.id.qty_blanket_d);
                        final TextView tv_remark = dialog.findViewById(R.id.et_remarkd);


                        //supply id
                        //date
                        //laundry id
                        //depot code
//                        final EditText et_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet);
//                        final EditText et_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d);
//                        final EditText et_pillow = dialog.findViewById(R.id.et_qty_pillow_d);
//                        final EditText et_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d);
//                        final EditText et_Blanket = dialog.findViewById(R.id.et_qty_blanket_d);
//                        final EditText et_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d);
////                        final EditText et_BathTowel = dialog.findViewById(R.id.et_qty_bath_towel_d);
//                        final EditText et_remark = dialog.findViewById(R.id.et_remarkd);
//
//                        if (list.get(position).bsReturn != null) {
//                            et_BedSheet.setText(list.get(position).bsReturn);
//                        } else {
//                            et_BedSheet.setText("");
//                        }
//                        if (list.get(position).pcReturn != null) {
//                            et_pillowCover.setText(list.get(position).pcReturn);
//                        } else {
//                            et_pillowCover.setText("");
//
//                        }
//                        if (list.get(position).ftReturn != null) {
//                            et_FaceTowel.setText(list.get(position).ftReturn);
//                        } else {
//                            et_FaceTowel.setText("");
//
//                        }
//                        if (list.get(position).blkReturn != null) {
//                            et_BlanketCover.setText(list.get(position).blkReturn);
//                        } else {
//                            et_BlanketCover.setText("");
//
//                        }
//                        if (list.get(position).blanketReturn != null) {
//                            et_Blanket.setText(list.get(position).blanketReturn);
//                        } else {
//                            et_Blanket.setText("");
//
//                        }
////                        if (list.get(position).bathTowelReturn != null) {
////                            et_BathTowel.setText(list.get(position).bathTowelReturn);
////                        } else {
////                            et_BathTowel.setText("");
////                        }
//
//                        if (list.get(position).mPillow_return != null) {
//                            et_pillow.setText(list.get(position).mPillow_return);
//                        } else {
//                            et_pillow.setText("");
//                        }
                        final EditText et_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet);
                        final EditText et_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d);
                        final EditText et_pillow = dialog.findViewById(R.id.et_qty_pillow_d); //yaha data jab bhi submit hoga tab pillow return mai jayega
                        final EditText et_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d);
                        final EditText et_Blanket = dialog.findViewById(R.id.et_qty_blanket_d);
                        final EditText et_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d);
//                        final EditText et_BathTowel = dialog.findViewById(R.id.et_qty_bath_towel_d);
                        final EditText et_remark = dialog.findViewById(R.id.et_remarkd);

                        //unused item editText
                        final EditText et_unused_BedSheet = dialog.findViewById(R.id.et_qty_bed_sheet_unUsed);
                        final EditText et_unused_pillowCover = dialog.findViewById(R.id.et_qty_pillow_cover_d_unUsed);
                        final EditText et_unused_pillow = dialog.findViewById(R.id.et_qty_pillow_d_unUsed); //yaha data jab bhi submit hoga tab pillow return mai jayega
                        final EditText et_unused_FaceTowel = dialog.findViewById(R.id.et_qty_face_towel_d_unUsed);
                        final EditText et_unused_Blanket = dialog.findViewById(R.id.et_qty_blanket_d_unUsed);
                        final EditText et_unused_BlanketCover = dialog.findViewById(R.id.et_qty_blanket_cover_d_unUsed);

                        setTextOrDefault(et_BedSheet, list.get(position).bsReturn);
                        setTextOrDefault(et_pillowCover, list.get(position).pcReturn);
                        setTextOrDefault(et_pillow, list.get(position).mPillow_return);
                        setTextOrDefault(et_FaceTowel, list.get(position).ftReturn);
                        setTextOrDefault(et_BlanketCover, list.get(position).blkReturn);
                        setTextOrDefault(et_Blanket, list.get(position).blanketReturn);

                        setTextOrDefault(et_unused_BlanketCover, list.get(position).mBlc_unused);
                        setTextOrDefault(et_unused_Blanket, list.get(position).mBlanket_unused);
                        setTextOrDefault(et_unused_BedSheet, list.get(position).mBs_unsed);
                        setTextOrDefault(et_unused_pillowCover, list.get(position).mPc_unused);
                        setTextOrDefault(et_unused_pillow, list.get(position).mPillow_unused);
                        setTextOrDefault(et_unused_FaceTowel, list.get(position).mHt_unused);

                        tv_date.setText(mDate);
                        tv_select_train.setText(mTrain_no);
                        tv_select_coach.setText(mCoach);
                        tv_select_laundry.setText(mLaundry_name);
                        tv_bed_sheet.setText(mBs);
                        tv_pillow_cover.setText(mPc);
                        tv_pillow.setText(pillow);
                        tv_face_towel.setText(mFt);
                        tv_blanket_cover.setText(mBlanket_cover);
                        tv_blanket.setText(mBlanket);
//                        tv_bath_towel.setText(mBath_towel);
                        tv_remark.setText(mRemark);

                        if (status.equals("1")) {
                            dialog.findViewById(R.id.v_positive).setVisibility(View.GONE);
                        } else {
                            dialog.findViewById(R.id.v_positive).setVisibility(View.VISIBLE);
                            dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    if (Integer.parseInt(list.get(position).mBs) < Integer.parseInt(et_BedSheet.getText().toString())) {
//                                        Toast.makeText(ReceivedFromTrainActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
//                                    } else if (Integer.parseInt(list.get(position).mFt) < Integer.parseInt(et_FaceTowel.getText().toString())) {
//                                        Toast.makeText(ReceivedFromTrainActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
//                                    } else if (Integer.parseInt(list.get(position).mBlanket_cover) < Integer.parseInt(et_BlanketCover.getText().toString())) {
//                                        Toast.makeText(ReceivedFromTrainActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
//                                    } /*else if (Integer.parseInt(list.get(position).mBath_towel) < Integer.parseInt(et_BathTowel.getText().toString())) {
//                                        Toast.makeText(ReceivedFromTrainActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
//                                    } */ else if (Integer.parseInt(list.get(position).mBlanket) < Integer.parseInt(et_Blanket.getText().toString())) {
//                                        Toast.makeText(ReceivedFromTrainActivity.this, "Invalid Value", Toast.LENGTH_SHORT).show();
//
//

                                    int bedSheetValue = safeParseInt(et_BedSheet.getText().toString());
                                    int unusedBedSheetValue = safeParseInt(et_unused_BedSheet.getText().toString());
                                    int pillowValue = safeParseInt(et_pillow.getText().toString());
                                    int unusedPillowValue = safeParseInt(et_unused_pillow.getText().toString());
                                    int pillowCoverValue = safeParseInt(et_pillowCover.getText().toString());
                                    int unusedPillowCoverValue = safeParseInt(et_unused_pillowCover.getText().toString());
                                    int blanketValue = safeParseInt(et_Blanket.getText().toString());
                                    int unusedBlanketValue = safeParseInt(et_unused_Blanket.getText().toString());
                                    int blanketCoverValue = safeParseInt(et_BlanketCover.getText().toString());
                                    int unusedBlanketCoverValue = safeParseInt(et_unused_BlanketCover.getText().toString());
                                    int faceTowelValue = safeParseInt(et_FaceTowel.getText().toString());
                                    int unusedFaceTowelValue = safeParseInt(et_unused_FaceTowel.getText().toString());

                                    // Retrieve list values safely
                                    int listBedSheetValue = safeParseInt(list.get(position).mBs);
                                    int listPillowValue = safeParseInt(list.get(position).mPillow);
                                    int listPillowCoverValue = safeParseInt(list.get(position).mPc);
                                    int listBlanketValue = safeParseInt(list.get(position).mBlanket);
                                    int listBlanketCoverValue = safeParseInt(list.get(position).mBlanket_cover);
                                    int listFaceTowelValue = safeParseInt(list.get(position).mFt);


                                    if (listBedSheetValue < bedSheetValue ||
                                            unusedBedSheetValue > subtractValues(listBedSheetValue, bedSheetValue)) {

                                        if (listBedSheetValue < bedSheetValue) {
                                            Toast.makeText(getApplicationContext(), "BedSheet value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused BedSheet value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (listPillowValue < pillowValue ||
                                            unusedPillowValue > subtractValues(listPillowValue, pillowValue)) {

                                        if (listPillowValue < pillowValue) {
                                            Toast.makeText(getApplicationContext(), "Pillow value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused Pillow value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (listPillowCoverValue < pillowCoverValue ||
                                            unusedPillowCoverValue > subtractValues(listPillowCoverValue, pillowCoverValue)) {

                                        if (listPillowCoverValue < pillowCoverValue) {
                                            Toast.makeText(getApplicationContext(), "Pillow Cover value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused Pillow Cover value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (listBlanketValue < blanketValue ||
                                            unusedBlanketValue > subtractValues(listBlanketValue, blanketValue)) {

                                        if (listBlanketValue < blanketValue) {
                                            Toast.makeText(getApplicationContext(), "Blanket value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused Blanket value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (listBlanketCoverValue < blanketCoverValue ||
                                            unusedBlanketCoverValue > subtractValues(listBlanketCoverValue, blanketCoverValue)) {

                                        if (listBlanketCoverValue < blanketCoverValue) {
                                            Toast.makeText(getApplicationContext(), "Blanket Cover value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused Blanket Cover value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (listFaceTowelValue < faceTowelValue ||
                                            unusedFaceTowelValue > subtractValues(listFaceTowelValue, faceTowelValue)) {

                                        if (listFaceTowelValue < faceTowelValue) {
                                            Toast.makeText(getApplicationContext(), "Hand Towel value is too large", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Unused Hand Towel value is too large", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        try {
                                            EditDataSave(
                                                    rowId,
                                                    laundryId,
                                                    mLaundry_name,
                                                    mDate,
                                                    et_pillow.getText().toString(),
                                                    et_BedSheet.getText().toString(),
                                                    et_pillowCover.getText().toString(),
                                                    et_FaceTowel.getText().toString(),
                                                    et_BlanketCover.getText().toString(),
//                                                    et_BathTowel.getText().toString(),//ok
                                                    et_Blanket.getText().toString(),//ok
                                                    et_remark.getText().toString(),

                                                    et_unused_BedSheet.getText().toString(),
                                                    et_unused_pillow.getText().toString(),
                                                    et_unused_pillowCover.getText().toString(),
                                                    et_unused_FaceTowel.getText().toString(),
                                                    et_unused_BlanketCover.getText().toString(),
                                                    et_unused_Blanket.getText().toString()


                                            );//ok


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                private int safeParseInt(String value) {
                                    if (value == null || value.trim().isEmpty()) {
                                        return 0; // Default value or handle as needed
                                    } else {
                                        try {
                                            return Integer.parseInt(value.trim());
                                        } catch (NumberFormatException e) {
                                            return 0; // Default value or handle as needed
                                        }
                                    }
                                }

                                private int subtractValues(int mbs, int bedsheet) {
                                    return mbs - bedsheet;
                                }

                            });
                        }
                        dialog.findViewById(R.id.v_negative).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    }

                    //                private void getLaundryType() {
//                    final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, LAUNDRY_API, null,
//                            new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
//                                        Log.e("response", response.toString());
//                                        JSONArray jsonArray = response.getJSONArray("laundry_data");
//                                        launddy_list.clear();
//                                        launddy_list.add(0, "Select Your Laundry");
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            JSONObject obj = jsonArray.getJSONObject(i);
//                                            // laundry_list.add(obj.getString("laundry_name"));
//                                            launddy_list.add(obj.getString("laundry_name"));
//                                            laundry_id_list.add(obj.getString("laundry_id"));
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    //  adapter_laundry.notifyDataSetChanged();
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Log.e("error", "" + error.toString());
//                                }
//                            });
//                    RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
//                    requestQueue.add(objectRequest);
//
//                }
                    private void setTextOrDefault(EditText editText, String value) {
                        editText.setText(value != null ? value : "");
                    }

                    private void EditDataSave(
                            String row_id,
                            String laundryId,
                            String laundryname,
                            String date,
                            String pillow,
                            String bs,
                            String pc,
                            String ft,
                            String blanket_cover,
//                            String bath_towel,
                            String blanket,
                            String remark,

                            String ubs,
                            String upillow,
                            String upillocover,
                            String ufacetowel,
                            String ublanketcover,
                            String ublanket
                    ) {

                        final JSONObject jsonObject = new JSONObject();
                        try {
                            Toast.makeText(ReceivedFromTrainActivity.this, "Laundry id " + laundryId + " " + row_id, Toast.LENGTH_SHORT).show();
                            jsonObject.put("row_id", row_id);
                            jsonObject.put("date", date);
                            jsonObject.put("laundry_id", laundryId); //yaha check karana dobara
                            jsonObject.put("depot_code", userDataModel.mUserItems.get(0).mDepot_code);

                            jsonObject.put("bs_return", bs);
                            jsonObject.put("pillow_return", pillow);

                            jsonObject.put("pc_return", pc);
                            jsonObject.put("ft_return", ft);

                            jsonObject.put("blanket_return", blanket);
                            jsonObject.put("blk_return", blanket);
                            jsonObject.put("bathtowel_return", "");
                            jsonObject.put("remark", remark);

                            jsonObject.put("bs_unused", ubs.isEmpty() ? "0" : ubs);
                            jsonObject.put("pillow_unused", upillow.isEmpty() ? "0" : upillow);
                            jsonObject.put("pc_unused", upillocover.isEmpty() ? "0" : upillocover);
                            jsonObject.put("blanket_unused", ublanket.isEmpty() ? "0" : ublanket);
                            jsonObject.put("blc_unused", ublanketcover.isEmpty() ? "0" : ublanketcover);
                            jsonObject.put("ht_unused", ufacetowel.isEmpty() ? "0" : ufacetowel);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String requestBody = jsonObject.toString();
                        Log.e("reqbody", requestBody);
                        showLoading("Please wait...");

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Update_API, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                hideLoading();
                                try {
                                    Log.e("responserespo", response);

                                    JSONObject jsonResponse = null;
                                    try {
                                        jsonResponse = new JSONObject(response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (jsonResponse != null && jsonResponse.has("message")) {
                                        String message = jsonResponse.getString("message");

                                        showConfirmationDialog(message, uiModeManager);
                                    } else {
                                        showConfirmationDialog(response, uiModeManager);
                                    }
                                    Type listType = new TypeToken<List<EditReceived>>() {
                                    }.getType();
                                    ArrayList<EditReceived> getList = new Gson().fromJson(response.toString(), listType);

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
                        RequestQueue requestQueue = Volley.newRequestQueue(ReceivedFromTrainActivity.this);
                        requestQueue.add(stringRequest);
                    }
                });

            }
            checkEmptyData();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public void filterList(ArrayList<SupplyList.TrainItem> filteredList) {
            list.clear();
            list.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void resetFilter() {
            list.clear();
            list.addAll(mListFull);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv, tv1, tv2, tv_verify;
        ImageView iv_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_serial_no);
            tv = (TextView) itemView.findViewById(R.id.tv_date);
            tv1 = (TextView) itemView.findViewById(R.id.tv_train_no);
            tv2 = (TextView) itemView.findViewById(R.id.tv_depots);
            tv_verify = (TextView) itemView.findViewById(R.id.tv_verify);
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

    public void showConfirmationDialog(String strMessage, UiModeManager uiModeManager) {
        final Dialog dialog = new Dialog(ReceivedFromTrainActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        TextView tvtitle = dialog.findViewById(R.id.confirmMessageTitle);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);

        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            tvtitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
            tvMessage.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.whiteTextColor));
        }


        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(ReceivedFromTrainActivity.this, ReceivedFromTrainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}
