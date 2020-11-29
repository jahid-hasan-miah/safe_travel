package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission;


public class start_trip extends AppCompatActivity implements LocationListener, OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    public static EditText editText;
    Button button;

    private static final String TAG = "Find route";
    int PROXIMITY_RADIUS = 10000;
    private static final float DEFAULT_ZOOM = 2f;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private MapView mapView;
    private SupportMapFragment mapFragment;
    private Location last_location;
    private Marker current_location_marker,destination_marker;
    MarkerOptions placestart, placedestination;
    private Handler mainHandler = new Handler();

    public static final int GPS_REQUEST_CODE = 9003;
    public static final int REQUEST_PERMISSION_CODE = 9001;
    private boolean locationPermissionGranted;


    Button btn_currentLocation,destinition,start_journey;
    EditText cuurent_location,destinition_location;
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private double longitude;
    private double latitude;

    private FirebaseAuth mAuth;
    private DatabaseReference JourneyRef,UsersRef,FriendRef;
    private String PassengerID, FriendUserID,CURRENT_STATE,saveCurrentDate ;
    private String saveCurrentTime;
    private String postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        editText = (EditText) findViewById(R.id.result);
        button = (Button) findViewById(R.id.button);

        btn_currentLocation = findViewById(R.id.btn_current_location);
        cuurent_location = findViewById(R.id.current_location);
        destinition =(Button) findViewById(R.id.destinition);
        start_journey =(Button) findViewById(R.id.startTrip);
        destinition_location =(EditText) findViewById(R.id.destinition_loc);

        mAuth = FirebaseAuth.getInstance();
        PassengerID=mAuth.getCurrentUser().getUid();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        JourneyRef= FirebaseDatabase.getInstance().getReference().child("Trips");




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentformap);
        mapFragment.getMapAsync(start_trip.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Thread GetLastLocationThread = new Thread(new GetLocationRunable(fusedLocationClient));
        GetLastLocationThread.start();



        btn_currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(start_trip.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(start_trip.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                }
                getLocation();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), scan_qr.class));
            }
        });
        destinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SearchDestnition();

            }
        });


        start_journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartJourney();

            }
        });

    }

    private void StartJourney() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        UsersRef.child(PassengerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("name").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String car_number= editText.getText().toString().trim();
                    String from= cuurent_location.getText().toString().trim();
                    String to= destinition_location.getText().toString().trim();

                    HashMap userMap=new HashMap();
                    userMap.put("carnumber",car_number);
                    userMap.put("uid",PassengerID);
                    userMap.put("journeystart",from);
                    userMap.put("destinition",to);
                    userMap.put("time", saveCurrentTime);
                    userMap.put("name", userFullName);
                    userMap.put("profileimage", userProfileImage);
                    JourneyRef.child(PassengerID).updateChildren(userMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        OpenTripInfo();
                                        Toast.makeText(start_trip.this,"Journey Started",Toast.LENGTH_LONG).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(start_trip.this, "Error Occured while updating your Journey.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void OpenTripInfo() {
        Intent intent = new Intent(start_trip.this,TripInfo.class);
        intent.putExtra("visit_user_id",PassengerID);
        startActivity(intent);
    }

    private void SearchDestnition() {

        String location = destinition_location .getText().toString();
        List<Address> addressList;


        if(!location.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(location, 1);

                if(addressList != null)
                {

                    for(int i = 0;i<addressList.size();i++) {
                        mGoogleMap.clear();
                        LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);
                        markerOptions.title(location);
                        mGoogleMap.addMarker(markerOptions);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,start_trip.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initGoogleMap() {

        if(isServiceOk()){
            if(isGPSEnabled()){
                if(CheckLocationPermission()){
                    mapView.getMapAsync(this);

                }
                else {
                    requestLocationPermission();
                }
            }
        }
    }

    private void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION_CODE);
            }

        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(providerEnabled){
            return true;
        }
        else {
            AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("Please Enabled Gps for this Service")
                    .setPositiveButton("Yes",((dialogInterface,i)->{
                        Intent intent=new Intent((Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        startActivityForResult(intent,GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();



        }
        return false;
    }

    private boolean isServiceOk() {
        if(locationPermissionGranted){
            Toast.makeText(this,"Map is Ready",Toast.LENGTH_SHORT).show();
        }else{
            requestLocationPermission();
        }
        return false;
    }
    private boolean CheckLocationPermission() {
        return ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            if(current_location_marker!=null){
                current_location_marker.remove();
            }
            last_location=location;
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            current_location_marker=mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
           // mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(12));

            Geocoder geocoder = new Geocoder(start_trip.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            cuurent_location.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

       // mGoogleMap=googleMap;
       // bulidGoogleApiClient();
       // mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);



    }

    class GetLocationRunable implements Runnable {
        FusedLocationProviderClient fusedLocationProviderClient;

        public GetLocationRunable(FusedLocationProviderClient fusedLocationProviderClient) {
            this.fusedLocationProviderClient = fusedLocationProviderClient;
        }

        @Override
        public void run() {
            getLastLocation(fusedLocationProviderClient);
        }

        private void getLastLocation(FusedLocationProviderClient flpc) {
            Log.d(TAG, "getLastLocation: called");
            if (ActivityCompat.checkSelfPermission(start_trip.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(start_trip.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            flpc.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            if (location == null) {
                                Log.d(TAG, "onSuccess: loqcation is null");
                                getLastLocation(fusedLocationClient);
                            } else {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        last_location = location;
                                        //   moveCamera(location, "getLastLocation");
                                        yourlocation(location, "getYourLocation");

                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: error=" + e.getMessage());
                        }
                    });
        }
    }
    public void yourlocation(Location location, String caller) {
        Log.d(TAG, "getlocation: called by " + caller);

        Geocoder geocoder = new Geocoder(start_trip.this, Locale.getDefault());
        try {
            if(current_location_marker!=null){
                current_location_marker.remove();
            }
            last_location=location;
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            current_location_marker=mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addressList.get(0).getAddressLine(0);
            cuurent_location.setText( address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GPS_REQUEST_CODE){
            LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(providerEnabled){
                Toast.makeText(this,"GPS is Enabled",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"GPS not Enabled",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(start_trip.this,HomePage.class);
        startActivity(intent);
        finish();
    }




}
