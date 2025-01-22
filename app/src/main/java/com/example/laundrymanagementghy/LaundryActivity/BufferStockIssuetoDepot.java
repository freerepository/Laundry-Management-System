package com.example.laundrymanagementghy.LaundryActivity;

import android.app.DatePickerDialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.GetBufferIssueList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
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

public class BufferStockIssuetoDepot extends AppCompatActivity {
    private final static String get_Buffer_list_API = "http://lmskyq.projectrailway.in/api/getbufferStockData";
    ImageView v_add_buffer;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    EditText et_dateFrom, et_dateTo;
    SwipeRefreshLayout srl;
    final Calendar myCalendar = Calendar.getInstance();
    String  depot_code="";
    AlertDialog dialog;
    TestAapter testAapter;
    private ArrayList<GetBufferIssueList.mItem> stockDataItemList = new ArrayList<>();

    UserDataModel userdataModel;
    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_stock_issue_depot);
        depot_code = getIntent().getStringExtra("deport_code");
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();
        uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES)
        {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));

        }else{
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }
        setupDatePickers(uiModeManager);
        setupRecyclerView();
        setupSwipeRefresh();

        GetBufferList();
    }
    private void initializeViews() {
        et_dateFrom = findViewById(R.id.et_date_from);
        et_dateTo = findViewById(R.id.et_date_to);

        v_add_buffer = findViewById(R.id.iv_add_supply);
        tv_empty_data = findViewById(R.id.tv_empty_data);

        v_add_buffer = findViewById(R.id.v_add_buffer);
        v_add_buffer.findViewById(R.id.v_add_buffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BufferStockIssuetoDepot.this, BufferStockIssueAddDepot.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());

        et_dateFrom.addTextChangedListener(new BufferStockIssuetoDepot.TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });

        et_dateTo.addTextChangedListener(new BufferStockIssuetoDepot.TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                filterBedrollStock();
            }
        });
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
            new DatePickerDialog(BufferStockIssuetoDepot.this, journeyDateFrom, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(BufferStockIssuetoDepot.this, journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.buffer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(BufferStockIssuetoDepot.this)) {
                GetBufferList();
            } else {
                srl.setRefreshing(false);
            }
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

                ArrayList<GetBufferIssueList.mItem> filteredList = new ArrayList<>();

                for (GetBufferIssueList.mItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mSubmission_date);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (testAapter!=null){
                    testAapter.filterList(filteredList);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (testAapter!=null){
                testAapter.resetFilter();
            }
        }
    }

    private void GetBufferList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
            srl.setRefreshing(true); // Start refreshing animation
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_Buffer_list_API,
                response -> {
                    Log.d("BedrollStocking", "Response: " + response); // Log the response
                    try {
                        GetBufferIssueList getStockModel = new Gson().fromJson(response, GetBufferIssueList.class);

                        // Update data list and notify adapter
                        stockDataItemList.clear();
                        stockDataItemList.addAll(getStockModel.mList);

                        // Initialize adapter if not already initialized
                        if (testAapter == null) {
                            testAapter = new TestAapter(stockDataItemList);
                            recyclerView.setAdapter(testAapter);
                        } else {
                            testAapter.notifyDataSetChanged();
                        }

                        // Check and update visibility based on data availability
                        checkEmptyData();
                    } catch (Exception e) {
                        Log.e("BedrollStocking", "Parsing error: " + e.getMessage(), e);
                    } finally {
                        srl.setRefreshing(false); // Stop refreshing animation
                    }
                },
                error -> {
                    Log.e("BedrollStocking", "Error Response: " + error.getMessage(), error);
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
                    Log.e("BedrollStocking", "Encoding error: " + uee.getMessage(), uee);
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

    public class TestAapter extends RecyclerView.Adapter<TestAapter.ViewHolder> {
        private ArrayList<GetBufferIssueList.mItem> mList;
        private ArrayList<GetBufferIssueList.mItem> mListFull;

        public TestAapter(ArrayList<GetBufferIssueList.mItem> mList) {
            this.mList = new ArrayList<>(mList);
            this.mListFull = new ArrayList<>(mList);
        }

        @NonNull
        @Override
        public TestAapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buffer_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TestAapter.ViewHolder holder, int position) {
            GetBufferIssueList.mItem item = mList.get(position);

            holder.tv_index.setText(String.valueOf(position + 1));
            holder.tv1.setText(mList.get(position).mSubmission_date);
            holder.tv2.setText(mList.get(position).mDepotCode);
            holder.tv3.setText(mList.get(position).mReason);
            holder.tv4.setText(mList.get(position).mBed_sheet);
            holder.tv5.setText(mList.get(position).mPillow);
            holder.tv6.setText(mList.get(position).mPillow_cover);
            holder.tv7.setText(mList.get(position).mBlanket);
            holder.tv8.setText(mList.get(position).mBlanket_cover);
            holder.tv9.setText(mList.get(position).mHand_towel);
            holder.tv_status.setText(mList.get(position).mStatus);


//            holder.iv_view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(BedrollStocking.this, BedrollStockViewActivity.class);
//                    intent.putExtra("id", item.mId);
//                    startActivity(intent);
//                }
//            });

            checkEmptyData();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void filterList(ArrayList<GetBufferIssueList.mItem> filteredList) {
            mList.clear();
            mList.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void resetFilter() {
            mList.clear();
            mList.addAll(mListFull);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index,tv1, tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv_status;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = (TextView) itemView.findViewById(R.id.tv_index);
                tv1 = (TextView) itemView.findViewById(R.id.tv_date);
                tv2 = (TextView) itemView.findViewById(R.id.tv_depot);
                tv3 = (TextView) itemView.findViewById(R.id.tv_reason);
                tv4 = (TextView) itemView.findViewById(R.id.tv_bed_sheet);
                tv5 = (TextView) itemView.findViewById(R.id.tv_pillow);
                tv6 = (TextView) itemView.findViewById(R.id.tv_pillow_cover);
                tv7 = (TextView) itemView.findViewById(R.id.tv_blanket);
                tv8 = (TextView) itemView.findViewById(R.id.tv_blanket_cover);
                tv9 = (TextView) itemView.findViewById(R.id.tv_hand_towel);
                tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            }
        }
    }

    // Helper class to simplify TextWatcher implementation
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
}