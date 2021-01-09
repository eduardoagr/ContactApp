package com.example.practica3eduardogomez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    RecyclerView mContacts;
    DbHelper dbHelper;

    //sort queries

    //This is just to remember the path for backup
    private static final String BACKUP_DATABASE_PATH = "SQLLiteBackup";
    private static final String CSV_NAME = "SQLLite_Backup.csv";

    String orderByNewest = DatabaseConstants.C_ADDED_TIMESTAMP + " DESC";
    String orderByOldest = DatabaseConstants.C_ADDED_TIMESTAMP + " ASC";
    String orderByNameAsc = DatabaseConstants.C_NAME + " ASC";
    String orderByNameDesc = DatabaseConstants.C_NAME + " DESC ";

    // We have to save the current order status
    String currentOrderState = orderByNewest;

    private static final int STORAGE_IMPORT = 50;
    private static final int STORAGE_EXPORT = 60;

    String [] StoragePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContacts = findViewById(R.id.mainRV);

        StoragePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        dbHelper = new DbHelper(this);

        //By default is newest first
        LoadRecords(orderByNewest);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("All Records"); //Setting the title
        getSupportActionBar().setSubtitle("Total records: " + dbHelper.GetRecordsCount());


        //New way of doing a click listener
        findViewById(R.id.main_addRecordBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, AddUpdateContactActivity.class);
            intent.putExtra("isEditMode",false); // We jest want to modify if we tap on the more button
            startActivity(intent);
        });
    }

    private void LoadRecords(String orderBy) {
        currentOrderState = orderBy;
        CustomAdapter customAdapter = new CustomAdapter(this, dbHelper.GetAllContacts(orderBy));
        mContacts.setAdapter(customAdapter);
    }

    public boolean CheckStoragePermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestStoragePermissionImport(){
        ActivityCompat.requestPermissions(this, StoragePermissions, STORAGE_IMPORT);
    }

    public void RequestStoragePermissionExport(){
        ActivityCompat.requestPermissions(this, StoragePermissions, STORAGE_EXPORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadRecords(currentOrderState); //This will reload the date
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actions, menu);

        //Here we need to instantiate out SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Search when keyboard clicked
                SearchDatabase(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchDatabase(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_sort){
            //Sort options
            Sorting();
        }else if (id == R.id.action_delete){
            dbHelper.DeleteAllData();
            onResume();
        //Because we want to make a backup and be able abbe to restore, we need STORAGE PERMISSION
        }else if (id == R.id.action_backup) {
            if(CheckStoragePermission()) {
                //Permission allowed
                ExportCSV();
            }else{
                RequestStoragePermissionExport();
            }
        }
        else if (id == R.id.action_restore){
            if(CheckStoragePermission()) {
                //Permission allowed
                ImportCSV();
                onResume();
            }else{
                RequestStoragePermissionImport();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void Sorting() {

        String [] options = {"Contact ascending","Contact Descending","Newest","Oldest"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Order by");

        builder.setItems(options, (dialogInterface, i) -> {

            switch (i){
                case 0:
                    LoadRecords(orderByNameAsc);
                    break;
                case 1:
                    LoadRecords(orderByNameDesc);
                    break;
                case 2:
                    LoadRecords(orderByNewest);
                    break;
                case 3:
                    LoadRecords(orderByOldest);
                    break;
            }
        }).show();
    }

    private void SearchDatabase(String query) {
        CustomAdapter customAdapter = new CustomAdapter(this, dbHelper.SearchContacts(query));
        mContacts.setAdapter(customAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_IMPORT:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ImportCSV();
                }
                break;
            case STORAGE_EXPORT:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   ExportCSV();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void ExportCSV() {

        File folder = new File(Environment.getExternalStorageDirectory() + "/" + BACKUP_DATABASE_PATH);

        boolean isCreated = false;
        if (!folder.exists()){
            isCreated = folder.mkdirs();
        }
        Log.e(TAG, "ExportCSV: " +isCreated);

        //create file
        String FilePathAAndName = folder.toString() + "/" + CSV_NAME;
        //Now we need to get records
        ArrayList<Contact> contactArrayList = new ArrayList<>();
        //I now is empty, but to make sure
        contactArrayList.clear();
        contactArrayList = dbHelper.GetAllContacts(orderByOldest);

        try {
            FileWriter fw = new FileWriter(FilePathAAndName);
            for (int i = 0; i <contactArrayList.size() ; ++i) {
                fw.append(""+contactArrayList.get(i).getId()); // 0
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getImage()); // 1
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getName()); // 2
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getLastName()); // 3
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getEmail()); // 4
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getAddress()); // 5
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getPhoneNumber()); // 6
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getAddedTime()); // 7
                fw.append(",");
                fw.append(""+contactArrayList.get(i).getUpdateTime()); // 8
                fw.append("\n");
            }

            fw.flush();
            fw.close();

            Toast.makeText(this, "Backup exported to: "+FilePathAAndName, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void ImportCSV() {
        //We are going to use the same path and file name to import

        String FilePathAAndName = Environment.getExternalStorageDirectory()+ "/" + BACKUP_DATABASE_PATH + "/" + CSV_NAME;

        File cvsFile = new File(FilePathAAndName);

        if (cvsFile.exists()){
            //We need to import with a external library
            try {
                CSVReader csvReader = new CSVReader(new FileReader(cvsFile.getAbsolutePath()));
                String [] nextLine;
                while ((nextLine = csvReader.readNext()) != null) {
                    //Is important to do this, in the same order
                    String ids = nextLine[0];
                    String image = nextLine[1];
                    String names = nextLine[2];
                    String lastnames = nextLine[3];
                    String email = nextLine[4];
                    String address = nextLine[5];
                    String phoneNumbers = nextLine[6];
                    String addedTimes = nextLine[7];
                    String updatedTimes = nextLine[8];

                    long timeStamp = System.currentTimeMillis();
                    long id = dbHelper.InsertRecord(
                            "" + image,
                            "" + names,
                            " " + lastnames,
                            " " + email,
                            "" + address,
                            "" + phoneNumbers,
                            "" + addedTimes,
                            "" + updatedTimes
                    );
                }
            }catch (Exception exception){

                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();

            }


        }else{
            Toast.makeText(this, "No backup backup", Toast.LENGTH_SHORT).show();
        }

    }


}