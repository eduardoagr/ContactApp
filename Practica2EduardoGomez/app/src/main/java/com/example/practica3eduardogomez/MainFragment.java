package com.example.practica3eduardogomez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica3eduardogomez.listeners.OnClickAdapterListener;
import com.example.practica3eduardogomez.listeners.OnClickMainListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements OnClickAdapterListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView mContacts;
    DbHelper dbHelper;

    String orderByNewest = DatabaseConstants.C_ADDED_TIMESTAMP + " DESC";
    String orderByOldest = DatabaseConstants.C_ADDED_TIMESTAMP + " ASC";
    String orderByNameAsc = DatabaseConstants.C_NAME + " ASC";
    String orderByNameDesc = DatabaseConstants.C_NAME + " DESC ";

    // We have to save the current order status
    String currentOrderState = orderByNewest;
    private OnClickMainListener listener;

    public MainFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContacts = view.findViewById(R.id.mainRV);

        init(view);
    }

    public void init(View view) {
        //New way of doing a click listener
        view.findViewById(R.id.main_addRecordBtn).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddUpdateContactActivity.class);
            intent.putExtra("isEditMode",false); // We jest want to modify if we tap on the more button
            startActivity(intent);
        });

        dbHelper = new DbHelper(getContext());

        //By default is newest first
        LoadRecords(orderByNewest);
    }

    public void LoadRecords(String orderBy) {
        currentOrderState = orderBy;
        CustomAdapter customAdapter = new CustomAdapter(getContext(), dbHelper.GetAllContacts(orderBy), this);
        mContacts.setAdapter(customAdapter);
    }

    public void SearchDatabase(String query) {
        CustomAdapter customAdapter = new CustomAdapter(getContext(), dbHelper.SearchContacts(query), this);
        mContacts.setAdapter(customAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (OnClickMainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void DataTransfer(String id) {
        listener.transferId(id);
    }
}