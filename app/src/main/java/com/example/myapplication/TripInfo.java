package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TripInfo extends AppCompatActivity {

    private ImageView profilePic;
    private TextView username,current_loc,destinition_loc,car_number;
    private DatabaseReference TripRef;
    private String currentUserID,receiverUserID,accident_car;
    private FirebaseAuth mAuth;
    private Button reach,home,call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        mAuth = FirebaseAuth.getInstance();
        TripRef = FirebaseDatabase.getInstance().getReference().child("Trips");
        currentUserID = mAuth.getCurrentUser().getUid();
        receiverUserID=getIntent().getExtras().get("visit_user_id").toString();

       // accident_car=getIntent().getExtras().get("car_number").toString();


        profilePic=(ImageView)findViewById(R.id.profileImage);
        username = (TextView) findViewById(R.id.profilename);
        current_loc = (TextView) findViewById(R.id.fromLocation);
        destinition_loc = (TextView) findViewById(R.id.toLocation);
        car_number = (TextView) findViewById(R.id.car_num);

        reach=findViewById(R.id.reach_des);
        home=findViewById(R.id.goHome);
        call=findViewById(R.id.urgent_call);

        TripRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        String fullname = dataSnapshot.child("name").getValue().toString();
                        username.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("journeystart"))
                    {
                        String from = dataSnapshot.child("journeystart").getValue().toString();
                        current_loc.setText(from);
                    }
                    if(dataSnapshot.hasChild("destinition"))
                    {
                        String to = dataSnapshot.child("destinition").getValue().toString();
                        destinition_loc.setText(to);
                    }
                    if(dataSnapshot.hasChild("carnumber"))
                    {
                        String num = dataSnapshot.child("carnumber").getValue().toString();
                        //if(num==accident_car){
                           // car_number.setText(num+"Occur Accident");
                      //  }else{
                            car_number.setText(num);
                        //}

                    }
                    if(dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(TripInfo.this).load(image).placeholder(R.drawable.person).into(profilePic);
                    }
                    else
                    {
                        Toast.makeText(TripInfo.this, "This Person Is not having Journey", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(TripInfo.this,HomePage.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!currentUserID.equals(receiverUserID)){
            reach.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);
        }else{

        }

        reach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndTrip();

            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToHomePage();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                String emergencynumber="999";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", emergencynumber, null));
                startActivity(intent);

            }
        });


    }

    private void EndTrip() {
        TripRef.child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendToHomePage();
                Toast.makeText(TripInfo.this,"You are Safely Reached,Now you can start your new Journey",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendToHomePage() {
        Intent intent=new Intent(this,HomePage.class);
        startActivity(intent);
    }
}
