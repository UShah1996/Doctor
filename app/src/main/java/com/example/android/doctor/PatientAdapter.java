package com.example.android.doctor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doctor.sql.Patient;

import java.util.List;

public class PatientAdapter extends ArrayAdapter<Patient> {

    Context mCtx;
    int listLayoutRes;
    List<Patient> patientList;
    SQLiteDatabase mDatabase;

    public PatientAdapter (Context mCtx, int listLayoutRes, List<Patient> patientList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, patientList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.patientList = patientList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Patient patient = patientList.get(position);


        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textViewtime = view.findViewById(R.id.textViewTime);

        textViewName.setText(patient.getNamep());
        textViewDate.setText(patient.getDate());
        textViewtime.setText(String.valueOf(patient.getTime()));


        Button buttonDelete = view.findViewById(R.id.deleteButton);
        Button buttonEdit = view.findViewById(R.id.editButton);

        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(patient);
            }
        });

        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM PATIENT_INFORMATION WHERE id = ?";
                        mDatabase.execSQL(sql, new String[]{patient.getMail()});
                        reloadEmployeesFromDatabase();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void updateEmployee(final Patient patient) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_update_patient, null);
        builder.setView(view);


        final EditText editTextName = view.findViewById(R.id.PatientName);
        final EditText editTextAge = view.findViewById(R.id.Age);
        final EditText editTextdate = view.findViewById(R.id.DatePicker);
        final EditText editTextTime = view.findViewById(R.id.TimePicker);
        final EditText editTextMail = view.findViewById(R.id.EmailId);
        final EditText editTextMobile = view.findViewById(R.id.MobileNumber);


        editTextName.setText(patient.getNamep());
        editTextAge.setText(String.valueOf(patient.getAge()));
        editTextdate.setText(patient.getDate());
        editTextTime.setText(patient.getTime());
        editTextMail.setText(patient.getMail());
        editTextMobile.setText(patient.getMobile());

        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.updatePatient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String age = editTextAge.getText().toString().trim();
                String date = editTextdate.getText().toString();
                String time = editTextTime.getText().toString();
                String mail = editTextMail.getText().toString();
                String mobile = editTextMobile.getText().toString();

                if (name.isEmpty()) {
                    editTextName.setError("Name can't be blank");
                    editTextName.requestFocus();
                    return;
                }

                if (age.isEmpty()) {
                    editTextAge.setError("Age can't be blank");
                    editTextAge.requestFocus();
                    return;
                }

                if( date.isEmpty()){
                    editTextdate.setError("Date can't be blank");
                    editTextdate.requestFocus();
                    return;
                }

                if (time.isEmpty()) {
                    editTextTime.setError("Time can't be blank");
                    editTextTime.requestFocus();
                    return;
                }

                if (mail.isEmpty()) {
                    editTextMail.setError("Mail can't be blank");
                    editTextMail.requestFocus();
                    return;
                }

                if (mobile.isEmpty()) {
                    editTextMobile.setError("Mobile number can't be blank");
                    editTextMobile.requestFocus();
                    return;
                }


                String sql = "UPDATE PATIENT_INFORMATION \n" +
                        "SET name = ?, \n" +
                        "age = ?, \n" +
                        "date = ?, \n" +
                        "time = ?, \n" +
                        "mail = ?, \n" +
                        "mobile = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, age, date, time, mail, mobile, String.valueOf(patient.getMail())});
                Toast.makeText(mCtx, "Patient's Information Updated", Toast.LENGTH_SHORT).show();
                reloadEmployeesFromDatabase();

                dialog.dismiss();
            }
        });
    }


    private void reloadEmployeesFromDatabase() {
        Cursor cursorPatients = mDatabase.rawQuery("SELECT * FROM PATIENT_INFORMATION", null);
        if (cursorPatients.moveToFirst()) {
            patientList.clear();
            do {
                patientList.add(new Patient(
                ));
            } while (cursorPatients.moveToNext());
        }
        cursorPatients.close();
        notifyDataSetChanged();
    }
}
