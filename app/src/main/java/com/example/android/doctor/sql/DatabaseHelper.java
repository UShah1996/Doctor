package com.example.android.doctor.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper  {

    private static DatabaseHelper instance;
    static String USER_ID = "user_email_id";
    static String USER_LOCAL_ID = "user_id";
    static String PASSWORD ="password";
    static String DOCTOR_INFORMATION = "doctor_information";
    static String DB_NAME = "Hospital_Management.db";
    static String PATIENT_INFORMATION = "patient_information";
    static String NAME = "name";
    static String AGE = "age";
    static String DATE = "date";
    static String TIME = "time";
    static String MOBILE = "mobile";
    static String EMAIL = "email";
    static int DB_VERSION = 1;
    private  Context context;

    //Creating Doctor table
    private static final String CREATE_USER =
            "CREATE TABLE " + DOCTOR_INFORMATION + " (" + BaseColumns._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_ID + " TEXT, " +  PASSWORD + " TEXT)";
    private String emaild;

    //Creating Patient table
    private static final String CREATE_PATIENT =
            "CREATE TABLE " + PATIENT_INFORMATION + " (" + BaseColumns._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +  AGE + " NUMBER, " +  DATE +  " TEXT, " +
                    TIME + " TEXT, " +  MOBILE + " NUMBER, " + EMAIL + " TEXT )";
    private String mailp;


    public DatabaseHelper(Context mContext) {
        super(mContext,DB_NAME, null, DB_VERSION );
    }

    public static DatabaseHelper getInstance(Context mContext){
        if(instance==null){
            instance = new DatabaseHelper(mContext);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
      //  sqliteDB = context.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_PATIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //--insertData into Database--
    public long insert(String tableName , ContentValues cv){
        return getWritableDatabase().insert(tableName, null,cv);
    }


    public int update(String tableName, ContentValues cv){
        return getWritableDatabase().update(tableName, cv, null, null);
    }

    public int delete(String tableName, String whereClause, String[] pWhereArg){
        return getWritableDatabase().delete(tableName, whereClause, pWhereArg);
    }

    public Cursor select(String tableName , String[] whereClause, String[] pWhereArg){
        return getReadableDatabase().rawQuery("SELECT * FROM " + tableName +" WHERE " + createWhereclause(whereClause), pWhereArg);
    }

    public Cursor select(String tableName){
        return getReadableDatabase().rawQuery("SELECT * FROM " + tableName ,null);
    }

    private String createWhereclause(String[] whereClause) {
        int length = whereClause.length;
        String toReturn = "";
        for(int i = 0; i< length - 1; i++){
            toReturn += whereClause[i] + "= ? AND ";
        }
        toReturn += whereClause[whereClause.length-1] + " =?";
        return toReturn;
    }


    public long addUserDoctor(User user) {
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.getEmail());
        values.put(PASSWORD, user.getPassword());

        // Inserting Row
      return  insert(DOCTOR_INFORMATION, values);

    }


    //Update the data
    public boolean updateData(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, patient.getNamep());
        values.put(AGE, patient.getAge());
        values.put(DATE, patient.getDate());
        values.put(TIME, patient.getTime());
        values.put(MOBILE, patient.getMobile());
        values.put(EMAIL, patient.getMail());
        db.update(PATIENT_INFORMATION, values, "EMAIL = ?",new String[] { mailp });
        return true;
    }

    public long addUserPatient(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(NAME, patient.getNamep());
        values.put(AGE, patient.getAge());
        values.put(DATE, patient.getDate());
        values.put(TIME, patient.getTime());
        values.put(MOBILE, patient.getMobile());
        values.put(EMAIL, patient.getMail());

        //Inserting row
        return insert(PATIENT_INFORMATION, values);
    }

    public long viewPatient(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(NAME, patient.getNamep());
        values.put(DATE, patient.getDate());
        values.put(TIME, patient.getTime());

        return insert(PATIENT_INFORMATION, values);

    }

    public ArrayList<String> getAllUserDoctor(){
        ArrayList<String> emailID = new ArrayList<>();
        Cursor cursor = select(DOCTOR_INFORMATION);
        while(cursor.moveToNext())
        {
            emaild =cursor.getString(cursor.getColumnIndex(USER_ID));
            emailID.add(emaild);

        }
        return  emailID;
    }

    public boolean isUserExist(String doctorUserId) {
        List<String> emailID = new ArrayList<>();
        String password = null;
        Cursor cursor = select(DOCTOR_INFORMATION, new String[]{USER_ID}, new String[]{"" + doctorUserId});

        if (cursor != null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<String> getAllUserPatient(){
        ArrayList<String> mailID = new ArrayList<>();
        Cursor cursor = select(PATIENT_INFORMATION);
        while(cursor.moveToNext())
        {
            mailp = cursor.getString(cursor.getColumnIndex(EMAIL));
            mailID.add(mailp);

        }
        return  mailID;
    }

    public String getUser(String userId){
        List<String> emailID = new ArrayList<>();
        String password = null;
        Cursor cursor = select(DOCTOR_INFORMATION, new String[]{USER_ID}, new String[]{""+ userId});
        if( cursor != null && cursor.moveToFirst() ) {
            password = (cursor.getString(cursor.getColumnIndex(PASSWORD)));
        }
        cursor.close();
        return  password;
    }

    //Gives the detail of patients
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+PATIENT_INFORMATION,null);
        return res;
    }

        public void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.example.android.doctor" +"/databases/"+ DB_NAME ;
        String backupDBPath = DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
    }
}

}

