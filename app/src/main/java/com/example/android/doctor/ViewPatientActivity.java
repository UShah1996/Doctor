package com.example.android.doctor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.doctor.sql.DBConstant;
import com.example.android.doctor.sql.DatabaseHelper;
import com.example.android.doctor.sql.Patient;

import java.util.ArrayList;
import java.util.List;

public class ViewPatientActivity extends AppCompatActivity {

    List<Patient> patientList;
    private ListView listViewPatients;
    private SQLiteDatabase mDatabase;
    private PatientAdapter adapter;
    private Context mContext;
    private Patient patient;
    private EditText EditTextName;
    private LinearLayout LinearLayoutDate;
    private EditText datePickerEditText;
    private EditText timePickerEditText;
    private String pName;
    private String date;
    private String time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);

        //opening the database
        mDatabase = this.openOrCreateDatabase(DBConstant.DB_NAME, MODE_PRIVATE, null);

        initViews();
        initValuesFromIntent();

        //this method will display the employees in the list
        showEmployeesFromDatabase();
    }

    private void initValuesFromIntent() {
        pName =  getIntent().getStringExtra("USER_NAME");
        date = getIntent().getStringExtra("DATE");
        time = getIntent().getStringExtra("TIME");
    }

    private void initViews(){
        EditTextName = (EditText) findViewById(R.id.PatientName);
        datePickerEditText = (EditText) findViewById(R.id.DatePicker);
        LinearLayoutDate = (LinearLayout) findViewById(R.id.dateLinearLayout);
        timePickerEditText = (EditText) findViewById(R.id.TimePicker);
        listViewPatients = (ListView) findViewById(R.id.listViewPatients);
        patientList = new ArrayList<>();
    }


    private void showEmployeesFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorPatients = mDatabase.rawQuery("SELECT * FROM" +" PATIENT_INFORMATION", null);

        //if the cursor has some data
        if (cursorPatients.moveToFirst()) {
            Patient patient =new Patient();
            patient.setNamep(pName);
            patient.setDate(date);
            patient.setTime(time);

            DatabaseHelper.getInstance(getApplicationContext()).viewPatient(patient);

            //closing the cursor
            cursorPatients.close();

            //creating the adapter object
            adapter = new PatientAdapter(this, R.layout.activity_details, patientList, mDatabase);

            //adding the adapter to listview
            listViewPatients.setAdapter(adapter);
        }
    }
}

