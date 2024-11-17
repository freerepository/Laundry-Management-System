package com.example.laundrymanagementghy.LaundryActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.resoures.QTaskCheckModel;
import com.example.laundrymanagementghy.resoures.QtestCheckanswerData;
import com.example.laundrymanagementghy.util.O;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCheckLaundryAddActivity extends AppCompatActivity {
    private final static String Train_List_API = "http://lmsguwahati.projectrailway.in/Api/get_trains";
    private final static String SupOfficerList_API = "http://lmsguwahati.projectrailway.in/Api/get_suptypes";
    private final static String get_depot = "http://lmsguwahati.projectrailway.in/Api/get_depots";
    private final static String Remark_API = " http://lmsguwahati.projectrailway.in/Api/get_remark";
    private final static String questionAPI = "http://lmsguwahati.projectrailway.in/Api/get_laundryItems";
    private RecyclerView recyclerView;
    SwipeRefreshLayout srl;
    Button btn_next;
    EditText et_pamount;
    ImageView iv_backView;
    View signature_layout;
    TextView tv_total_penality_amount;
    Spinner sp_sup_officer,spTrainNo,sp_depotName,sp_remark;
    public JSONArray questionArray;


    ProgressDialog mProgressDialog;
    int shortfall_focus_position=0;
    int et_position=0;

    public String selectedTrain="", selectedSupOfficer="",selectedDepot,selectRemark;
     TaskCheckAdapter taskCheckAdapter;
    public HashMap<String, QtestCheckanswerData> qmaps=new HashMap<>();
    UserDataModel userdataModel;
    QTaskCheckModel qTaskCheckModel=null;
    ArrayList<String> trainNoList = new ArrayList<>(), train_id_list = new ArrayList<>();
    ArrayList<String> supOfficerNameList = new ArrayList<>(),supOfficer_id_list = new ArrayList<>();
    ArrayList<String> depotList = new ArrayList<>(), depot_id_list = new ArrayList<>();
    ArrayList<String> remarkList = new ArrayList<>(),remark_id_list = new ArrayList<>();
    ArrayAdapter<String> trainNoAdapter,supOfficerAdapter,depotAdapter,remarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_check_add_laundry);
        qTaskCheckModel=(QTaskCheckModel) getIntent().getSerializableExtra("qdata");
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

        signature_layout = findViewById(R.id.signature_layout);
        sp_sup_officer = findViewById(R.id.sp_officer_supervisor);
        sp_depotName = findViewById(R.id.sp_train_no);
        spTrainNo=findViewById(R.id.sp_depotName);
        sp_remark=findViewById(R.id.sp_remark);
        et_pamount=findViewById(R.id.et_Pamount);
        srl = findViewById(R.id.srl);
        btn_next = findViewById(R.id.btn_next_submit);
        iv_backView=findViewById(R.id.iv_backView);
        btn_next.setText("Next");
        tv_total_penality_amount = findViewById(R.id.tv_total_penality_amount);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(TestCheckLaundryAddActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(true);
        srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (taskCheckAdapter == null || taskCheckAdapter.testcheckList.length()==0) {
                    if (O.checkNetwork(TestCheckLaundryAddActivity.this)) {
                        getTaskCheckQList();

                    }
                } else {
                    srl.setRefreshing(false);

                }
            }
        });

        iv_backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Registeration.this.onBackPressed();
                android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(TestCheckLaundryAddActivity.this);

                alertbox.setTitle("Exit ? All data & progress will be lost!");
                alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // finish used for destroyed activity
                        finish();
                    }
                });

                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Nothing will be happened when clicked on no button
                        // of Dialog
                    }
                });
                alertbox.show();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isAllChecked = true;
                if (taskCheckAdapter == null || taskCheckAdapter.testcheckList.length() == 0) {
                    isAllChecked = false;
                } else {
                    for (int n = 0; n <= taskCheckAdapter.testcheckList.length(); n++) {
                        try {
                            String key = taskCheckAdapter.testcheckList.getJSONObject(n).getString("id");
//                            if (!qmaps.containsKey(key)){
//                                isAllChecked=false;
//                                Toast.makeText(getApplicationContext(), "Please give all rating",
//                                        Toast.LENGTH_LONG).show();
//                                break;
                            // }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (sp_sup_officer.getSelectedItemPosition() == 0) {
                    Toast.makeText(TestCheckLaundryAddActivity.this, "Select Supervisor Officer", Toast.LENGTH_LONG).show();
                } else if (spTrainNo.getSelectedItemPosition() == 0) {
                    Toast.makeText(TestCheckLaundryAddActivity.this, "Select Train", Toast.LENGTH_LONG).show();
                } else if (sp_depotName.getSelectedItemPosition() == 0) {
                    Toast.makeText(TestCheckLaundryAddActivity.this, "Select Depot", Toast.LENGTH_LONG).show();

                } else if (isAllChecked) {
                    Intent i = new Intent(TestCheckLaundryAddActivity.this, TakeCameraLaundry.class);
                    i.putExtra("qdata", qmaps);
                    i.putExtra("train_no",selectedTrain);
                    i.putExtra("user_type",selectedSupOfficer);
                    i.putExtra("depot_code",selectedDepot);
                    i.putExtra("remark",selectRemark);
                    i.putExtra("penalty",et_pamount.getText().toString());


                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Please give all rating",
                            Toast.LENGTH_LONG).show();
                }
            }

        });

        trainNoList.add(0, "Select Train.");
        trainNoAdapter = new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList);
        spTrainNo.setAdapter(trainNoAdapter);
        spTrainNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedTrain = "";

                } else {
                    selectedTrain = trainNoList.get(i);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        GetLaundryType();
        depotList.add(0, "Select Depot.");
        depotAdapter = new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, depotList);
        sp_depotName.setAdapter(depotAdapter);
        sp_depotName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedDepot = "";

                } else {
                    selectedDepot = depotList.get(i);
                    GetTrain(selectedDepot);
                    Log.e("selectedDepot",selectedDepot);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        GetRemarkType();
        remarkList.add(0, "Select remark.");
        remarkAdapter = new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, remarkList);
        sp_remark.setAdapter(remarkAdapter);
        sp_remark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectRemark = "";

                } else {
                    selectRemark = remarkList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        GetSupOfficerList();
        supOfficerNameList.add(0, "Select One");
        supOfficerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, supOfficerNameList);
        supOfficerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_sup_officer.setAdapter(supOfficerAdapter);
        sp_sup_officer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int myPosition, long myID) {
                if (myPosition == 0) {
                    selectedSupOfficer = "";
                    // signature_layout.setVisibility(View.GONE);
                } else {
                    selectedSupOfficer = supOfficerNameList.get(myPosition);
                    //  signature_layout.setVisibility(View.VISIBLE);
                    getTaskCheckQList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void getTaskCheckQList() {
        JSONObject jsonObject = new JSONObject();

        srl.setRefreshing(true);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, questionAPI, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        srl.setRefreshing(false);
                        Log.e("response_req", response.toString());
                        try {

                            questionArray = response.getJSONArray("laundry_items");
                            taskCheckAdapter = new TaskCheckAdapter(questionArray,getApplicationContext(), TestCheckLaundryAddActivity.this);
                            taskCheckAdapter.testcheckList=questionArray;
                            recyclerView.setAdapter(taskCheckAdapter);
                            taskCheckAdapter.notifyDataSetChanged();
                            qTaskCheckModel = new Gson().fromJson(response.toString(), QTaskCheckModel.class);


                        }catch (Exception e){}


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    public class TaskCheckAdapter extends RecyclerView.Adapter<TaskCheckAdapter.MyViewHolder> {
        private Context context;
        private JSONArray testcheckList;
        private TestCheckLaundryAddActivity rating;


        public TaskCheckAdapter(JSONArray testcheckList, Context context, TestCheckLaundryAddActivity testCheckLaundry) {
            this.context = context;
            this.testcheckList = testcheckList;
            this.rating=testCheckLaundry;
        }


        @NonNull
        @Override
        public TaskCheckAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_task_check, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TaskCheckAdapter.MyViewHolder holder, int pos) {
            holder.setIsRecyclable(false);
            final int position=pos;
            try {
                final JSONObject jsonObject = testcheckList.getJSONObject(position);
                holder.tv_index.setText((position + 1) + "");
                holder.tv_ques.setText(jsonObject.getString("item_name"));

                for (QtestCheckanswerData qtestCheckData : qmaps.values()) {
                    if (qtestCheckData.quest_id.equalsIgnoreCase(jsonObject.getString("id"))) {
                        try {
                            holder.et_item.setText(qtestCheckData.item_no);
                            holder.et_wmi.setText(qtestCheckData.wmi);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                holder.et_wmi.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.e("data",s.toString());
                        shortfall_focus_position=position;
                        et_position=1;
                        try {
                            holder.et_wmi.requestFocus(holder.et_wmi.getText().length());
                            if (!holder.et_wmi.getText().toString().trim().isEmpty()) {
                                String  ques_id = "",wmi="",item_no="",item_name="";
                                ques_id = jsonObject.getString("id");
                                item_name = jsonObject.getString("item_name");
                                wmi = holder.et_wmi.getText().toString().trim();
                                item_no = holder.et_item.getText().toString().trim();

                                itemselect(new QtestCheckanswerData().setQuestId(ques_id).setWmi(wmi).setItem_no(item_no).setItemName(item_name));

                            } else {
                                Log.e("sks ","item unselect called");
                                itemUnSelect(jsonObject.getString("id"));

                            }
                            notifyItemChanged(position,jsonObject);
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                    }
                });

                holder.et_item.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.e("data",editable.toString());
                        et_position=0;
                        shortfall_focus_position=position;
                        try {
                            holder.et_item.requestFocus(holder.et_item.getText().length());
                            if (!holder.et_item.getText().toString().trim().isEmpty()) {
                                String ques_id = "",item_no="",wmi="",item_name="";
                                ques_id = jsonObject.getString("id");
                                item_name = jsonObject.getString("item_name");
                                wmi=holder.et_wmi.getText().toString().trim();
                                item_no = holder.et_item.getText().toString().trim();

                                itemselect(new QtestCheckanswerData().setQuestId(ques_id).setItem_no(item_no).setWmi(wmi).setItemName(item_name));

                            } else {
                                Log.e("sks ","item unselect called");
                                itemUnSelect(jsonObject.getString("id"));


                            }
                            notifyItemChanged(position,jsonObject);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }catch (Exception e){ }

            if(position==shortfall_focus_position) {

                if (et_position==0)
                 holder.et_item.requestFocus();

                if (et_position==1)
                    holder.et_wmi.requestFocus();

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
    public void itemselect(QtestCheckanswerData qtestCheckData){

        qmaps.put(qtestCheckData.quest_id, qtestCheckData);
        taskCheckAdapter.notifyDataSetChanged();
        Log.e("kumar select", "qdata "+
                "\nquestid "+qtestCheckData.quest_id+" "+
                "\nitem_name "+qtestCheckData.item_name+" "+
                "\nitem_no "+qtestCheckData.item_no+" "+
                "\nwmi "+qtestCheckData.wmi+" ");
    }

    public void itemUnSelect(String itemId) {
        qmaps.remove(itemId);
        taskCheckAdapter.notifyDataSetChanged();
    }

    private void GetTrain(String selectedDepot) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code",selectedDepot);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Train_List_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("train_data");
                            trainNoList.clear();
                            trainNoList.add(0,"Select Train");
                            train_id_list.clear();
                            train_id_list.add(0,"Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                trainNoList.add(obj.getString("train_no"));
                                train_id_list.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spTrainNo.setAdapter(new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList));
                        spTrainNo.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    private void GetLaundryType() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code", userdataModel.mUserItems.get(0).mDepot_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, get_depot, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("depot_data");
                            depotList.clear();
                            depotList.add(0,"Select Depot");
                            depot_id_list.clear();
                            depot_id_list.add(0,"Select Depot");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                depotList.add(obj.getString("depot_code"));
                                depot_id_list.add(obj.getString("id"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_depotName.setAdapter(new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, depotList));
                        sp_depotName.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    private void GetSupOfficerList() {

        JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, SupOfficerList_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("getData");
                            if (array.length() > 0) {
                                supOfficerNameList.clear();
                                supOfficerNameList.add(0,"Select One");
                                supOfficer_id_list.clear();
                                supOfficer_id_list.add(0,"Select One");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    supOfficerNameList.add(obj.getString("name"));
                                    supOfficer_id_list.add(obj.getString("id"));

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_sup_officer.setAdapter(new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, supOfficerNameList));
                        sp_sup_officer.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    private void GetRemarkType() {

        JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Remark_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("remarkData");
                            if (array.length() > 0) {
                                remarkList.clear();
                                remarkList.add(0,"Select Remark");
                                remark_id_list.clear();
                                remark_id_list.add(0,"Select Remark");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    remarkList.add(obj.getString("remark"));
                                    remark_id_list.add(obj.getString("id"));

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_remark.setAdapter(new ArrayAdapter<String>(TestCheckLaundryAddActivity.this, android.R.layout.simple_spinner_dropdown_item, remarkList));
                        sp_remark.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

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

