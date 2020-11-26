package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUp_4 extends AppCompatActivity{
    Spinner spinner_relation_1,spinner_relation_2,spinner_relation_3;
    EditText pContact_1,pContact_2,pContact_3,personContact,vCode;
    Button vFinish;
    private FirebaseAuth tAuth;
    private DatabaseReference userReference;
    String currentUserId;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_4);
        pContact_1=findViewById(R.id.pContact_1);
        pContact_2=findViewById(R.id.pContact_2);
        pContact_3=findViewById(R.id.pContact_3);
        vFinish=findViewById(R.id.vFinish);
        vCode=findViewById(R.id.emergency_number);
        personContact=findViewById(R.id.pContact);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Data Recording...");
        //relation spinner_1
        spinner_relation_1= findViewById(R.id.spinner_relation_1);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items_relation,
                R.layout.color_spiner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        spinner_relation_1.setAdapter(adapter);
        //spinner_relation_1.setOnItemSelectedListener(this);

        //relation spinner_2
        spinner_relation_2= findViewById(R.id.spinner_relation_2);
        ArrayAdapter adapter_2=ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items_relation,
                R.layout.color_spiner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        spinner_relation_2.setAdapter(adapter);
        //spinner_relation_2.setOnItemSelectedListener(this);

        //relation spinner_3
        spinner_relation_3= findViewById(R.id.spinner_relation_3);
        ArrayAdapter adapter_3=ArrayAdapter.createFromResource(
                this,
                R.array.Spinner_items_relation,
                R.layout.color_spiner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        spinner_relation_3.setAdapter(adapter);
        //spinner_relation_3.setOnItemSelectedListener(this);

        tAuth=FirebaseAuth.getInstance();
        currentUserId=tAuth.getUid();
        userReference= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        vFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        saveInformation();
                }
        });
    }

    public void openActivityLogin(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void saveInformation(){
        progressDialog.show();
        String contact_1_relation= spinner_relation_1.getSelectedItem().toString();
        String contact_2_relation= spinner_relation_2.getSelectedItem().toString();
        String contact_3_relation= spinner_relation_3.getSelectedItem().toString();
        String contact_1=pContact_1.getText().toString().trim();
        String contact_2=pContact_2.getText().toString().trim();
        String contact_3=pContact_3.getText().toString().trim();
        String person_Contact= personContact.getText().toString().trim();
        String e_contact=vCode.getText().toString().trim();

        HashMap userMap=new HashMap();
        userMap.put(contact_1_relation,contact_1);
        userMap.put(contact_2_relation,contact_2);
        userMap.put(contact_3_relation,contact_3);
        userMap.put("number",person_Contact);
        userMap.put("emergencynumber",e_contact);

        userReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    openActivityLogin();
                    Toast success=Toast.makeText(SignUp_4.this,"Sign Up Successfull,Please Log in",Toast.LENGTH_LONG);
                    success.show();
                }
                else {
                    String messege=task.getException().getMessage();
                    progressDialog.setMessage("Error Occurd");
                }
            }
        });

    }
}
