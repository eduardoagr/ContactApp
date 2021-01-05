package com.example.practica2eduardogomez;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import java.io.BufferedReader;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.HolderContacts> {

    private static final String TAG = "adapter" ;
    Context context;
    ArrayList<Contact> contactArrayList;
    DbHelper dbHelper;
    //Constructor
    public CustomAdapter(Context context, ArrayList<Contact> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;

        dbHelper = new DbHelper(context);
    }

    //Here we just need to inflate our new created layout
    @NonNull
    @Override
    public HolderContacts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new HolderContacts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderContacts holder, int position) {
        //Here we want to get everything from our model (class)
        Contact contactModel = contactArrayList.get(position);

        String id = contactModel.getId();
        String image = contactModel.getImage();
        String name = contactModel.getName();
        String lastName = contactModel.getLastName();
        String email = contactModel.getEmail();
        String address = contactModel.getAddress();
        String phoneNumber = contactModel.getPhoneNumber();
        String addedTime = contactModel.getAddedTime();
        String updatedTime = contactModel.getUpdateTime();

        if (image.equals("null")){
            holder.profileImage.setImageResource(R.drawable.ic_baseline_person_24);
        }else{
            holder.profileImage.setImageURI(Uri.parse(image));
        }
        holder.Name.setText(name + "" + lastName);
        holder.phone.setText(phoneNumber);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("detailID", id);
            context.startActivity(intent);

        });
        holder.MoreBtn.setOnClickListener(view -> {

            //Know is time for edit and delete in our db

            //Is always better to work with sitings

            ShowMore(
                    ""+position,
                    ""+id,
                    ""+image,
                    ""+name,
                    ""+lastName,
                    ""+email,
                    ""+address,
                    ""+phoneNumber,
                    ""+addedTime,
                    ""+updatedTime
            );

            Log.e(TAG, "onBindViewHolder: " + image);
        });
    }

    //I do not understand anything, so I will change the parameters
    private void ShowMore(String position, String id, String image, String name,
                          String lastName, String email, String address, String phoneNumber, String addedTime, String updatedTime) {


        String [] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(options, (dialogInterface, i) -> {

            switch (i){
                case 0:
                    //Edit
                    Intent intent = new Intent(context, AddUpdateContactActivity.class);
                    intent.putExtra("ID", id);
                    intent.putExtra("IMAGE", image);
                    intent.putExtra("NAME", name);
                    intent.putExtra("LAST_NAME", lastName);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("ADDRESS", address);
                    intent.putExtra("PHONE_NUMBER", phoneNumber);
                    intent.putExtra("ADDED_TIME", addedTime);
                    intent.putExtra("UPDATE_TIME", updatedTime);
                    intent.putExtra("isEditMode", true);

                    context.startActivity(intent);

                    break;

                case 1:
                        //Delete

                    dbHelper.DeleteData(id);

                    //I want to call onResume on the MainActivity
                    ((MainActivity)context).onResume();
            }
        }).show();
    }

    //Here we are going to return the number of row, but only if is not empty
    @Override
    public int getItemCount() {
        if (contactArrayList.size() > 0){
            return contactArrayList.size();
        }
        return 0;
    }

    static class HolderContacts extends RecyclerView.ViewHolder {

        CircularImageView profileImage;
        TextView Name, phone;
        ImageButton MoreBtn;

        public HolderContacts(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.row_circularImageView);
            Name = itemView.findViewById(R.id.row_name);
            phone = itemView.findViewById(R.id.row_phone_number);
            MoreBtn = itemView.findViewById(R.id.row_moreBtn);
        }
    }
}
