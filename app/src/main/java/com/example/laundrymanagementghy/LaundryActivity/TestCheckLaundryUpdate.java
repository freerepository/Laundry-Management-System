package com.example.laundrymanagementghy.LaundryActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.laundrymanagementghy.Amodel.GetTestCheckList;
import com.example.laundrymanagementghy.Amodel.UpdateTestCheckDepot;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QtestCheckanswerData;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TestCheckLaundryUpdate extends AppCompatActivity {
    private final static String view_testcheck_API = "http://lmskyq.projectrailway.in/Api/view_testcheck";
    private RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    EditText et_user_type,et_train_no,et_depot,et_remark,et_pamount;
    ImageView iv_backView;
    public JSONArray questionArray;
    TestCheckAdapter adapter;
    UserDataModel userdataModel;
    UpdateTestCheckDepot updateTestCheckDepot=null;
    String id;
    ProgressDialog mProgressDialog;
    GetTestCheckList getTestCheckList;
    GetTestCheckList.mGetTestItem list ;
    public HashMap<String, QtestCheckanswerData> qmaps=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_check_laundry_update);
        updateTestCheckDepot = (UpdateTestCheckDepot) getIntent().getSerializableExtra("qdata");
        id = getIntent().getStringExtra("id");
        try {

            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.iv_backView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_backView = findViewById(R.id.iv_backView);
        et_user_type = findViewById(R.id.et_supervisor);
        et_depot = findViewById(R.id.et_train);
        et_train_no = findViewById(R.id.et_depot);
        et_remark = findViewById(R.id.et_remark);
        et_pamount = findViewById(R.id.et_Pamount);
        recyclerView = findViewById(R.id.recyclerView);
        srl = findViewById(R.id.srl);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(TestCheckLaundryUpdate.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter == null || (adapter == null) || adapter.testcheckList.length() == 0) {
                    if (O.checkNetwork(TestCheckLaundryUpdate.this)) {
                        callTab();

                    }
                } else {
                    srl.setRefreshing(false);
                }
                callTab();
                srl.setRefreshing(false);
            }
        });
        Log.e("ResponceTab1", "in create");
        callTab();
    }

    private void callTab() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("item_id", id);
            jsonObject.put("received_type", "laundry");

            srl.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("test_check", jsonObject.toString());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, view_testcheck_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        srl.setRefreshing(false);
                        Log.e("response_req", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("GetstoreData");
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String user_type = object.getString("user_type");
                                    String train_no = object.getString("train_no");
                                    String depot = object.getString("depot_code");
                                    String remark = object.getString("remark");
                                    String pamount = object.getString("penalty");
                                    et_user_type.setText(user_type);
                                    et_train_no.setText(train_no);
                                    et_depot.setText(depot);
                                    et_remark.setText(remark);
                                    et_pamount.setText(pamount);
                                    // Log.d("select_date", date);

                                    try {
                                        questionArray = object.getJSONArray("testcheckData");
                                        adapter = new TestCheckAdapter(questionArray,getApplicationContext(), TestCheckLaundryUpdate.this);
                                        adapter.testcheckList=questionArray;
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        Log.d("select_list", "item" + questionArray);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    public class TestCheckAdapter extends RecyclerView.Adapter<TestCheckAdapter.MyViewHolder> {
        private Context context;
        private JSONArray testcheckList;
        private  TestCheckLaundryUpdate rating;


        public TestCheckAdapter(JSONArray testcheckList, Context context, TestCheckLaundryUpdate testCheckLaundryUpdate) {
            this.context = context;
            this.testcheckList = testcheckList;
            this.rating=testCheckLaundryUpdate;
        }


        @NonNull
        @Override
        public TestCheckAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_check, parent, false);
            MyViewHolder vh = new MyViewHolder(view);
            return vh;


        }

        @Override
        public void onBindViewHolder(@NonNull TestCheckAdapter.MyViewHolder holder, int pos) {
            holder.setIsRecyclable(false);
            final int position=pos;
            try {
                final JSONObject jsonObject = testcheckList.getJSONObject(position);
                holder.tv_index.setText((position + 1) + "");
                holder.tv_ques.setText(jsonObject.getString("item_name"));
                holder.et_item.setText(jsonObject.getString("item_no"));
                holder.et_wmi.setText(jsonObject.getString("wmi"));



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        @Override
        public int getItemCount() {

            if (testcheckList != null)
                return testcheckList.length();
            else
                return 0;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_index,tv_ques;
            EditText et_item,et_wmi;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_index=itemView.findViewById(R.id.tv_index_number);
                tv_ques=itemView.findViewById(R.id.tv_qus);
                et_item=itemView.findViewById(R.id.et_item);
                et_wmi=itemView.findViewById(R.id.et_wmi);
            }
        }
    }

    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }
}
