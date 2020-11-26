package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PersonalInfo extends AppCompatActivity {

    private TextView name,email,contact;
    private Button send_req,cancel_req;
    private ImageView profilePic;

    private FirebaseAuth mAuth;
    private DatabaseReference FriendRequestRef,UsersRef,FriendRef;
    private String senderUserID, receiverUserID,CURRENT_STATE,saveCurrentDate ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();

        receiverUserID=getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef=FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        name=findViewById(R.id.user_name);
        email=findViewById(R.id.user_email);
        contact=findViewById(R.id.user_contact);
        send_req=findViewById(R.id.send_request);
        cancel_req=findViewById(R.id.cancel_request);
        profilePic=findViewById(R.id.profile_pic);

        CURRENT_STATE="not_friends";


        UsersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                        String fullname = dataSnapshot.child("name").getValue().toString();
                        name.setText(fullname);
                        String person_email = dataSnapshot.child("email").getValue().toString();
                        email.setText(person_email);
                        String person_contact = dataSnapshot.child("contact").getValue().toString();
                        contact.setText(person_contact);
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(PersonalInfo.this).load(image).placeholder(R.drawable.person).into(profilePic);

                        MaintainanceOfButton();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cancel_req.setVisibility(View.INVISIBLE);
        cancel_req.setEnabled(true);

        if(!senderUserID.equals(receiverUserID)){
            send_req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    send_req.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnfriendExistingFriend();
                    }
                }
            });

        }else {
            send_req.setVisibility(View.INVISIBLE);
            cancel_req.setVisibility(View.INVISIBLE);
        }

    }

    private void UnfriendExistingFriend() {
        FriendRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                send_req.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                send_req.setText("Send Friend Request");
                                                cancel_req.setVisibility(View.INVISIBLE);
                                                cancel_req.setEnabled(false);

                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void AcceptFriendRequest() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        FriendRef.child(senderUserID).child(receiverUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRef.child(receiverUserID).child(senderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                FriendRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        send_req.setEnabled(true);
                                                                                        CURRENT_STATE="friends";
                                                                                        send_req.setText("Unfriend");
                                                                                        cancel_req.setVisibility(View.INVISIBLE);
                                                                                        cancel_req.setEnabled(false);

                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                send_req.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                send_req.setText("Send Friend Request");
                                                cancel_req.setVisibility(View.INVISIBLE);
                                                cancel_req.setEnabled(false);

                                            }

                                        }
                                    });
                        }

                    }
                });


    }

    private void MaintainanceOfButton() {
        FriendRequestRef.child(senderUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){
                            String request_type=dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                CURRENT_STATE="request_sent";
                                send_req.setText("Cancel Friend Request");
                                cancel_req.setVisibility(View.INVISIBLE);
                                cancel_req.setEnabled(false);
                            }
                            else if(request_type.equals("received")){
                                CURRENT_STATE="request_received";
                                send_req.setText("Accept Friend Request");
                                cancel_req.setVisibility(View.VISIBLE);
                                cancel_req.setEnabled(true);

                                cancel_req.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else {
                            FriendRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.hasChild(receiverUserID)){
                                               CURRENT_STATE="friends";
                                               send_req.setText("Unfriend");
                                               cancel_req.setVisibility(View.INVISIBLE);
                                               cancel_req.setEnabled(false);

                                           }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToPerson() {
        FriendRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                send_req.setEnabled(true);
                                                CURRENT_STATE="request_sent";
                                                send_req.setText("Cancel Friend Request");
                                                cancel_req.setVisibility(View.INVISIBLE);
                                                cancel_req.setEnabled(false);

                                            }

                                        }
                                    });
                        }

                    }
                });

    }
}
