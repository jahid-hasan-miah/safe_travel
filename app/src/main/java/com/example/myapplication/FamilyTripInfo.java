package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyTripInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    private RecyclerView tripList;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, TripRef, FriendRef;
    String currentUserID, friendUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_trip_info);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);

        TripRef = FirebaseDatabase.getInstance().getReference().child("Trips");
        friendUserID = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID).toString();


        tripList = (RecyclerView) findViewById(R.id.all_user_post_list);
        tripList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tripList.setLayoutManager(linearLayoutManager);


        drawerLayout = findViewById(R.id.nav_drawer_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar1);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(FamilyTripInfo.this);

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
                        Picasso.with(FamilyTripInfo.this).load(image).placeholder(R.drawable.person).into(NavProfileImage);
                    } else {
                        Toast.makeText(FamilyTripInfo.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        JourneyInfo();

    }

    private void JourneyInfo() {
        FirebaseRecyclerAdapter<trip, allTripViewHolder> RecyclerAdapter =
                new FirebaseRecyclerAdapter<trip, allTripViewHolder>
                        (
                                trip.class,
                                R.layout.trip_profile,
                                allTripViewHolder.class,
                                FriendRef

                        ) {

                    @Override
                    protected void populateViewHolder(allTripViewHolder viewHolder, trip model, int i) {
                        final String userID = getRef(i).getKey();
                        TripRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final String Time = dataSnapshot.child("time").getValue().toString();
                                    viewHolder.setTime("Started Journey at " + Time);

                                } else {
                                    final String Time = "This Person is not In Journey";
                                    viewHolder.setTime(Time);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //final String userID=getRef(i).getKey();
                        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final String username = dataSnapshot.child("name").getValue().toString();
                                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();

                                    viewHolder.setName(username);
                                    viewHolder.setProfileimage(getApplicationContext(), profileImage);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(i).getKey();

                                Intent intent = new Intent(FamilyTripInfo.this, TripInfo.class);
                                intent.putExtra("visit_user_id", visit_user_id);
                                startActivity(intent);
                            }
                        });
                    }
                };
        tripList.setAdapter(RecyclerAdapter);

    }

    public static class allTripViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public allTripViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.user_profile_pic);
            Picasso.with(ctx).load(profileimage).into(image);
        }

        public void setName(String name) {
            TextView username = (TextView) mView.findViewById(R.id.user_profile_name);
            username.setText(name);
        }

        public void setTime(String time) {
            TextView juouneyTime = (TextView) mView.findViewById(R.id.journey_time);
            juouneyTime.setText(time);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
            case R.id.nav_addFamily:
                SendUserToFindFriendActivity();
                break;
            case R.id.nav_profile:
                SendUserToProfileActivity();
                finish();
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
        startActivity(intent);
    }

    private void SendUserToFindFriendActivity() {
        Intent i = new Intent(FamilyTripInfo.this, FindFriends.class);
        startActivity(i);
    }

    private void SendUserToProfileActivity() {
        Intent i = new Intent(FamilyTripInfo.this, profile.class);
        startActivity(i);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(FamilyTripInfo.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }


}
