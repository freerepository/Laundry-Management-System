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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.laundrymanagementghy.Activity.FragmentModel.FragmentModel;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.BedrollReturntoLaundrytFromBufferStockActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class FirstFragment extends Fragment {
    private final static String BEDROLL_API = "http://lmskyq.projectrailway.in/api/LaundryStockSummary";
//    private final static String BEDROLL_API = "http://lmsguwahati.projectrailway.in/api/LaundryStockSummary";

    TextView tv_empty_data;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    AlertDialog dialog;
    BedrollStockAdapter bedrollStockAdapter;
    private ArrayList<FragmentModel.mStoreItem> stockDataItemList = new ArrayList<FragmentModel.mStoreItem>();
    UserDataModel userdataModel;
    Context context = getActivity();
    String laundryid;

    public static FirstFragment newInstance(String id) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        return  fragment;
    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup itemView, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_first, itemView, false);
            if (getArguments() != null) {
                laundryid = getArguments().getString("id");
            }

        try {
            userdataModel = new Gson().fromJson(O.getPreference(context, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = view.findViewById(R.id.recylerview2);
        tv_empty_data = view.findViewById(R.id.tv_empty_data);
        srl = view.findViewById(R.id.swirpeRL);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

//        Toast.makeText(context, "Laundry id is "+userItem.mLaundryID, Toast.LENGTH_SHORT).show();
        setupSwipeRefresh();

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetLaundryList(laundryid);
            }
        });

        GetLaundryList(laundryid);

        return view;
    }

    public class BedrollStockAdapter extends RecyclerView.Adapter<BedrollStockAdapter.ViewHolder> {
        private ArrayList<FragmentModel.mStoreItem> mList;

        public BedrollStockAdapter(ArrayList<FragmentModel.mStoreItem> stockDataItemList) {
            this.mList = stockDataItemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_model, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FragmentModel.mStoreItem item = mList.get(position);
            holder.tvIndex.setText(String.valueOf(position + 1));
            holder.tvItem.setText(item.mItemName);
            holder.tvQRF.setText(item.mQuantityReceivedFromDepotStore);
            holder.tvIstolen.setText(item.mLostStolen);
            holder.tv_condemned.setText(item.mTotalCondemned);
            holder.tv_BFTD.setText(item.mTotalBufferProvidedToDepot);
            holder.tv_INWL.setText(item.mTotalInHand);



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
            TextView tvIndex, tvItem, tvQRF, tvIstolen, tv_condemned, tv_BFTD,tv_INWL;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                tvIndex = itemView.findViewById(R.id.tv_index_number);
                tvItem = itemView.findViewById(R.id.tv_item);
                tvQRF = itemView.findViewById(R.id.tv_qrf);
                tvIstolen = itemView.findViewById(R.id.tv_lostStolen);
                tv_condemned = itemView.findViewById(R.id.tv_condemned);
                tv_BFTD = itemView.findViewById(R.id.tv_BFTD);
                tv_INWL = itemView.findViewById(R.id.tv_INWL);
            }
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
                    Log.d("FirstFragmentResponse", "Response: " + response); // Log the response

                    try {
                        FragmentModel fragmentModel = new Gson().fromJson(response, FragmentModel.class);
                        stockDataItemList.clear();
                        stockDataItemList.addAll(fragmentModel.mList);

                        // Notify the adapter after updating the data list
//                        bedrollStockAdapter.notifyDataSetChanged();

                        if (bedrollStockAdapter == null) {
                            bedrollStockAdapter = new BedrollStockAdapter(stockDataItemList);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(ll);
        dialog = builder.create();
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
        dialog.dismiss();
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