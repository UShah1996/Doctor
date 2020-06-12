package com.example.android.doctor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.doctor.helper.InputValidation;
import com.example.android.doctor.sql.DatabaseHelper;
import com.example.android.doctor.sql.Patient;
import com.example.android.doctor.sql.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import static android.app.TimePickerDialog.*;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String mName;
    private TextView tvName;
    private String kept;
    private Button ButtonAppointment;
    private DatabaseHelper databaseHelper;
    private InputValidation inputValidation;
    private Patient patient;
    private DatePickerDialog datePickerDialog;
    private EditText datePickerEditText;
    private EditText timePickerEditText;
    private EditText EditTextName;
    private EditText EditTextAge;
    private EditText EditTextMobile;
    private EditText EditTextMail;

    private ArrayList<String> mailpList= new ArrayList<>();
    private LinearLayout LinearLayoutMail;
    private LinearLayout LinearLayoutMobile;
    private LinearLayout LinearLayoutName;
    private LinearLayout LinearLayoutAge;
    private LinearLayout LinearLayoutDate;
    private TextView TextViewPatients;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initValueFromIntent();
        initViews();
        initListeners();
        initObjects();

    }


    private void initValueFromIntent() {
        mName =  getIntent().getStringExtra("USER_NAME");
        kept = mName.substring( 0, mName.indexOf("@"));    }

    private void initViews() {
        tvName =(TextView)findViewById(R.id.tv);
        tvName.setText(kept);

        EditTextName = (EditText) findViewById(R.id.PatientName);
        LinearLayoutName = (LinearLayout) findViewById(R.id.nameLinearLayout);
        EditTextAge = (EditText) findViewById(R.id.Age);
        LinearLayoutAge = (LinearLayout) findViewById((R.id.ageLinearLayout));
        datePickerEditText = (EditText) findViewById(R.id.DatePicker);
        LinearLayoutDate = (LinearLayout) findViewById(R.id.dateLinearLayout);
        timePickerEditText = (EditText) findViewById(R.id.TimePicker);
        LinearLayoutMail = (LinearLayout) findViewById(R.id.mailLayout);
        EditTextMobile = (EditText) findViewById(R.id.MobileNumber);
        LinearLayoutMobile = (LinearLayout) findViewById(R.id.MobileLayout);
        EditTextMail = (EditText) findViewById(R.id.EmailId);
        ButtonAppointment = (Button) findViewById(R.id.Book_button);
    }

    private void initListeners() {
        ButtonAppointment.setOnClickListener(this);
        datePickerEditText.setOnClickListener(this);
        timePickerEditText.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(WelcomeActivity.this);
        inputValidation = new InputValidation(WelcomeActivity.this);
        patient = new Patient();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mymenu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id)
        {
            case R.id.mnLog:
                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.mnExpo:
                databaseHelper.exportDB();
                break;
        }

        return true;
    }

    private void DatePickerDialog() {
        //Get current date
        Calendar calendar = Calendar.getInstance();
        //Create datePickerDialog with the current date
        datePickerDialog = new DatePickerDialog(WelcomeActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //When a date is selected, it comes here.
                //Change the Edittext's text and dismiss dialog.
                monthOfYear = monthOfYear + 1;
                datePickerEditText.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                datePickerDialog.dismiss();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    //Method for the time picker
    private void TimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(WelcomeActivity.this, new OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                String timeSet = "";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12){
                    timeSet = "PM";
                }else{
                    timeSet = "AM";
                }

                String min = "";
                if (minutes < 10)
                    min = "0" + minutes ;
                else
                    min = String.valueOf(minutes);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hour).append(':')
                        .append(min ).append(" ").append(timeSet).toString();
               timePickerEditText.setText(aTime);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DatePicker:
                DatePickerDialog();
                if(datePickerDialog!=null) {
                    datePickerDialog.show();
                }
                break;

            case R.id.TimePicker:
                TimePickerDialog();
                break;

            case R.id.Book_button:
                postDataToSqlite();
                break;
        }
    }


    private void postDataToSqlite() {

        if(!inputValidation.isEditTextFilled(EditTextName,LinearLayoutName,"Please enter the name")) {
            return;
        }

        if(!inputValidation.isEditTextFilled(EditTextAge,LinearLayoutAge,"Please enter the age")){
            return;
        }

        if(!inputValidation.isEditTextFilled(datePickerEditText,LinearLayoutDate,"Enter the date")){
            return;
        }

        if(!inputValidation.isEditTextFilled(timePickerEditText,LinearLayoutDate,"Enter the time")){
            return;
        }

        if (!inputValidation.isEditTextEmail(EditTextMail, LinearLayoutMail, getString(R.string.error_message_email))) {
            return;
        }

        if (!inputValidation.isValidPhoneNumber(EditTextMobile, LinearLayoutMobile, getString(R.string.error_message_mobile))) {
            return;
        }

        patient.setNamep(EditTextName.getText().toString());
        patient.setAge(Integer.parseInt(EditTextAge.getText().toString()));
        patient.setDate(datePickerEditText.getText().toString());
        patient.setTime(timePickerEditText.getText().toString());
        patient.setMail(EditTextMail.getText().toString());
        patient.setMobile(Integer.parseInt(EditTextMobile.getText().toString()));

        mailpList = DatabaseHelper.getInstance(getApplicationContext()).getAllUserPatient();

        DatabaseHelper.getInstance(getApplicationContext()).addUserPatient(patient);
        Toast.makeText(WelcomeActivity.this, "The record is saved successfully", Toast.LENGTH_LONG).show();
        Intent accountsIntent = new Intent(getApplicationContext(), LastActivity.class);
        startActivity(accountsIntent);
        emptyInputEditText();
    }

    private void emptyInputEditText() {
        EditTextName.setText(null);
        EditTextAge.setText(null);
        datePickerEditText.setText(null);
        timePickerEditText.setText(null);
        EditTextMail.setText(null);
        EditTextMobile.setText(null);
    }
}