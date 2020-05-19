package com.ineed.senior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import java.util.ArrayList;

public class Resulsts extends AppCompatActivity {
    // Crete an array to hold the filtered values
    ArrayList<Need> filteredArrayList;
    // Create an array to hold the incoming Needs array
    ArrayList<Need> needArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulsts);
        // Get and assign the Need array inside intent bundle
        needArrayList=(ArrayList<Need>)getIntent().getSerializableExtra("needs");


        // Get and assing the reference to the recylerView and editTextView
        final RecyclerView resultsView=(RecyclerView)findViewById(R.id.recylerview_favorite);
        final TextView filterText=(TextView)findViewById(R.id.textview_filter);

        // Assing a new aaraylist to the filter array
      filteredArrayList =new ArrayList<>();

      // Create and assingn an Adapter to the recyler view with the Need array
       resultsView.setLayoutManager(new LinearLayoutManager(this));
       RecylerNeedAdapter adapter = new RecylerNeedAdapter(needArrayList);
       resultsView.setAdapter(adapter);

       // Set an eventlistener for editTextView to get the key to filter
       filterText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               // Call the filter method
               filterResults(s.toString());
               // Create and assign a new adapter to the recylerView the filtered need array
               resultsView.setAdapter(new RecylerNeedAdapter(filteredArrayList));
           }

           @Override
           public void afterTextChanged(Editable s) {



           }
       });

    }

    void  filterResults(String s){
        // Clear the filtered array so start from beginning
        filteredArrayList.clear();

        // Loop over the Need array
        for(Need n:needArrayList){
            // chech if th title or the user of any elemet of the Need array contains the filter key
            if(n.getmTitle().trim().toLowerCase().contains(s.trim().toLowerCase()) || n.getmUser().trim().toLowerCase().contains(s.trim().toLowerCase()))
                // Add the fons elements to the filter array
                filteredArrayList.add(n);
        }



    }

}
