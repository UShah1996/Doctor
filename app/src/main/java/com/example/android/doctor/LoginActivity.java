package com.example.android.doctor;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doctor.helper.InputValidation;
import com.example.android.doctor.sql.DBConstant;
import com.example.android.doctor.sql.DatabaseHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout LinearLayoutEmail;
    private NestedScrollView nestedScrollView;
    private LinearLayout LinearLayoutPassword;
    private EditText editTextEmailText;
    private EditText editTextPasswordText;
    private Button ButtonLogin;
    private TextView textViewLinkRegister;
    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private String password;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            initViews();
            initListeners();
            initObjects();
            mDatabase = this.openOrCreateDatabase(DBConstant.DB_NAME, MODE_PRIVATE, null);
            databaseHelper.exportDB();
            mDatabase.close();
            mDatabase.getPath();

    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        LinearLayoutEmail = (LinearLayout) findViewById(R.id.LayoutEmail);

        LinearLayoutPassword = (LinearLayout) findViewById(R.id.LayoutPassword);

        editTextEmailText = (EditText) findViewById(R.id.EditTextEmail);

        editTextPasswordText = (EditText) findViewById(R.id.EditTextPassword);

        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);

        textViewLinkRegister = (TextView) findViewById(R.id.LinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        ButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(LoginActivity.this);
        inputValidation = new InputValidation(LoginActivity.this);

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.LinkRegister:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        if (!inputValidation.isEditTextFilled(editTextEmailText, LinearLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isEditTextEmail(editTextEmailText, LinearLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isEditTextFilled(editTextPasswordText, LinearLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }

//        String currentString = "Fruit: they taste good";
//        String[] separated = currentString.split(":");
//        separated[0]; // this will contain "Fruit"
//        separated[1]; // this will contain " they taste good"

        password = DatabaseHelper.getInstance(getApplicationContext()).getUser(editTextEmailText.getText().toString());
        if(editTextPasswordText.getText().toString().equals(password)) {
            Intent accountsIntent = new Intent(getApplicationContext(), WelcomeActivity.class);

                accountsIntent.putExtra("USER_NAME" ,editTextEmailText.getText().toString() );
                startActivity(accountsIntent);

        }
        else{
            Toast.makeText(getApplicationContext(), "Password wrong" ,Toast.LENGTH_LONG).show();
        }

    }

}


