package com.example.laundrymanagementghy.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.FragmentModel.FragmentModel3;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturntoLaundrytFromBufferStockActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ThreeFragment extends Fragment {

    private final static String BEDROLL_API = "http://lmskyq.projectrailway.in/api/LaundrybalanceSummary";
    private final static String GET_DEPOT_TYPE = "http://lmskyq.projectrailway.in/Api/get_depots";

    EditText et_dateFrom, et_dateTo;
    Spinner sp_depot;
    ArrayList<String> depot_list = new ArrayList<>();
    ArrayAdapter<String> adapter_depot;
    TextView tv_tittle, tv_empty_data;
    String depot_code = "";
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    AlertDialog dialog;
    ThreeFragment.BedrollStockAdapter bedrollStockAdapter;
    private ArrayList<FragmentModel3.mStoreItem> stockDataItemList = new ArrayList<FragmentModel3.mStoreItem>();
    UserDataModel userdataModel;
    String selectedDepot = "",laundryId;
    Context context = getActivity();
    public  static  ThreeFragment newInstance(String id){
        ThreeFragment threeFragment = new ThreeFragment();
        Bundle args = new Bundle();
        args.putString("id",id);
        threeFragment.setArguments(args);
        return threeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        if (getArguments()!=null){
            laundryId = getArguments().getString("id");
        }


        try {
            userdataModel = new Gson().fromJson(O.getPreference(context, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = view.findViewById(R.id.view2);
        srl = view.findViewById(R.id.srl);
        tv_empty_data = view.findViewById(R.id.tv_empty_data);


        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetLaundryList(laundryId);
            }
        });


        GetLaundryList(laundryId);

        return view;
    }
    public class BedrollStockAdapter extends RecyclerView.Adapter<ThreeFragment.BedrollStockAdapter.ViewHolder> {
        private ArrayList<FragmentModel3.mStoreItem> mList;

        public BedrollStockAdapter(ArrayList<FragmentModel3.mStoreItem> stockDataItemList) {
            this.mList = stockDataItemList;
        }

        @NonNull
        @Override
        public ThreeFragment.BedrollStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_model3, parent, false);
            return new ThreeFragment.BedrollStockAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ThreeFragment.BedrollStockAdapter.ViewHolder holder, int position) {
            FragmentModel3.mStoreItem item = mList.get(position);
            holder.tvIndex.setText(String.valueOf(position + 1));
            holder.tvItem.setText(item.itemName);
            holder.tvIssused.setText(item.issuedToDepotTrain);
            holder.tvFresh.setText(String.valueOf(item.freshWashedAvailable));///////////////////////////////////////////////////
            holder.tvSolid.setText(item.soiledAvailable);


//            holder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), BedrollReturntViewStockActivity.class);
//                intent.putExtra("id", item.mId);
//                startActivity(intent);
//            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
              TextView tvIndex, tvItem, tvIssused, tvFresh, tvSolid;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvIndex = itemView.findViewById(R.id.tv_index_number);
                tvItem = itemView.findViewById(R.id.tv_itemName);
                tvIssused = itemView.findViewById(R.id.tvIssudedDepotTrain);
                tvFresh = itemView.findViewById(R.id.tvFreshWash);
                tvSolid = itemView.findViewById(R.id.tvSolidAvailble);
            }
        }
    }

    private void setupSwipeRefresh() {
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(requireContext())) { // Change context to requireContext()
                GetLaundryList(laundryId);
            } else {
                srl.setRefreshing(false);
            }
        });
    }

    private void GetLaundryList(String laundryId) {
        Context context = requireContext();
        RequestQueue requestQueue = Volley.newRequestQueue(context); //1 yaha request queue bana li
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
                    Log.d("ThirdFragmentResponse", "Response: " + response); // Log the response

                    try {
                        FragmentModel3 fragmentModel = new Gson().fromJson(response, FragmentModel3.class);
                        stockDataItemList.clear();
                        stockDataItemList.addAll(fragmentModel.mList);

                        // Notify the adapter after updating the data list
//                        bedrollStockAdapter.notifyDataSetChanged();

                        if (bedrollStockAdapter == null) {
                            bedrollStockAdapter = new ThreeFragment.BedrollStockAdapter(stockDataItemList);
                            recyclerView.setAdapter(bedrollStockAdapter);
                        } else {
                            bedrollStockAdapter.notifyDataSetChanged();
                        }

                        // Check and update visibility based on data availability
//                            checkEmptyData();


                        checkEmptyData();
                    } catch (Exception e) {
                        Log.e("BedrollStocking", "Parsing error: " + e.getMessage(), e);
                        srl.setRefreshing(false); // Start refreshing animation
                    } finally {
                        srl.setRefreshing(false); // Start refreshing animation
                    }
                },
                error -> {
                    Log.e("BedrollStocking", "Error: " + error.getMessage());
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

    private void checkEmptyData() {///////////////////////////////////////////////////////////////
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

}