package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.model.GetStockModel;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BedrollStocking extends AppCompatActivity {
    private final static String STOCKDATA_list = "http://lmsguwahati.projectrailway.in/api/getStockData";

    ImageView iv_add_supply;
    TextView tv_tittle, tv_empty_data;
    UserDataModel userdataModel;
    private BedrollStockAdapter bedrollStockAdapter;
    View linearLayoutManager,train_no_layout;
    private ArrayList<GetStockModel.stockDataItem> stockDataItemList = new ArrayList<>();


    RecyclerView recyclerView;
    String depot_code = "";
    final Calendar myCalendar = Calendar.getInstance();
    EditText et_dateFrom, et_dateTo;
    SwipeRefreshLayout srl;
    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroll_stocking);
        depot_code = getIntent().getStringExtra("deport_code");

        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        initializeViews();
//        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);
        if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES)
        {
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.whiteTextColor));
            linearLayoutManager.setBackgroundResource(R.drawable.shape_white);
            train_no_layout.setBackgroundResource(R.drawable.shape_white);

        }else{
            et_dateFrom.setHintTextColor(getResources().getColor(R.color.black));
            et_dateTo.setHintTextColor(getResources().getColor(R.color.black));
        }
        setupDatePickers(uiModeManager);
        setupRecyclerView();
        setupSwipeRefresh();

        GetLaundryList();
    }

    private void initializeViews() {
        et_dateFrom = findViewById(R.id.et_date_from);
        linearLayoutManager = findViewById(R.id.supOfficer_layout);
        train_no_layout = findViewById(R.id.train_no_layout);

        et_dateTo = findViewById(R.id.et_date_to);
        tv_tittle = findViewById(R.id.tv_tittle);
        tv_tittle.setText(userdataModel.mUserItems.get(0).mHeader);
        iv_add_supply = findViewById(R.id.iv_add_supply);
        tv_empty_data = findViewById(R.id.tv_empty_data);

        iv_add_supply.setOnClickListener(v -> {
            Intent intent = new Intent(BedrollStocking.this, BedrollStockingAddActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        findViewById(R.id.v_back).setOnClickListener(view -> onBackPressed());


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
            new DatePickerDialog(BedrollStocking.this, journeyDateFrom, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(BedrollStocking.this, journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.view2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void setupSwipeRefresh() {
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(BedrollStocking.this)) {
                GetLaundryList();
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

                ArrayList<GetStockModel.stockDataItem> filteredList = new ArrayList<>();

                for (GetStockModel.stockDataItem item : stockDataItemList) {
                    try {
                        Date itemDate = dateFormat.parse(item.mDate);
                        if (itemDate != null && !itemDate.before(fromDate) && !itemDate.after(toDate)) {
                            filteredList.add(item);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                bedrollStockAdapter.filterList(filteredList);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            bedrollStockAdapter.resetFilter();
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
                    Log.d("BedrollStocking", "Response: " + response); // Log the response
                    try {
                        GetStockModel getStockModel = new Gson().fromJson(response, GetStockModel.class);

                        // Update data list and notify adapter
                        stockDataItemList.clear();
                        stockDataItemList.addAll(getStockModel.mUpstockItem);

                        // Initialize adapter if not already initialized
                        if (bedrollStockAdapter == null) {
                            bedrollStockAdapter = new BedrollStockAdapter(stockDataItemList);
                            recyclerView.setAdapter(bedrollStockAdapter);
                        } else {
                            bedrollStockAdapter.notifyDataSetChanged();
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

    public class BedrollStockAdapter extends RecyclerView.Adapter<BedrollStockAdapter.ViewHolder> {
        private ArrayList<GetStockModel.stockDataItem> mList;
        private ArrayList<GetStockModel.stockDataItem> mListFull;

        public BedrollStockAdapter(ArrayList<GetStockModel.stockDataItem> mList) {
            this.mList = new ArrayList<>(mList);
            this.mListFull = new ArrayList<>(mList);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_bedroll_stocking, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GetStockModel.stockDataItem item = mList.get(position);

            holder.tv_index.setText(String.valueOf(position + 1));
            holder.tv1.setText(item.mDate);
//            holder.tv2.setText(item.mStore);
            holder.tv3.setText(item.mLaundry_id);

            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BedrollStocking.this, BedrollStockViewActivity.class);
                    intent.putExtra("id", item.mId);
                    startActivity(intent);
                }
            });
//            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showLogoutAlertDialog();
//                }
//
//
//            });
            checkEmptyData();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
        private void showLogoutAlertDialog() {
            final Dialog dialog = new Dialog(BedrollStocking.this, R.style.Dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //dialog.setCancelable(false);
            dialog.setContentView(R.layout.diolog_add_received);
            final ImageView iv_calender=dialog.findViewById(R.id.iv_calender);
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
                    DatePickerDialog dpd = new DatePickerDialog(BedrollStocking.this, journeyDate1, myCalendar
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

//            GetTrainType();
            et_select_train.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BedrollStocking.this);
                        // builder.setTitle("Select Train No...");
//                        builder.setItems(train_list.toArray(new CharSequence[train_list.size()]), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    et_select_train.setText(train_list.get(which));
//                                    dialog.dismiss();
//                                } catch (IndexOutOfBoundsException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });


//            GetCoachType();
            et_select_coach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BedrollStocking.this);
                        // builder.setTitle("Select Train No...");
//                        builder.setItems(coach_list.toArray(new CharSequence[coach_list.size()]), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    et_select_coach.setText(coach_list.get(which));
//                                    dialog.dismiss();
//                                } catch (IndexOutOfBoundsException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
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
                        Toast.makeText(BedrollStocking.this, "Select Date",
                                Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(et_select_train.getText().toString())) {
                        Toast.makeText(BedrollStocking.this, "Select Train",
                                Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(et_select_coach.getText().toString())) {
                        Toast.makeText(BedrollStocking.this, "Select Coach",
                                Toast.LENGTH_LONG).show();

                    } else {
//                        try {
//                            String train_id = train_id_list.get(train_list.indexOf(et_select_train.getText().toString()));
//                            SaveReceivedPackage(dialog,et_date.getText().toString(),train_id,
//                                    et_select_coach.getText().toString(),
//                                    et_no_of_bag.getText().toString(),
//                                    et_bed_sheet.getText().toString(),
//                                    et_pillow_cover.getText().toString()
//                                    ,et_face_towel.getText().toString(),
//                                    et_blanket_cover.getText().toString(),
//                                    et_bath_towel.getText().toString(),
//                                    et_blanket.getText().toString(),
//                                    et_total_packet.getText().toString(),
//                                    et_unused_packet.getText().toString(),
//                                    et_remark.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

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
        public void filterList(ArrayList<GetStockModel.stockDataItem> filteredList) {
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
            TextView tv_index, tv1, tv2, tv3;
            ImageView iv_view,ivEdit;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index = itemView.findViewById(R.id.tv_index_number);
                tv1 = itemView.findViewById(R.id.tv_date);
                tv2 = itemView.findViewById(R.id.tv_store);
                tv3 = itemView.findViewById(R.id.tv_laundry);
                iv_view = itemView.findViewById(R.id.iv_view);
//                ivEdit = itemView.findViewById(R.id.iv_editt);
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
