package com.example.laundrymanagementghy.LaundryActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.LaundryActivity.Updating.FreshBedRollUpdateActivty;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.model.GetFreshSupplyList;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FreshBedrollSupplytoDepot extends AppCompatActivity {
    private final static String fresh_laundry_list = "http://lmskyq.projectrailway.in/Api/depot_received_from_laundry";


    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    TextView tv_empty_data;
    ImageView iv_add_supply;
    String deport_code = "";
    AlertDialog dialog;
    FreshAdapter adapter;
    UserDataModel userdataModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fresh_bedroll_supply_depot);
        deport_code = getIntent().getStringExtra("deport_code");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_add_supply = findViewById(R.id.iv_add_supply);

        findViewById(R.id.v_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_add_supply.setOnClickListener(v -> {
            Intent intent = new Intent(FreshBedrollSupplytoDepot.this, FreshBedrollSupplytoAddDepot.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });





        recyclerView = (RecyclerView) findViewById(R.id.fresh_recyclerview);
        tv_empty_data = findViewById(R.id.tv_empty_data);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FreshAdapter(new ArrayList<>(),getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(FreshBedrollSupplytoDepot.this)) {
                FreshLaundryList();
            } else {
                srl.setRefreshing(false);
            }
        });




        FreshLaundryList();



    }


    private void FreshLaundryList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fresh_laundry_list,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        srl.setRefreshing(false);
                        Log.d("response_req", response);
                        try {
                            GetFreshSupplyList receivedList = new Gson().fromJson(response.toString(), GetFreshSupplyList.class);
                            recyclerView.setAdapter(new FreshAdapter(receivedList.mSupplyList,getApplicationContext()));
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

    public class FreshAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<GetFreshSupplyList.mGetItem> list;
        private ArrayList<GetFreshSupplyList.mGetItem> mListFull;
        private Context context;

        public FreshAdapter(ArrayList<GetFreshSupplyList.mGetItem> list, Context context) {
            this.list = list;
            this.mListFull = mListFull;
            this.context = context;
        }

        @NonNull
        @Override
        public FreshBedrollSupplytoDepot.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.row_fresh_supply_list, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
            final int position = pos;
            holder.tv_index.setText((position + 1) + "");
            holder.tv1.setText(list.get(position).mSupply_date);
            holder.tv2.setText(list.get(position).mDepot_code);
            holder.tv3.setText(list.get(position).mPacket_count);
            holder.tv4.setText(list.get(position).mTrain_no);
            holder.tv5.setText(list.get(position).mCoach);


            String status = list.get(position).mStatus;
            String row_id = list.get(position).mSupply_id;
            if (!status.equals("0")){
                holder.iv_editRow.setImageResource(R.drawable.ic_baseline_edit_24);

            }else{
                holder.iv_editRow.setImageResource(R.drawable.ic_baseline_edit_24_blue);
                holder.iv_editRow.setOnClickListener(v -> {
                    Intent intent = new Intent(FreshBedrollSupplytoDepot.this, FreshBedRollUpdateActivty.class);

                    ////////////////////////////////////////////////////////////////////////////////////////
                    intent.putExtra("editing",true);
                    ////////////////////////////////////////////////////////////////////////////////////////

                    intent.putExtra("u_date",list.get(position).mSupply_date);
                    intent.putExtra("u_pillow",list.get(position).mPillow);
                    intent.putExtra("u_id",list.get(position).mSupply_id);
                    intent.putExtra("u_depot_code",list.get(position).mDepot_code); //kyq bagera
                    intent.putExtra("u_train_no",list.get(position).mTrain_no);
                    intent.putExtra("u_coach",list.get(position).mCoach);
                    intent.putExtra("u_no_of_bag",list.get(position).mNo_of_bag);
                    intent.putExtra("u_bs",list.get(position).mBs);
                    intent.putExtra("u_pc",list.get(position).mPc);
                    intent.putExtra("u_ft",list.get(position).mFt);
                    intent.putExtra("u_blanket_cover",list.get(position).mBlanket_cover);
                    intent.putExtra("u_bath_towel",list.get(position).mBath_towel);
                    intent.putExtra("u_no_blanket",list.get(position).mNo_blanket);
                    intent.putExtra("u_packet_count",list.get(position).mPacket_count);
                    intent.putExtra("u_remark",list.get(position).mRemark);

                    intent.putExtra("u_depot",list.get(position).mDepot);

//                    Toast.makeText(context, "depot "+list.get(position).mDepot, Toast.LENGTH_SHORT).show();

//                    intent.putExtra("depotAdap",list.get(position).mDepot); //it's numbers
//                    intent.putExtra("depotAdapCode",list.get(position).mDepot_code); // and it's GHY/KYQ
//                    intent.putExtra("laundryApi",fresh_laundry_list);
//                    intent.putExtra("laundry_id", userdataModel.mUserItems.get(0).mLaundryID);
//                    intent.putExtra("laundry_supply_id",userdataModel.mUserItems.get(0).mLogin_id);
//                    intent.putExtra("suply_id",row_id);
//





                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tv_empty_data.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tv_empty_data.setVisibility(View.GONE);
            }

            holder.iv_qr_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scanCodeAlertDialog();
                }

                private void scanCodeAlertDialog() {

                    final Dialog dialog = new Dialog(FreshBedrollSupplytoDepot.this, R.style.Dialog);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    //dialog.setCancelable(false);
                    dialog.setContentView(R.layout.diolog_qr_code_delivery);
                    final ImageView iv_qr_codes = dialog.findViewById(R.id.iv_qr_code);

                    //Picasso.get().load(list.get(position).mQr_url).into(iv_qr_codes);
                    Picasso.with(FreshBedrollSupplytoDepot.this)
                            .load(list.get(position).mQR_URL)
                            .noFade().resize(230, 230)
                            .centerCrop().into(iv_qr_codes);

                    dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (TextUtils.isEmpty(et_date.getText().toString())) {
//                                Toast.makeText(LaundryReceived.this, "Select Date", Toast.LENGTH_SHORT).show();
//                            } else {
                            try {

                            } catch (Exception e) {
                                throw new RuntimeException(e);

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
            });



        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void filterList(ArrayList<GetFreshSupplyList.mGetItem> filteredList) {
            list.clear();
            mListFull.addAll(list);
            notifyDataSetChanged();
        }

        public void resetFilter() {

            if (list!=null){
                list.clear();
                list.addAll(mListFull);
            }else{
                list.clear();
            }
            notifyDataSetChanged();
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_index, tv1, tv2, tv3, tv4, tv5;

        ImageView iv_qr_code,iv_editRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
            tv1 = (TextView) itemView.findViewById(R.id.tv_date);
            tv2 = (TextView) itemView.findViewById(R.id.tv_depot);
            tv3 = (TextView) itemView.findViewById(R.id.tv_packet);
            tv4 = (TextView) itemView.findViewById(R.id.tv_train);
            tv5 = (TextView) itemView.findViewById(R.id.tv_coach);
            iv_qr_code = (ImageView) itemView.findViewById(R.id.iv_qr_scan);
            iv_editRow = (ImageView) itemView.findViewById(R.id.iv_editRow);

        }

    }


}