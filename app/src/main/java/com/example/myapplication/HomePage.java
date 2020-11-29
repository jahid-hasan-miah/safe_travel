package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private CodeScanner mCodeScanner;
    private CodeScannerView mCodeScannerView;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;


    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference NumberRef;
    String currentUserID, value;

    public CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //currentUserID="0oYhJxB1W7dfP1bt2C9akRgaVml2";
        currentUserID = mAuth.getCurrentUser().getUid();


        drawerLayout = findViewById(R.id.nav_drawer_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar1);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        card1 = (CardView) findViewById(R.id.update_accident);
        card2 = (CardView) findViewById(R.id.connect_device);
        card3 = (CardView) findViewById(R.id.scan_qr);
        card4 = (CardView) findViewById(R.id.emergency_call);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        String fullname = dataSnapshot.child("name").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(HomePage.this).load(image).placeholder(R.drawable.person).into(NavProfileImage);
                    } else {
                        Toast.makeText(HomePage.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }


    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(HomePage.this, SignUp.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(HomePage.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int backButtonCount = 1;
            if (backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.update_accident:
                i = new Intent(this, NewsFeed.class);
                startActivity(i);
                finish();
                break;

            case R.id.connect_device:
                i = new Intent(this, start_trip.class);
                startActivity(i);
                finish();
                break;

            case R.id.scan_qr:
                i = new Intent(this, start_trip.class);
                startActivity(i);
                finish();
                break;

            case R.id.emergency_call:
                String emergencynumber = "999";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", emergencynumber, null));
                startActivity(intent);
                break;

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
            case R.id.nav_profile:
                SendUserToProfileActivity();
                finish();
                break;
            case R.id.nav_addFamily:
                SendUserToFindFriendActivity();
                break;
            case R.id.nav_trip_info:
                SendUserToTripInfo();
                break;

            case R.id.nav_family_journey:
                SendUserToFamilyJourney();
                break;
            case R.id.nav_home:
                SendUserToHomepage();
                break;
        }
        return true;
    }

    private void SendUserToHomepage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    private void SendUserToFamilyJourney() {
        Intent intent = new Intent(this, FamilyTripInfo.class);
        startActivity(intent);
    }

    private void SendUserToTripInfo() {
        Intent intent = new Intent(this, TripInfo.class);
        intent.putExtra("visit_user_id", currentUserID);
        startActivity(intent);
    }

    private void SendUserToProfileActivity() {
        Intent i = new Intent(HomePage.this, profile.class);
        startActivity(i);
    }

    private void SendUserToFindFriendActivity() {
        Intent i = new Intent(HomePage.this, FindFriends.class);
        startActivity(i);
    }
}
