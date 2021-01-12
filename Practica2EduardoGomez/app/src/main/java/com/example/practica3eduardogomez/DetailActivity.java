package com.example.practica3eduardogomez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.example.practica3eduardogomez.DatabaseConstants.C_ADDED_TIMESTAMP;
import static com.example.practica3eduardogomez.DatabaseConstants.C_ADDRESS;
import static com.example.practica3eduardogomez.DatabaseConstants.C_APPOINTMENT;
import static com.example.practica3eduardogomez.DatabaseConstants.C_EMAIL;
import static com.example.practica3eduardogomez.DatabaseConstants.C_ID;
import static com.example.practica3eduardogomez.DatabaseConstants.C_IMAGE;
import static com.example.practica3eduardogomez.DatabaseConstants.C_LAST_NAME;
import static com.example.practica3eduardogomez.DatabaseConstants.C_NAME;
import static com.example.practica3eduardogomez.DatabaseConstants.C_PHONE_NUMBER;
import static com.example.practica3eduardogomez.DatabaseConstants.C_UPDATED_TIMESTAMP;
import static com.example.practica3eduardogomez.DatabaseConstants.TABLE_NAME;

public class DetailActivity extends AppCompatActivity {

    private static final int PHONE_REQUEST_CODE = 300;
    private static final String TAG = "DetailActivity";
    TextView nameTv, emailTv, addressTv, phoneNumberTv, dateAdded, birthDate;
    //This is wll be used for displaying the picture
    CircularImageView circularProfile;
    DbHelper dbHelper;

    String contactID="";
    String[] phonePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
    }

    private void init(){
        dbHelper = new DbHelper(this);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("Contact detailt"); //Setting the title

        dateAdded = findViewById(R.id.detail_date_added);
        nameTv = findViewById(R.id.detail_name);
        emailTv = findViewById(R.id.detail_email);
        addressTv = findViewById(R.id.detail_address);
        phoneNumberTv = findViewById(R.id.detail_phone_number);
        circularProfile = findViewById(R.id.detail_profileImage);
        phonePermission = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
        birthDate = findViewById(R.id.detail_birthday);

        Intent intent = getIntent();
        if( getIntent().getExtras() != null)
        {
            contactID = intent.getStringExtra("detailID");
            if (!contactID.isEmpty()) {
                ShowDetails();
            }
        }
    }

    private void ShowDetails() {

        //get recons detail

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + C_ID + " =\"" + contactID + "\"";

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String id = "" + cursor.getInt(cursor.getColumnIndex(C_ID));
                String image = "" + cursor.getString(cursor.getColumnIndex(C_IMAGE));
                String name = "" + cursor.getString(cursor.getColumnIndex(C_NAME));
                String lastName = "" + cursor.getString(cursor.getColumnIndex(C_LAST_NAME));
                String email = "" + cursor.getString(cursor.getColumnIndex(C_EMAIL));
                String address = "" + cursor.getString(cursor.getColumnIndex(C_ADDRESS));
                String phoneNumber = "" + cursor.getString(cursor.getColumnIndex(C_PHONE_NUMBER));
                String addedTimeStamp = "" + cursor.getString(cursor.getColumnIndex(C_ADDED_TIMESTAMP));
                String updateTimeStamp = "" + cursor.getString(cursor.getColumnIndex(C_UPDATED_TIMESTAMP));
                String birth = "" + cursor.getString(cursor.getColumnIndex(C_APPOINTMENT));

                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(Long.parseLong(addedTimeStamp));
                String timeAdded = "" + DateFormat.format("dd/MM/yyyy", calendar);

                //i WILL PROBABLY NOT USE THIS
                //Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                //calendar.setTimeInMillis(Long.parseLong(updateTimeStamp));
                //String updatedTime = ""+ DateFormat.format("dd/MM/yyyy hh:mm:ss", calendar1);

                //setData
                nameTv.setText(String.format("Name: %s%s", name, lastName));
                emailTv.setText(String.format("Email: %s", email));
                addressTv.setText(String.format("address: %s", address));
                phoneNumberTv.setText(String.format("phone number: %s", phoneNumber));
                birthDate.setText(String.format("Birthday: %s", birth));
                dateAdded.setText(String.format("Added on: %s", timeAdded));
                circularProfile.setImageURI(Uri.parse(image));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.detail_menu_sms:
                RequestCallingPermission();
                if (CheckForCallPermission())
                    SendSms();
                break;

            case R.id.detail_menu_call:
                RequestCallingPermission();
                if (CheckForCallPermission()) {
                    //Permission is already granted
                    PhoneCall();
                }
                break;
            case R.id.detail_menu_email:
                sendEmail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendSms() {
        //Send the SMS//
        String[] num = phoneNumberTv.getText().toString().split(":");
        String phone = num[1];

        Uri sms_uri = Uri.parse("smsto:" + phone);
        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
        sms_intent.putExtra("sms_body", "");
        startActivity(sms_intent);

    }


    private void PhoneCall() {

        String[] num = phoneNumberTv.getText().toString().split(":");
        String phone = num[1];

        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + phone));
        startActivity(phoneCallIntent);
    }

    protected void sendEmail(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        String [] emailParts = emailTv.getText().toString().trim().split(":");
        String email = emailParts[1];

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Important");
        emailIntent.putExtra(Intent.EXTRA_TEXT, (String) null);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void RequestCallingPermission() {
        ActivityCompat.requestPermissions(this, phonePermission, PHONE_REQUEST_CODE);
    }

    private boolean CheckForCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}