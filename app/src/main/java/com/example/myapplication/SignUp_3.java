package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp_3 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    RadioGroup radioGroup;
    RadioButton radioButton, male, female, yes_1, no_1, yes_2, no_2;
    Spinner bld_select;
    private FirebaseAuth tAuth;
    private DatabaseReference userReference;
    String currentUserId;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_3);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        yes_1 = findViewById(R.id.yes_1);
        no_1 = findViewById(R.id.no_1);
        yes_2 = findViewById(R.id.yes_2);
        no_2 = findViewById(R.id.no_2);
        bld_select = findViewById(R.id.bld_select);

        progressDialog = new ProgressDialog(this);

        tAuth = FirebaseAuth.getInstance();
        currentUserId = tAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        //blood selector spinner
        Spinner bld_select = findViewById(R.id.bld_select);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items,
                R.layout.color_spiner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        bld_select.setAdapter(adapter);
        // bld_select.setOnItemSelectedListener(this);

        //diabetics radioButton
        radioGroup = findViewById(R.id.radioGroup_diabetics);

        //asthma radioButton
        radioGroup = findViewById(R.id.radioGroup_asthma);

        //continue_3

        Button continue_3 = (Button) findViewById(R.id.continue_3);
        continue_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void checkButton(View view) {
        int radioId_diabetics = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId_diabetics);
    }


    public void openActivitySignup_4() {
        Intent intent = new Intent(this, SignUp_4.class);
        startActivity(intent);
    }

    public void saveInformation() {

        progressDialog.show();
        String pMale = male.getText().toString();
        String pFemale = female.getText().toString();
        String blood = bld_select.getSelectedItem().toString();
        String diabetics_pos = yes_1.getText().toString();
        String diabetics_neg = no_1.getText().toString();
        String Asthma_pos = yes_2.getText().toString();
        String Asthma_neg = no_2.getText().toString();
        HashMap userMap = new HashMap();
        if (male.isChecked()) {
            userMap.put("Gender", pMale);
        } else {
            userMap.put("Gender", pFemale);
        }
        if (yes_1.isChecked()) {
            userMap.put("Diabetics", diabetics_pos);
        } else {
            userMap.put("Diabetics", diabetics_neg);
        }
        if (yes_2.isChecked()) {
            userMap.put("Asthma", Asthma_pos);
        } else {
            userMap.put("Asthma", Asthma_neg);
        }
        userMap.put("Blood Group", blood);

        userReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast success = Toast.makeText(SignUp_3.this, "Data Recorded Successfully", Toast.LENGTH_SHORT);
                    success.show();
                    openActivitySignup_4();
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
