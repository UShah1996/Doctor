package com.example.android.doctor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NameActivity extends AppCompatActivity{


    private String pName;
    private String kept;
    private TextView tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);
        initValueFromIntent();
        initViews();
    }

    private void initViews() {
        pName =  getIntent().getStringExtra("USER_NAME");
    }

    private void initValueFromIntent() {
        tvName =(TextView)findViewById(R.id.textViewName);
        tvName.setText(pName);
    }
}
