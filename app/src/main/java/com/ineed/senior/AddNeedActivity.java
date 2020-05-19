package com.ineed.senior;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddNeedActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference databaseReference;
    EditText description, locationInfo;
    Spinner needType;
    Switch locationSwitch;
    LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_add_need);

        description = findViewById(R.id.add_need_description);
        locationInfo = findViewById(R.id.locationInfo);
        locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setChecked(true);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationInfo.setVisibility(View.INVISIBLE);
                    locationInfo.setHeight(0);
                }
                else {
                    locationInfo.setVisibility(View.VISIBLE);
                    locationInfo.setHeight(500);
                }
            }
        });



        needType = findViewById(R.id.add_need_spinner);
        needType.setPrompt("Select Need Type");
        user = FirebaseAuth.getInstance().getCurrentUser();
       // secondaryDBref = MainActivity.getSecondaryDB().getReference();
        //location = getIntent().getExtras().getString("location");
        currentLatLng = new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendPost(view, currentLatLng);
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void sendPost(View view, LatLng currentLatLng) throws IOException {
        MyNeed newNeed = new MyNeed(description.getText().toString(), user.getEmail(), getAddress(currentLatLng),
                needType.getSelectedItem().toString(), currentLatLng.latitude, currentLatLng.longitude, false);

        databaseReference.child("needs").push().setValue(newNeed);

        Snackbar snackbar = Snackbar.make(view, "Your call for help has been posted!", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.addCallback(new Snackbar.Callback() {
           @Override
           public void onDismissed(Snackbar snackbar, int event){
               finish();
           }
        });
        snackbar.show();

        description.setText("");
    }


    private String getAddress(LatLng latlng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        /*
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else

         */
        return addresses.get(0).getAddressLine(0);
    }



}
