package com.ineed.senior.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.ineed.senior.LookAroundActivity;
import com.ineed.senior.MyNeed;
import com.ineed.senior.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AppBarConfiguration mAppBarConfiguration;

    private String[] suggestions = new String[] {
            "Ankara", "Sakarya"
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });



        final ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, suggestions);
        final AutoCompleteTextView searchField = (AutoCompleteTextView) root.findViewById(R.id.searchField);
        searchField.setAdapter(searchAdapter);

        FloatingActionButton searchBtn = root.findViewById(R.id.searchBtnHome);

        ImageButton addButton = root.findViewById(R.id.add_button);


        // Create and get an instance variable  and assign the FirebaseDatabase object to it
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lookAround = new Intent(getActivity(), LookAroundActivity.class);
                startActivity(lookAround);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showMap = new Intent(v.getContext(), LookAroundActivity.class);
                showMap.putExtra("mode", "search");
                showMap.putExtra("key", searchField.getText()+"".toLowerCase());
                startActivity(showMap);
            }
        });

        return root;
    }



}