package com.example.laundrymanagementghy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.FragmentModel.FragmentModel;
import com.example.laundrymanagementghy.Activity.FragmentModel.FragmentModel2;
import com.example.laundrymanagementghy.Activity.FragmentModel.FragmentModel2Deserializer;
import com.example.laundrymanagementghy.Amodel.SupplyList;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.Bmodel.GetCondemendList;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturntoLaundrytFromBufferStockActivity;
import com.example.laundrymanagementghy.DeportActivity.ReceivedFromTrainActivity;
import com.example.laundrymanagementghy.LaundryActivity.BufferStockIssuetoDepot;
import com.example.laundrymanagementghy.R;

import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SecondFragment extends Fragment {

    private final static String BEDROLL_API = "http://lmsguwahati.projectrailway.in/api/clean_bedroll_supply_summary";
    private final static String GET_DEPOT_TYPE = "http://lmsguwahati.projectrailway.in/Api/get_depots";

    EditText et_dateFrom, et_dateTo;
    Spinner sp_depot;
    //    Button filterButton;
    ArrayList<String> depot_list = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayAdapter<String> adapter_depot;
    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    AlertDialog dialog;
    SecondFragmentAdapter secondFragmentAdapter;
    private ArrayList<FragmentModel2.mStoreItem> stockDataItemList = new ArrayList<>();
    UserDataModel userdataModel;
    String selectedDepot = "", laundryid;
    Context context = getActivity();
    ImageView calFrom, calTo;


    private boolean isSelectedSpinner = false;

    public static SecondFragment newInstance(String id) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        if (getArguments() != null) {
            laundryid = getArguments().getString("id");
        }

        try {
            userdataModel = new Gson().fromJson(O.getPreference(context, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        srl = view.findViewById(R.id.srl);
        sp_depot = view.findViewById(R.id.sp_depot);
//        filterButton = view.findViewById(R.id.filterButton);
        et_dateFrom = view.findViewById(R.id.et_datefrom_);
        tv_empty_data = view.findViewById(R.id.tv_empty_data);
        et_dateTo = view.findViewById(R.id.et_dateto_);
        calFrom = view.findViewById(R.id.ic_calenderfrom_);
        calTo = view.findViewById(R.id.ic_calenderto_);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetLaundryList(laundryid);

            }
        });
        setupRecyclerView(view);
        GetLaundryList(laundryid);
        GetDepotType();

        depot_list.add(0, "Select Depot...");
        adapter_depot = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, depot_list);
        adapter_depot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_depot.setAdapter(adapter_depot);

        setupDatePickers();
        et_dateFrom.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String laundryId = laundryid;
                String fromDate = et_dateFrom.getText().toString();
                String toDate = et_dateTo.getText().toString();
                String depotCode = selectedDepot;
                GetSecondAPIData(laundryId, fromDate, toDate, depotCode);
            }
        });

        et_dateTo.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String laundryId = laundryid;
                String fromDate = et_dateFrom.getText().toString();
                String toDate = et_dateTo.getText().toString();
                String depotCode = selectedDepot;
                GetSecondAPIData(laundryId, fromDate, toDate, depotCode);
            }
        });
//        Toast.makeText(requireContext(), "pos : " + sp_depot.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
        sp_depot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "Select Depot";
                    srl.setRefreshing(false);
                } else {
                    selectedDepot = depot_list.get(i);
                    String laundryId = laundryid;
                    String fromDate = et_dateFrom.getText().toString();
                    String toDate = et_dateTo.getText().toString();
                    GetSecondAPIData(laundryId, fromDate, toDate, selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private void GetSecondAPIData(String laundryId, String fromDate, String toDate, String depotCode) {

        if (!depotCode.equals("Select Depot")) {

            Context context = requireContext();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String url = BEDROLL_API; // Yaha pe apna second API ka URL daalein

            // JSON body prepare karein
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("laundry_id", laundryId);
                jsonObject.put("from_date", fromDate);
                jsonObject.put("to_date", toDate);
                jsonObject.put("depot_code", depotCode);
                srl.setRefreshing(true); // Start refreshing animation
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String requestBody = jsonObject.toString();

            // POST Request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                Log.d("re", response.toString());

                try {
                    FragmentModel2 fragmentModel2 = new Gson().fromJson(response, FragmentModel2.class);

                    // "Data Not Found" ka message check karte hue
                    if (fragmentModel2.mUserItems == null || fragmentModel2.mUserItems.isEmpty()) {
                        // Agar message field mein "Data Not Found" message ho, toh woh show karte hain
                        if (fragmentModel2.message != null && fragmentModel2.message.equals("Data Not Found")) {
                            Toast.makeText(context, fragmentModel2.message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Data Not Found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Agar data milta hai, toh list ko update karte hain

                        recyclerView.setVisibility(View.VISIBLE);
                        tv_empty_data.setVisibility(View.GONE);


                        stockDataItemList.clear();
                        stockDataItemList.addAll(fragmentModel2.mUserItems);

                        if (secondFragmentAdapter == null) {
                            secondFragmentAdapter = new SecondFragmentAdapter(stockDataItemList);
                            recyclerView.setAdapter(secondFragmentAdapter);
                        } else {
                            secondFragmentAdapter.updateData(fragmentModel2.mUserItems);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    recyclerView.setVisibility(View.GONE);
                    tv_empty_data.setVisibility(View.VISIBLE);
                } finally {
                    srl.setRefreshing(false);
                }



//                try {
//                    FragmentModel2 fragmentModel2 = new Gson().fromJson(response, FragmentModel2.class);
//                    stockDataItemList.clear();
//                    stockDataItemList.addAll(fragmentModel2.mUserItems);
//
//                    if (secondFragmentAdapter == null) {
//                        secondFragmentAdapter = new SecondFragment.SecondFragmentAdapter(stockDataItemList);
//                        recyclerView.setAdapter(secondFragmentAdapter);
//                    } else {
//                        secondFragmentAdapter.updateData(fragmentModel2.mUserItems);
//                    }
//                } catch (Exception e) {
//                    Log.e("SecondAPIParsingError", "Parsing error: " + e.getMessage(), e);
//                } finally {
//                    srl.setRefreshing(false); // Stop refreshing animation
//                }


            },
                    error -> {
                        Log.e("SecondAPIError", "Error: " + error.getMessage());
                        srl.setRefreshing(false);
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
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public class SecondFragmentAdapter extends RecyclerView.Adapter<SecondFragmentAdapter.ViewHolder> {
        private List<FragmentModel2.mStoreItem> mList;
        private List<FragmentModel2.mStoreItem> mListFull; // Backup list for filtering

        public SecondFragmentAdapter(List<FragmentModel2.mStoreItem> list) {
            if (list != null) {
                this.mList = new ArrayList<>(list);
                this.mListFull = new ArrayList<>(list);
            } else {
                this.mList = new ArrayList<>();
                this.mListFull = new ArrayList<>();
                Log.w("BedrollStockAdapter", "Initialized with null list. Using empty lists.");
            }
        }

        @NonNull
        @Override
        public SecondFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_model2, parent, false);
            return new SecondFragmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SecondFragmentAdapter.ViewHolder holder, int position) {
            FragmentModel2.mStoreItem item = mList.get(position);
            holder.tvIndex.setText(String.valueOf(position + 1));
            holder.tvTrainNumber.setText(item.trainNo);
            holder.tvDate.setText(item.supplyDate);
            holder.tvBedSheet.setText(item.bedSheet);
            holder.tvPillowCover.setText(item.pillowCover);
            holder.tvHandTowel.setText(item.faceTowel);
            holder.tvBlanket.setText(item.noBlanket);
            holder.tvBlanketCover.setText(item.blanketCover);

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        public void updateData(List<FragmentModel2.mStoreItem> newList) {
            mList.clear();
            mList.addAll(newList);
//            Toast.makeText(requireContext(), "New list " + newList.size(), Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            recyclerView.invalidate();
            recyclerView.requestLayout();

            if (mList.isEmpty()) {
                // Show empty view
//                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Hide empty view
//                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        public void filterList(ArrayList<FragmentModel2.mStoreItem> filteredList) {
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
            TextView tvIndex, tvTrainNumber, tvDate, tvBedSheet, tvPillowCover, tvHandTowel, tvBlanket, tvBlanketCover;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvIndex = itemView.findViewById(R.id.tv_index_number);
                tvTrainNumber = itemView.findViewById(R.id.tv_train_no);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvBedSheet = itemView.findViewById(R.id.tv_bed_sheet);
                tvPillowCover = itemView.findViewById(R.id.tv_pillow_cover);
                tvHandTowel = itemView.findViewById(R.id.tv_hand_towel);
                tvBlanket = itemView.findViewById(R.id.tv_blanket);
                tvBlanketCover = itemView.findViewById(R.id.tv_blanket_cover);
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

    private void setupSwipeRefresh() {
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(requireContext())) { // Change context to requireContext()
                GetLaundryList(laundryid);
            } else {
                srl.setRefreshing(false);
            }
        });
    }

    private void GetLaundryList(String laundryId) {
        Context context = requireContext();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = BEDROLL_API;

        // Create JSON object to send laundry_id
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", laundryId);
            srl.setRefreshing(true); // Start refreshing animation
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();

        // POST Request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("secondFragmentResponse", "Response: " + response); // Log the response


                    try {
                        FragmentModel2 fragmentModel2 = new Gson().fromJson(response, FragmentModel2.class);

                        // Clear previous data and add new data
                        stockDataItemList.clear();
                        stockDataItemList.addAll(fragmentModel2.mUserItems);

                        // Log the received data for debugging
//                        for (int i = 0; i < stockDataItemList.size(); i++) {
//                            Log.d("ResponseData", stockDataItemList.get(i)());
//                        }

                        // Initialize the adapter if it's null
                        if (secondFragmentAdapter == null) {
                            secondFragmentAdapter = new SecondFragment.SecondFragmentAdapter(stockDataItemList);
                            recyclerView.setAdapter(secondFragmentAdapter);
                        } else {
                            // Notify the adapter of the data change
                            secondFragmentAdapter.notifyDataSetChanged();
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
                    Log.e("cleBedrollStocking", "Error: " + error.getMessage());
                    srl.setRefreshing(false);
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
                    return null;
                }
            }
        };

        requestQueue.add(stringRequest);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void showLoading(@NonNull String message0) {
        LinearLayout ll = new LinearLayout(requireContext());
        ll.setPadding(16, 16, 16, 16);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(llParam);

        TextView tv = new TextView(requireContext());
        tv.setText(message0);
        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(llParam);
        tv.setPadding(8, 8, 8, 8);
        ll.addView(tv);

        RelativeLayout rl = new RelativeLayout(requireContext());
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(rlParam);

        ImageView iv = new ImageView(requireContext());
        iv.setImageDrawable(getResources().getDrawable(R.drawable.progress));
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv.setLayoutParams(rlParam);
        rl.addView(iv);
        iv.animate().setInterpolator(new DecelerateInterpolator()).rotation(-3600).setDuration(20000).start();

        ImageView iv_logo = new ImageView(requireContext());
        iv_logo.setImageDrawable(getResources().getDrawable(R.mipmap.logo));
        iv_logo.setPadding(20, 20, 20, 20);
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv_logo.setLayoutParams(rlParam);
        rl.addView(iv_logo);
        iv_logo.animate().setInterpolator(new DecelerateInterpolator()).rotation(3600).setDuration(20000).start();

        ll.addView(rl);

        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(ll);
            dialog = builder.create();
            dialog.show();
        }
        dialog.show();


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

    protected void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(requireContext());
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
                Intent intent = new Intent(requireContext(), BedrollReturntoLaundrytFromBufferStockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(context, "Toast is not called update here", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });
        dialog.show();
    }

    private void setupDatePickers() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        final DatePickerDialog.OnDateSetListener journeyDateFrom = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateFrom.setText(dateFormat.format(calendar.getTime()));
        };

        final DatePickerDialog.OnDateSetListener journeyDateTo = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            et_dateTo.setText(dateFormat.format(calendar.getTime()));
        };

//        et_dateFrom.setOnClickListener(v -> {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_YEAR, -1);
//            DatePickerDialog dpd = new DatePickerDialog(requireContext(), journeyDateFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//            dpd.getDatePicker().setMaxDate(new Date().getTime());
//            dpd.show();
//        });

        et_dateFrom.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long yesterdayInMillis = calendar.getTimeInMillis();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), journeyDateFrom,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(yesterdayInMillis);
            dpd.show();
        });


        et_dateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), journeyDateTo,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//            dpd.getDatePicker().setMaxDate(new Date().getTime());
            dpd.show();
        });
    }

    private void GetDepotType() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
            jsonObject.put("depot_code", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GET_DEPOT_TYPE, jsonObject,
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, depot_list);
                        sp_depot.setAdapter(adapter);
                        sp_depot.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading(); // Define this method in your fragment if required
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(objectRequest);
    }
}