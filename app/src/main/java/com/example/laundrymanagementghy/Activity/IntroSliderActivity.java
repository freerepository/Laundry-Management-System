package com.example.laundrymanagementghy.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.laundrymanagementghy.Adapter.ViewPagerAdapter;
import com.example.laundrymanagementghy.Amodel.UserDataModel;
import com.example.laundrymanagementghy.DeportActivity.DeportDashboard;
import com.example.laundrymanagementghy.LaundryActivity.LaundryDashboard;
import com.example.laundrymanagementghy.R;
import com.example.laundrymanagementghy.util.O;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

public class IntroSliderActivity extends AppCompatActivity {

    TextView tv_Laundry_Type;
    Button btn_next_submit;
    UserDataModel userdataModel;
    ImageView vlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        try {
            userdataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        vlogout = findViewById(R.id.iv_logout);
        vlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlertDialog();
            }
        });
        btn_next_submit=findViewById(R.id.btn_next_submit);
        tv_Laundry_Type=findViewById(R.id.tv_Laundry_Type);
        tv_Laundry_Type.setText(userdataModel.mUserItems.get(0).mHeader);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,userdataModel.mUserItems.get(0).mLaundryID);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Setting custom indicators
            tab.setText(""); // Remove text if you just want dots
            tab.setIcon(R.drawable.dot); // Use a custom drawable for the dot, like a small circle
        }).attach();

        btn_next_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introslider=new Intent(getApplicationContext(), LaundryDashboard.class);
                startActivity(introslider);
            }
        });
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
                O.clearPref(IntroSliderActivity.this);
                Intent i=new Intent(IntroSliderActivity.this, LoginActivity.class);
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


}