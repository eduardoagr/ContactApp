package com.example.practica3eduardogomez;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class AddUpdateContactActivity extends AppCompatActivity {

    //This is used for debugging purposes
    private static final String TAG = "ed";

    //Declaration of widget
    CircularImageView profileImage;
    EditText nameEt, lastNameEt, emailEt, addressEt, phoneNumberEt, birth;

    /*This will contained our image and data to save, I created a class for this purpose.
    // NO MORE THAN 300 LINES OF CODE IN AN ACTIVITY (SOMETIMES YOU CAN BEND THIS RULE), AND ALWAYS MAKE THING ON METHODS (IF POSSIBLE)*/
    Uri ImageFileUri;
    DatePickerDialog picker;
    String id, name, lastName, email, address, phoneNumber, addedTime, updatedTime;

    boolean isEditMode = false;

    //Now we need to crate a variable for our db
    DbHelper dbHelper;
    //Permissions codes
    public static final int CAMERA_REQUEST_CODE = 100, STORAGE_REQUEST_CODE = 101,
            IMAGE_PICK_CAMERA_CODE = 102,
            IMAGE_PICK_GALLERY_CODE = 103,
            FINE_LOCATION = 104;

    //Array of permission
    String[] cameraPermission, storagePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_contact);

        Init();
    }


    //This is for setting up the widgets and permissions
    private void Init() {
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("Add a new Contact"); //Setting the title

        //Initialising array permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        profileImage = findViewById(R.id.addUpdate_profilePicture);
        nameEt = findViewById(R.id.addUpdate_name);
        lastNameEt = findViewById(R.id.addUpdate_lastName);
        emailEt = findViewById(R.id.addUpdate_email);
        addressEt = findViewById(R.id.addUpdate_address);
        birth = findViewById(R.id.addUpdate_birthday);
        phoneNumberEt = findViewById(R.id.addUpdate_phoneNumber);

        //Get data from intent
        Intent intent = getIntent();
        //Avoid nulls
        if( getIntent().getExtras() != null)
        {
            isEditMode = intent.getBooleanExtra("isEditMode", false);
        }
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        if (isEditMode) {
            getSupportActionBar().setTitle("Update");

            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            lastName = intent.getStringExtra("LAST_NAME");
            email = intent.getStringExtra("EMAIL");

            // II was trying to implement google places api, that I always do this, but is deprecated.
            // I tried to use it, but I cannot retrieve the address
            address = intent.getStringExtra("ADDRESS");
            phoneNumber = intent.getStringExtra("PHONE_NUMBER");
            addedTime = intent.getStringExtra("ADDED_TIME");
            updatedTime = intent.getStringExtra("UPDATE_TIME");
            ImageFileUri = Uri.parse(intent.getStringExtra("IMAGE"));


            nameEt.setText(name);
            lastNameEt.setText(lastName);
            emailEt.setText(email);
            addressEt.setText(address);
            phoneNumberEt.setText(phoneNumber);

            if (ImageFileUri.toString().equals("null")) {
                profileImage.setImageResource(R.drawable.ic_baseline_person_24);
            } else {
                profileImage.setImageURI(ImageFileUri);
            }
        }
        //I think Java borrowed this from kotlin
        findViewById(R.id.addUpdate_save).setOnClickListener(view -> {
            InputData();
        });

        dbHelper = new DbHelper(this);
    }

    //Method for saving data
    private void InputData() {

        name = nameEt.getText().toString().trim();
        lastName = lastNameEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        address = addressEt.getText().toString().trim();
        phoneNumber = phoneNumberEt.getText().toString().trim();

        if (isEditMode) {

            String timestanp = "" + System.currentTimeMillis();

            dbHelper.UpdateRecord(
                    "" + id,
                    "" + ImageFileUri,
                    " " + name,
                    " " + lastName,
                    "" + email,
                    "" + address,
                    "" + phoneNumber,
                    "" + addedTime,
                    timestanp
            );

            finish();

        } else {
            ///new data

            String timestanp = "" + System.currentTimeMillis();

            //I have to ask why do we have to convert to string
            long id = dbHelper.InsertRecord(
                    "" + ImageFileUri,
                    "" + name,
                    " " + lastName,
                    " " + email,
                    "" + address,
                    "" + phoneNumber,
                    "" + timestanp,
                    "" + timestanp
            );

            finish();
        }
    }

    //Create a menu, so the user can take a picture
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_camera, menu);
        return true;
    }

    //When the menu item is selected, we have to handle a click event
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId(); // Capture the id of the option, in case of having more than one, we can use a switch

        //ED note: A switch is faster tan a if, but in this case, we just have one option

        if (id == R.id.menu_camera) {
            ImageDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    //Is always a good idea to put all the logic in methods, for easier debugging
    private void ImageDialog() {

        //Options of the dialog
        String[] options = {"Camera", "Gallery"};
        //Display dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set title
        builder.setTitle("Pick image from: ");
        builder.setItems(options, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    RequestCameraPermission();
                    ;
                    if (CheckForCameraPermission()) {
                        //Permission is already granted
                        LaunchCamera();
                    }
                case 1:
                    RequestStoragePermission();
                    if (CheckForStoragePermission()) {
                        LaunchGalley();
                    }
            }
            //I forgot to show the dial9og lol
        }).show();

    }

    //Intent for launching the gallery
    private void LaunchGalley() {
        //Intent for launching the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); // We want only images
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    //Intent for launching the camera, the image will be returned in the onActivityResult method
    private void LaunchCamera() {
        //This line will put the picture as extra on our cameraIntent, so that we can grab the URI, and store it io our DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        //Put image in ImageFileUri
        ImageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        //If we o not do this, we will receive null in or uri
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageFileUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // We can navigate back by pressing the button on the action bar
        return true;
    }

    //Because we are in a API that is higher than Marshmallow, we need to ask for permissions;
    private void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void RequestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    //We need to check the result of those permissions;
    private boolean CheckForStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE +
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    //We need to check the result of those permissions;
    private boolean CheckForCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storagePermissions = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && storagePermissions) {
                        LaunchCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storagePermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storagePermissions) {
                        LaunchGalley();
                    } else {
                        Toast.makeText(this, "Storage permissions is required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to detect your location!", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    //We will receive the image from camera or gallery here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            //Image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //This means that we picked a image from gallery
                //I want to crop the image#
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //This means that we picked a image from camera
                //I want to crop the image
                CropImage.activity(ImageFileUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //Cropped imaged received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    ImageFileUri = result.getUri();
                }
                //Finally we can set the image to our ProfileImageView
                profileImage.setImageURI(ImageFileUri);

                CopyFileOrDirectory("" + ImageFileUri.getPath(), "" + getDir("SQLiteRecordImage", MODE_PRIVATE));


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*
    This is for fixing a problem that I have, that every picture that I take inside the app, and I cleared the cache the photos gets deleted
 */
    public void CopyFileOrDirectory(String srcDir, String desDir) {

        try {
            File src = new File(srcDir);
            File des = new File(desDir, src.getName());
            if (src.isDirectory()) {
                String[] files = src.list();
                int filesLength = files.length; // This was going to be used in a for loop
                for (String file : files) {
                    String src1 = new File(src, file).getPath();
                    String des1 = des.getPath();

                    CopyFileOrDirectory(src1, des1);
                }
            } else {
                CopyFile(src, des);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void CopyFile(File srcDir, File desDir) throws IOException {

        if (!desDir.getParentFile().exists()) {
            desDir.mkdirs();
        } else if (!desDir.exists()) {
            desDir.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(srcDir).getChannel();
            destination = new FileOutputStream(desDir).getChannel();
            destination.transferFrom(source, 0, source.size());

            ImageFileUri = Uri.parse(desDir.getPath()); // Image URI
            Log.e(TAG, "CopyFile: " + "ImagePath: " + ImageFileUri);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            //Close Resources
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public void OpenCalendar(View view) {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }
}