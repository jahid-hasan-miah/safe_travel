package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {

    private RecyclerView friendlist;
    private DatabaseReference UsersRef,FriendRef,FriendListRef;
    private Button search,myFriend,myPendingRequest;
    private EditText inputSearch;
    private String online_user_id;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        Button search=(Button)findViewById(R.id.search_btn);
        Button myFriend=(Button)findViewById(R.id.my_friend);
        Button myPendingRequest=(Button)findViewById(R.id.my_pending_friend);
        inputSearch=(EditText)findViewById(R.id.search);

        mAuth = FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        FriendListRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(online_user_id);

        friendlist = (RecyclerView) findViewById(R.id.all_user);
        friendlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friendlist.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendlist.setLayoutManager(linearLayoutManager);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = inputSearch.getText().toString();

                showFriends(searchText);
            }
        });
        myFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMyFriends();

            }
        });
        myPendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PendingFriendRequest();
            }
        });



    }

    private void PendingFriendRequest() {
        FirebaseRecyclerAdapter<FriendList, friendViewHolder> RecyclerAdapter =
                new FirebaseRecyclerAdapter<FriendList, friendViewHolder>
                        (
                                FriendList.class,
                                R.layout.friend_list,
                                friendViewHolder.class,
                                FriendListRef
                        )
                {

                    @Override
                    protected void populateViewHolder(friendViewHolder viewHolder, FriendList model, int i) {

                        final String userID=getRef(i).getKey();
                        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final String username=dataSnapshot.child("name").getValue().toString();
                                    final String useremail=dataSnapshot.child("email").getValue().toString();
                                    final String profileImage=dataSnapshot.child("profileimage").getValue().toString();

                                    viewHolder.setName(username);
                                    viewHolder.setEmail(useremail);
                                    viewHolder.setProfileimage(getApplicationContext(),profileImage);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id=getRef(i).getKey();

                                Intent intent = new Intent(FindFriends.this,PersonalInfo.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);
                            }
                        });
                    }
                };
        friendlist.setAdapter(RecyclerAdapter);
    }

    private void showMyFriends() {
        FirebaseRecyclerAdapter<Friends, myFriendViewHolder> RecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, myFriendViewHolder>
                        (
                                Friends.class,
                                R.layout.friend_list,
                                myFriendViewHolder.class,
                                FriendRef
                        )
                {

                    @Override
                    protected void populateViewHolder(myFriendViewHolder viewHolder, Friends model, int i) {

                        viewHolder.setDate(model.getDate());
                        final String userID=getRef(i).getKey();
                        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final String username=dataSnapshot.child("name").getValue().toString();
                                    final String profileImage=dataSnapshot.child("profileimage").getValue().toString();

                                    viewHolder.setName(username);
                                    viewHolder.setProfileimage(getApplicationContext(),profileImage);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id=getRef(i).getKey();

                                Intent intent = new Intent(FindFriends.this,PersonalInfo.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);
                            }
                        });
                    }
                };
        friendlist.setAdapter(RecyclerAdapter);

    }

    private void showFriends(String searchText) {
        Toast.makeText(FindFriends.this, "Searching", Toast.LENGTH_LONG).show();
        Query firebaseSearchQuery = UsersRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf0ff");
        FirebaseRecyclerAdapter<FriendList, friendViewHolder> RecyclerAdapter =
                new FirebaseRecyclerAdapter<FriendList, friendViewHolder>
                        (
                                FriendList.class,
                                R.layout.friend_list,
                                friendViewHolder.class,
                                firebaseSearchQuery
                        )
                {

                    @Override
                    protected void populateViewHolder(friendViewHolder viewHolder, FriendList model, int i) {
                        viewHolder.setName(model.getName());
                        viewHolder.setEmail(model.getEmail());
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id=getRef(i).getKey();

                                Intent intent = new Intent(FindFriends.this,PersonalInfo.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);
                            }
                        });
                    }
                };
        friendlist.setAdapter(RecyclerAdapter);
    }

    public static class friendViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public friendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context ctx, String profileimage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.user_profile_pic);
            Picasso.with(ctx).load(profileimage).into(image);
        }

        public void setName(String name){
            TextView username = (TextView) mView.findViewById(R.id.user_profile_name);
            username.setText(name);
        }
        public void setEmail(String email){
           TextView useremail = (TextView) mView.findViewById(R.id.user_profile_email);
            useremail.setText(email);
       }
    }

    public static class myFriendViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public myFriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setProfileimage(Context ctx, String profileimage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.user_profile_pic);
            Picasso.with(ctx).load(profileimage).into(image);
        }

        public void setName(String name){
            TextView username = (TextView) mView.findViewById(R.id.user_profile_name);
            username.setText(name);
        }
        public void setDate(String date){
            TextView friendDate = (TextView) mView.findViewById(R.id.user_profile_email);
            friendDate.setText(date);
        }
    }


}
