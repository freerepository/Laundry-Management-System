package com.example.laundrymanagementghy.DeportActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.LoginActivity;
import com.example.laundrymanagementghy.Amodel.LaundryCategory;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.PanaltyUpdateScreen.Depot_PanaltyScreen;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DeportDashboard extends AppCompatActivity {
    public static String Depot_CATEGORY = "http://lmsguwahati.projectrailway.in/Api/get_maincategory";
    TextView tv_user_name;
    ImageView vlogout;
    ImageView v_back_button;
    UserDataModel userdataModel;

    RecyclerView recyclerView;
    SwipeRefreshLayout srl;

    TaskTypeDeportAdapter taskAdapter;
    ArrayList<LaundryCategory.Item> shop_list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deport_dashboard);
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toast.makeText(this, "laundry id "+userdataModel.mUserItems.get(0).mLaundryID, Toast.LENGTH_SHORT).show();

        vlogout = findViewById(R.id.iv_logout);
        v_back_button=findViewById(R.id.v_back_button);
        tv_user_name = findViewById(R.id.tvUserType);
        tv_user_name.setText(userdataModel.mUserItems.get(0).mArea_name);

        vlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });


        v_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskTypeDeportAdapter(new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setHasFixedSize(true);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if(O.checkNetwork(DeportDashboard.this)) {
                CategoryLaundry();
            }else{
                srl.setRefreshing(false);
            }
        });
        CategoryLaundry();
    }

    private void CategoryLaundry() {
        srl.setRefreshing(false);
        final JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("type", userdataModel.mUserItems.get(0).mType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        srl.setRefreshing(false);
        final String requestBody = jsonObject.toString();
        Log.e("reqbody", requestBody);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Depot_CATEGORY, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("responseList", response.toString());
                        try {
                            LaundryCategory getCategory = new Gson().fromJson(response.toString(), LaundryCategory.class);
                            if (getCategory.CatList.size() > 0) {
                                taskAdapter.list=shop_list=getCategory.CatList;
                                recyclerView.setAdapter(taskAdapter);

                            } else {

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DeportDashboard.this,"Error: File not Show",Toast.LENGTH_SHORT).show();


            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    public class TaskTypeDeportAdapter extends RecyclerView.Adapter<DeportDashboard.ViewHolder> {
        private Object object;
        private ArrayList<LaundryCategory.Item> list;

        public TaskTypeDeportAdapter(Object object) {
            this.object = object;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_task_type_deport, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder , final int pos) {
            final int position=pos;
            holder.tv.setText(list.get(position).mCategory_name);
//            Toast.makeText(DeportDashboard.this, "depot code "+userdataModel.mUserItems.get(0).mDepot_code, Toast.LENGTH_SHORT).show();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (list.get(position).mCategory_name.trim().equals("Buffer Received From Laundry")){
                        Intent intent = new Intent(DeportDashboard.this, BufferReceiptFromLaundryActivity.class);
                        startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("Fresh Bedroll Received From Laundry")) {
                        Intent intent = new Intent(DeportDashboard.this, BFreshBedrollReceiptFromLaundryActivity.class);
                        intent.putExtra("model",userdataModel.mUserItems.get(0).mDepot_code);
                        startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("Fresh Bedroll Supply to Train")) {
                        Intent intent = new Intent(DeportDashboard.this, BFreshBedrollSupplytoTrainActivity.class);
                        startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("Soiled Bedroll Return to Laundry")) {
                            Intent intent = new Intent(DeportDashboard.this, ReceivedFromTrainActivity.class);
                            startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("Bedroll Return to Laundry From Buffer Stock")) {
                        Intent intent = new Intent(DeportDashboard.this, BedrollReturntoLaundrytFromBufferStockActivity.class);
                        startActivity(intent);

                    }
                    else if (list.get(position).mCategory_name.trim().equals("Penalty")) {
//                        Intent intent = new Intent(DeportDashboard.this,PenaltyActivity.class);
                        Intent intent = new Intent(DeportDashboard.this, Depot_PanaltyScreen.class);
                        startActivity(intent);

                    }
                    else if (list.get(position).mCategory_name.trim().equals("Scan")) {
                        Intent intent = new Intent(DeportDashboard.this,QrCodeDepotActivity.class);
                        startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("CBS Received From Store")) {
                        Intent intent = new Intent(DeportDashboard.this,CBSReceivedFromstore.class);
                        startActivity(intent);
                    }
                    else if (list.get(position).mCategory_name.trim().equals("Supply CBS to Laundry")) {
                        Intent intent = new Intent(DeportDashboard.this,SupplyCondemneBedSheetDepot.class);
                        startActivity(intent);

                    }
                    else if (list.get(position).mCategory_name.trim().equals("Received CBS from Laundry")) {
                        Intent intent = new Intent(DeportDashboard.this,ReceivedCBSFromLaundry.class);
                        startActivity(intent);

                    }
                    else if (list.get(position).mCategory_name.trim().equals("Sent CBS to Store")) {
                        Intent intent = new Intent(DeportDashboard.this, SentCBStoStoreActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            if (list != null)
                return list.size();
            else
                return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_view);
        }
    }
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Exit ? Click on Exit Button!")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        DeportDashboard.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAffinity();

    }
    public void showLogoutAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        TextView tv=dialog.findViewById(R.id.tv);
        tv.setText("Logout Confirm ?");
        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                O.clearPref(DeportDashboard.this);
                Intent i=new Intent(DeportDashboard.this, LoginActivity.class);
                finishAffinity();
                startActivity(i);
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
 //   @Override
//    public void onBackPressed() {
//        finishAffinity();
//        System.exit(0);
//        super.onBackPressed();
    }
