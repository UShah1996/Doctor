package com.example.android.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doctor.helper.InputValidation;
import com.example.android.doctor.sql.DatabaseHelper;
import com.example.android.doctor.sql.User;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private NestedScrollView nestedScrollView;
    private LinearLayout LayoutEmail;
    private LinearLayout LayoutPassword;
    private LinearLayout LayoutConfirmPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button ButtonRegister;
    private TextView TextViewLoginLink;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;
    private ArrayList<String> emaildList= new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        LayoutEmail = (LinearLayout) findViewById(R.id.LayoutEmail);
        LayoutPassword = (LinearLayout) findViewById(R.id.LayoutPassword);
        LayoutConfirmPassword = (LinearLayout) findViewById(R.id.LayoutConfirmPassword);

        editTextEmail = (EditText) findViewById(R.id.EditTextEmail);
        editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.EditTextConfirmPassword);

        ButtonRegister = (Button) findViewById(R.id.ButtonRegister);

        TextViewLoginLink = (TextView) findViewById(R.id.LoginLink);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        ButtonRegister.setOnClickListener(this);
        TextViewLoginLink.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        inputValidation = new InputValidation(RegisterActivity.this);
        databaseHelper = new DatabaseHelper(RegisterActivity.this);
        user = new User();

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ButtonRegister:
                postDataToSQLite();
                break;

            case R.id.LoginLink:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void postDataToSQLite() {

        if (!inputValidation.isEditTextFilled(editTextEmail, LayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isEditTextEmail(editTextEmail, LayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isEditTextFilled(editTextPassword, LayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isEditTextMatches(editTextPassword, editTextConfirmPassword,
                LayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

            user.setEmail(editTextEmail.getText().toString());
            user.setPassword(editTextPassword.getText().toString());
//            emaildList = DatabaseHelper.getInstance(getApplicationContext()).getAllUserDoctor();

        boolean isUserExist = false;
        isUserExist = DatabaseHelper.getInstance(getApplicationContext()).isUserExist(user.getEmail());

        if(isUserExist) {
            Toast.makeText(getApplicationContext(), "Already exist", Toast.LENGTH_SHORT).show();
        } else{
            DatabaseHelper.getInstance(getApplicationContext()).addUserDoctor(user);
            Toast.makeText(RegisterActivity.this, "The record is saved successfully", Toast.LENGTH_LONG).show();
            emptyInputEditText();
            Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLogin);
        }

    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        editTextEmail.setText(null);
        editTextPassword.setText(null);
        editTextConfirmPassword.setText(null);
    }

}
