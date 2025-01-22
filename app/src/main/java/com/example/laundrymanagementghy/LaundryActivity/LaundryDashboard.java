package com.example.laundrymanagementghy.LaundryActivity;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Activity.IntroSliderActivity;
import com.example.laundrymanagementghy.Activity.LoginActivity;
import com.example.laundrymanagementghy.Amodel.LaundryCategory;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.LaundryActivity.PenaltyScreens.LaundryPenltryScreenActivity;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LaundryDashboard extends AppCompatActivity {
    public static String LIST_CATEGORY = "http://lmskyq.projectrailway.in/Api/get_maincategory";

    TextView tv_user_name;
    UserDataModel userdataModel;
    RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    ImageView backImage,vlogout;
    TaskTypeAdapter taskAdapter;
    ArrayList<LaundryCategory.Item> shop_list = new ArrayList<>();

    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_login);

        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), (Type) UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        uiModeManager = (UiModeManager) getApplicationContext().getSystemService(getApplicationContext().UI_MODE_SERVICE);


        //UI MODE SERVICES
        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);

        tv_user_name = findViewById(R.id.tvUserType);
        backImage = findViewById(R.id.v_back);
        vlogout = findViewById(R.id.iv_logoutt);
        recyclerView = findViewById(R.id.recyclerView);
        srl = findViewById(R.id.srl);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), IntroSliderActivity.class));
            }
        });
        tv_user_name.setText(userdataModel.mUserItems.get(0).mHeader);

        vlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskTypeAdapter(new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setHasFixedSize(true);
        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(LaundryDashboard.this)) {
                CategoryLaundry();
            } else {
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

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, LIST_CATEGORY, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("responseList", response.toString());
                        try {
                            LaundryCategory getCategory = new Gson().fromJson(response.toString(), LaundryCategory.class);
                            if (getCategory.CatList.size() > 0) {
                                taskAdapter.list = shop_list = getCategory.CatList;
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
                Toast.makeText(LaundryDashboard.this, "Error: File not Show", Toast.LENGTH_SHORT).show();


            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }




    public class TaskTypeAdapter extends RecyclerView.Adapter<LaundryDashboard.ViewHolder> {
        private Object object;
        private ArrayList<LaundryCategory.Item> list;

        UiModeManager uiModeManager;

        public TaskTypeAdapter(Object object) {
            this.object = object;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_task_type, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
            final int position = pos;
            uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);


//            if (uiModeManager.getNightMode()==UiModeManager.MODE_NIGHT_YES){
//                holder.tv.setBackgroundResource(R.drawable.et_black_bg);
//                holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.whiteTextColor));
////                holder.tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorHint));
//            }else{
//                holder.tv.setBackgroundResource(R.drawable.et_white_bg);
//                holder.tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorBlue1));
//
//            }

            holder.tv.setText(list.get(position).mCategory_name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   if (list.get(position).mCategory_name.trim().equals("Bedroll Stocking")) {
                       Intent intent = new Intent(LaundryDashboard.this, BedrollStocking.class);
                       startActivity(intent);
                   } else if (list.get(position).mCategory_name.trim().equals("Soiled Bedroll Received from Depot")) {
                        Intent intent = new Intent(LaundryDashboard.this, FreshBedrollReceiptfromLaundry.class);
                        startActivity(intent);
                   } else if (list.get(position).mCategory_name.trim().equals("Fresh Bedroll Supply to Depot")) {
                        Intent intent = new Intent(LaundryDashboard.this, FreshBedrollSupplytoDepot.class);
                        startActivity(intent);
                    } else if (list.get(position).mCategory_name.trim().equals("Bedroll Condemnation")) {
                        Intent intent = new Intent(LaundryDashboard.this, CondemnedBedrollActivity.class);
                        startActivity(intent);
                    } else if (list.get(position).mCategory_name.trim().equals("Penalty")) {
//                        Intent intent = new Intent(LaundryDashboard.this, PenaltyModuleActivity.class);
                        Intent intent = new Intent(LaundryDashboard.this, LaundryPenltryScreenActivity.class);
                        startActivity(intent);
                    } else if (list.get(position).mCategory_name.trim().equals("Buffer Stock issue to Depot")) {
                        Intent intent = new Intent(LaundryDashboard.this, BufferStockIssuetoDepot.class);
                        startActivity(intent);
                    } else if (list.get(position).mCategory_name.trim().equals("Scan")) {
                        Intent intent = new Intent(LaundryDashboard.this, QrCodeScannerActivity.class);
                       // intent.putExtra("id",list.get(position).mID);
                            startActivity(intent);
                       // crcodescan();
                    }else if(list.get(position).mCategory_name.trim().equals("Receive returned Buffer from Laundry")){
                       Intent intent = new Intent(LaundryDashboard.this, ReceiveReturnedBufferFromLaundry.class);
                       startActivity(intent);
                   }

                }

//                private void crcodescan() {
//                    IntentIntegrator intentIntegrator = new IntentIntegrator(LaundryDashboard.this);
//                    intentIntegrator.setCaptureActivity(CaptureAct.class);
//                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//                    intentIntegrator.setPrompt("Scan a barcode or QR Code");
//                    intentIntegrator.setOrientationLocked(false);
//                    intentIntegrator.initiateScan();
//
//                }
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

    public void showLogoutAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        TextView tv = dialog.findViewById(R.id.tv);
        tv.setText("Logout Confirm ?");
        dialog.findViewById(R.id.v_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                O.clearPref(LaundryDashboard.this);
                Intent i = new Intent(LaundryDashboard.this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), IntroSliderActivity.class));
//        finishAffinity();
//        System.exit(0);
        super.onBackPressed();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
//        if (intentResult != null) {
//            if (intentResult.getContents() == null) {
//                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
//            } else {
//                // if the intentResult is not null we'll set
//                // the content and format of scan message
//                // tv1.setText(intentResult.getContents());
//                //tv1.setText(intentResult.getFormatName());
//
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//
//        }
//    }
}