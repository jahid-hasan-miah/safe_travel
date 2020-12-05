package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class SignUp extends AppCompatActivity {
    public EditText pName, pEmail, fPassword, cPassword;
    Button continue_1;
    //progressbar to display while data recording
    ProgressDialog progressDialog;
    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        pName = findViewById(R.id.pName);
        pEmail = findViewById(R.id.pEmail);
        fPassword = findViewById(R.id.fPassword);
        cPassword = findViewById(R.id.cPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Data Recording...");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();

        Button continue_1 = (Button) findViewById(R.id.continue_1);
        continue_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input name,email,pass,confirm pass
                String name = pName.getText().toString().trim();
                String email = pEmail.getText().toString().trim();
                String password = fPassword.getText().toString().trim();
                String confirm_passwoord = cPassword.getText().toString().trim();

                //validate email format
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set eror and focusable
                    pEmail.setError("Invalid Email");
                    pEmail.setFocusable(true);
                } else if (!password.equals(confirm_passwoord)) {
                    cPassword.setError("Password and confirm password didn't match");
                    cPassword.setFocusable(true);

                } else {

                    registerUser(name, email, password, confirm_passwoord);

                }
            }
        });
    }


    private void registerUser(String name, String email, String password, String confirm_passwoord) {
        //email and password is valid show progress and dialouge
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, (Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        String device_token = FirebaseInstanceId.getInstance().getToken();

                        Toast success = Toast.makeText(SignUp.this, "Data Recorded Successfully", Toast.LENGTH_SHORT);
                        success.show();
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        currentUserId = mAuth.getCurrentUser().getUid();
                        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
                        HashMap userMap = new HashMap();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("password", password);
                        userMap.put("devicetoken", device_token);

                        userReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast success = Toast.makeText(SignUp.this, "Data Recorded Successfully", Toast.LENGTH_SHORT);
                                    success.show();
                                    openActivitySignup_2();
                                    progressDialog.dismiss();
                                    finish();
                                } else {
                                    String messege = task.getException().getMessage();
                                    progressDialog.setMessage("Error Occurd");
                                }
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();

                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUp.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openActivitySignup_2() {
        Intent intent = new Intent(SignUp.this, SignUp_2.class);
        startActivity(intent);
    }

}
