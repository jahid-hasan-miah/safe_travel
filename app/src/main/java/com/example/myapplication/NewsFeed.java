package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class NewsFeed extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {


    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    private RecyclerView postList;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostsRef;
    String currentUserID;

    private Button addPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Button addPost=(Button)findViewById(R.id.add_post);

        mAuth = FirebaseAuth.getInstance();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        currentUserID = mAuth.getCurrentUser().getUid();

        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);



        drawerLayout = findViewById(R.id.nav_drawer_main);
        navigationView =(NavigationView) findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar1);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(NewsFeed.this);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        String fullname = dataSnapshot.child("name").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(NewsFeed.this).load(image).placeholder(R.drawable.person).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(NewsFeed.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsFeed.this,createPost.class);
                startActivity(intent);
            }
        });
        DisplayAllUsersPosts();
    }

    private void DisplayAllUsersPosts() {

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_post,
                                PostsViewHolder.class,
                                PostsRef
                        )
                {

                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
                    {
                        viewHolder.setName(model.getName());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setCarnumber(model.getCarnumber());
                        viewHolder.setAccidentspot(model.getAccidentspot());
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(name);
        }

        public void setProfileimage(Context ctx, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(image);
        }


        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.time);
            PostTime.setText(time);
        }


        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.date);
            PostDate.setText(date);
        }

        public void setCarnumber(String carnumber)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.car_number);
            PostDescription.setText(carnumber);
        }
        public void setAccidentspot(String accidentspot)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(accidentspot);
        }

        public void setPostimage(Context ctx1,  String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx1).load(postimage).into(PostImage);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(NewsFeed.this, MainActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
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
        Intent intent=new Intent(this,HomePage.class);
        startActivity(intent);
    }

    private void SendUserToFamilyJourney() {
        Intent intent=new Intent(this,FamilyTripInfo.class);
        startActivity(intent);
    }

    private void SendUserToTripInfo() {
        Intent intent=new Intent(this,TripInfo.class);
        startActivity(intent);
    }

    private void SendUserToFindFriendActivity() {
        Intent i = new Intent(NewsFeed.this, FindFriends.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(NewsFeed.this,HomePage.class);
            startActivity(intent);
            finish();
        }
    }
    private void SendUserToProfileActivity() {
        Intent i = new Intent(NewsFeed.this, profile.class);
        startActivity(i);
    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(NewsFeed.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }
}
