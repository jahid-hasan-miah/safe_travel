package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class SignUp_2 extends AppCompatActivity {
    private static final String TAG = "SignUp_2";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText pDob, PCountry, pState, P_Plc_station, pStreet;
    private Button coninue_2;
    private FirebaseAuth tAuth;
    private DatabaseReference userReference;
    String currentUserId;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);
        pDob = findViewById(R.id.pDob);
        PCountry = findViewById(R.id.PCountry);
        pState = findViewById(R.id.pState);
        P_Plc_station = findViewById(R.id.P_Plc_station);
        pStreet = findViewById(R.id.pStreet);

        progressDialog = new ProgressDialog(this);

        tAuth = FirebaseAuth.getInstance();
        currentUserId = tAuth.getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        //datePicker
        mDisplayDate = (TextView) findViewById(R.id.pDob);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignUp_2.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        day, month, year);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy:" + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };


        //continue_2
        Button continue_2 = (Button) findViewById(R.id.continue_2);
        continue_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
            }

        });

    }

    public void saveInformation() {

        progressDialog.show();
        String date_of_birth = pDob.getText().toString().trim();
        String country = PCountry.getText().toString().trim();
        String state = pState.getText().toString().trim();
        String police_station = P_Plc_station.getText().toString().trim();
        String street = pStreet.getText().toString().trim();
        if (TextUtils.isEmpty(date_of_birth)) {
            Toast.makeText(this, "Please enter Date of Birth", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please enter your Country", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(state)) {
            Toast.makeText(this, "Please enter State", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(police_station)) {
            Toast.makeText(this, "Please enter Police Station", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(street)) {
            Toast.makeText(this, "Please enter Street address", Toast.LENGTH_SHORT).show();
        } else {
            HashMap userMap = new HashMap();
            userMap.put("Date of Birth", date_of_birth);
            userMap.put("Country Name", country);
            userMap.put("State Name", state);
            userMap.put("Ploice Station", police_station);
            userMap.put("Street Name", street);


            userReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast success = Toast.makeText(SignUp_2.this, "Data Recorded Successfully", Toast.LENGTH_SHORT);
                        success.show();
                        openActivitySignup_3();
                        progressDialog.dismiss();
                        finish();
                    } else {
                        String messege = task.getException().getMessage();
                        progressDialog.setMessage("Error Occurd");
                    }
                }
            });


        }

    }

    public void openActivitySignup_3() {
        Intent intent = new Intent(this, SignUp_3.class);
        startActivity(intent);
    }
}
