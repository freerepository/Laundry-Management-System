package com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.Depot_PanaltyAddScreen;
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.PanaltyAdapter;
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.PenaltyViewModelClass;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LaundryPenltryScreenActivity extends AppCompatActivity {
//    private final static String STOCKDATA_list = "http://lmsguwahati.projectrailway.in/api/getmissingPenaltyData";
    private final static String STOCKDATA_list = "http://lmsguwahati.projectrailway.in/api/getPenaltyData";

    private ImageView v_add_buffer;
    RecyclerView recyclerView;
    String depot_code = "";
    final Calendar myCalendar = Calendar.getInstance();
    EditText et_dateFrom, et_dateTo;
    TextView tv_empty_data;
    SwipeRefreshLayout srl;
    UiModeManager uiModeManager;
    UserDataModel userdataModel;
    PanaltyAdapter2 adapter;
    private ArrayList<PenaltyViewModelClass.PenaltyItem> stockDataItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_penltry_screen);
        depot_code = getIntent().getStringExtra("deport_code");

        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES)
        {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
//            linearLayoutManager.setBackgroundResource(R.drawable.shape_white);
//            train_no_layout.setBackgroundResource(R.drawable.shape_white);

        }else{
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }

        setupDatePickers(uiModeManager);
        setupRecyclerView();
        setupSwipeRefresh();
        GetLaundryList();

    }

//    private void getTeproryData() {
////        adapter = new PanaltyAdapter()
//        arrayList = new ArrayList<>();
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        arrayList.add(new DepotPanltyModel("abc","23","23","view"));
//        adapter = new PanaltyAdapter(arrayList);
//        recyclerView.setAdapter(adapter);  // **This line is essential**
//        adapter.notifyDataSetChanged();
//
//    }

    private void initializeViews() {
        v_add_buffer = findViewById(R.id.v_add_buffer);
        v_add_buffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LaundryPenltyAddScreenActivity.class));
            }
        });

        tv_empty_data = findViewById(R.id.tv_empty_data);
        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);

//        tv_tittle = findViewById(R.id.tv_tittle);
//        tv_tittle.setText(userdataModel.mUserItems.get(0).mHeader);
//        iv_add_supply = findViewById(R.id.iv_add_supply);
//        tv_empty_data = findViewById(R.id.tv_empty_data);
//
//        iv_add_supply.setOnClickListener(v -> {
//            Intent intent = new Intent(BedrollStocking.this, BedrollStockingAddActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        });

        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());


        et_dateFrom.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterPenaltyItem();

            }
        });
        et_dateTo.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterPenaltyItem();

            }
        });
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.panltyListRecyvlerView);
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
            new DatePickerDialog(LaundryPenltryScreenActivity.this, journeyDateFrom, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(LaundryPenltryScreenActivity.this, journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }
    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(LaundryPenltryScreenActivity.this)) {
//                GetLaundryList();
            } else {
                srl.setRefreshing(false);
            }
        });
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

    private void filterPenaltyItem() {
        String fromDateString = et_dateFrom.getText().toString();
        String toDateString = et_dateTo.getText().toString();

        if (!TextUtils.isEmpty(fromDateString) && !TextUtils.isEmpty(toDateString)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fromDate = dateFormat.parse(fromDateString);
                Date toDate = dateFormat.parse(toDateString);

                ArrayList<PenaltyViewModelClass.PenaltyItem> filteredList = new ArrayList<>();

                for (PenaltyViewModelClass.PenaltyItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.penaltyDate);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (adapter != null){
                    adapter.filterList(filteredList);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (adapter!=null){
                adapter.resetFilter();
            }
        }
    }

    private void GetLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
            srl.setRefreshing(true); // Start refreshing animation
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, STOCKDATA_list,
                response -> {
                    Log.d("penaltyStocking", "Response: " + response); // Log the response
                    try {
                        PenaltyViewModelClass penaltyViewModelClass = new Gson().fromJson(response, PenaltyViewModelClass.class);

                        // Update data list and notify adapter
                        stockDataItemList.clear();
                        stockDataItemList.addAll(penaltyViewModelClass.penaltyData);

                        // Initialize adapter if not already initialized
                        if (adapter == null) {
                            adapter = new PanaltyAdapter2(stockDataItemList,getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        // Check and update visibility based on data availability
                        checkEmptyData();
                    } catch (Exception e) {
                        Log.e("LaundryPenalty", "Parsing error: " + e.getMessage(), e);
                    } finally {
                        srl.setRefreshing(false); // Stop refreshing animation
                    }
                },
                error -> {
                    Log.e("LaundryPenalty", "Error Response: " + error.getMessage(), error);
                    srl.setRefreshing(false); // Stop refreshing animation
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
                    Log.e("LaundryPenalty", "Encoding error: " + uee.getMessage(), uee);
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

}