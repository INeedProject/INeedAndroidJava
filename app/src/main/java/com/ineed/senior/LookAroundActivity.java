package com.ineed.senior;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LookAroundActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Context context;
    private ArrayList<MyNeed> allNeeds = new ArrayList<>();
    private ArrayList<Offer> allOffers = new ArrayList<>();
    private ArrayList<String> needHashes = new ArrayList<>();
    DatabaseReference needsRef = FirebaseDatabase.getInstance().getReference("needs");
    DatabaseReference offersRef = FirebaseDatabase.getInstance().getReference("offers");
    FloatingActionButton addNeed;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_around);

        context = this;

        addNeed = findViewById(R.id.add_from_map_button);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addNeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(context, AddNeedActivity.class);
                add.putExtra("latitude", mLastLocation.getLatitude());
                add.putExtra("longitude", mLastLocation.getLongitude());
                startActivity(add);
            }
        });

        needsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot need : dataSnapshot.getChildren()){
                    MyNeed aNeed = need.getValue(MyNeed.class);
                    if (isSearchMode()) {
                        if (isAMatch(aNeed, getIntent().getStringExtra("key")))
                            storeNeed(need.getKey(), aNeed);
                    }
                    else
                        storeNeed(need.getKey(), aNeed);
                }
                updateNeedMarkers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (isSearchMode()){
            addNeed.setVisibility(View.INVISIBLE);
        }


        fetchOffers();
    }


    private void fetchOffers(){
        offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot offerRetrieved : dataSnapshot.getChildren()){
                    allOffers.add(offerRetrieved.getValue(Offer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMarkerClickListener(this);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.9091513, 32.8648532)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void storeNeed(String hash, MyNeed need){
        needHashes.add(hash);
        allNeeds.add(need);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isSearchMode())
            return;
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
       // MarkerOptions markerOptions = new MarkerOptions();
        // markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        // mCurrLocationMarker = mMap.addMarker(markerOptions);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    private void updateNeedMarkers(){
        for (MyNeed need : allNeeds){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(need.getLatitude(), need.getLongitude()));
            markerOptions.title(need.getDesc());
            // display user's own need markers in blue color
            if (need.getEmail().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(need);
        }
        if (isSearchMode()){
            try {
                MyNeed last = allNeeds.get(allNeeds.size() - 1);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(last.getLatitude(), last.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            } catch (Exception e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(39.9091513, 32.8648532)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
            if (allNeeds.isEmpty())
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Results", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(Marker marker) {

       // Integer clickCount = (Integer) marker.getTag();

       /*
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Snackbar.make(view, marker.get, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }*/

       MyNeed need = (MyNeed) marker.getTag();
       if (!need.getEmail().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
           showNeedDialog(need);


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void showNeedDialog(final MyNeed need){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Display Need description
        builder.setMessage(need.getDesc());

        // Add the buttons
        builder.setPositiveButton("Help " + need.getEmail().split("@")[0], new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!need.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    if (offerAlreadyExists(needHashes.get(allNeeds.indexOf(need)),
                            FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        Snackbar.make(getWindow().getDecorView().getRootView(), "You have already offered to help for this need"
                                , Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    else {
                        sendOffer(new Offer(need.getEmail(), needHashes.get(allNeeds.indexOf(need)),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                false));
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Your offer has been posted." +
                                        " Wait for a message from " + need.getEmail()
                                , Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Display the AlertDialog
        builder.create().show();
    }

    private boolean offerAlreadyExists(String needHash, final String offerer){
        for (Offer offer : allOffers){
            if (offer.getNeedhash().equalsIgnoreCase(needHash)
                    && offer.getOfferer().equalsIgnoreCase(offerer))
                return true;
        }
        return false;
    }

    private void sendOffer(Offer offer){
        offersRef.push().setValue(offer);
    }

    private void startChat(String otherUser){
        Intent chat = new Intent(LookAroundActivity.this, ChatActivity.class);
        chat.putExtra("receiverEmail", otherUser);
        startActivity(chat);
    }

    private boolean isSearchMode(){
        try {
            if (getIntent().getStringExtra("mode").equals("search"))
                return true;
        } catch (NullPointerException n ) {
            return false;
        }
        return false;
    }

    private boolean isAMatch(MyNeed need, String key){
        return need.toString().toLowerCase().contains(key.toLowerCase());
    }
}
