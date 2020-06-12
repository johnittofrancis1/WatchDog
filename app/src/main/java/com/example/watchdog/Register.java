package com.example.watchdog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText name,age,gender,mail,phone,password,cnfrmpassword;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        mail = findViewById(R.id.mail);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.pass);
        cnfrmpassword =findViewById(R.id.cnfrmpass);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                String nameValue = name.getText().toString();
                String ageValue = age.getText().toString();
                String genderValue = gender.getText().toString();
                String mailValue = mail.getText().toString();
                String phoneValue = phone.getText().toString();
                String pass = password.getText().toString();
                String cnfrmpass = cnfrmpassword.getText().toString();

                if(pass.equals(cnfrmpass))
                {
                    db.child(nameValue).child("EmailId").setValue(nameValue);
                    db.child(nameValue).child("Age").setValue(ageValue);
                    db.child(nameValue).child("Gender").setValue(genderValue);
                    db.child(nameValue).child("Phone").setValue(phoneValue);
                    db.child(nameValue).child("Password").setValue(pass);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Password does not match",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
