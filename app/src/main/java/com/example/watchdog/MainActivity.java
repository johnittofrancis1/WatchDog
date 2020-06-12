package com.example.watchdog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    TextView welcome,register;
    EditText mail,password;
    Button submit;
    SignInButton googlebutton;
    GoogleSignInAccount account;
    /*@Override
    protected void onStart() {
        super.onStart();
        getDelegate().onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        welcome = findViewById(R.id.welcome);
        if(account!=null)
        {
            welcome.setText("Welcome "+account.getDisplayName());
            Intent i = new Intent(MainActivity.this,Welcome.class);
            i.putExtra("account",account);
            startActivity(i);
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googlebutton = findViewById(R.id.sign_in_button);
        welcome = findViewById(R.id.welcome);
        register = findViewById(R.id.register);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mailValue = mail.getText().toString();
                final String passValue = password.getText().toString();

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(mailValue))
                        {
                            String dbPassword = (String)dataSnapshot.child(mailValue).child("Password").getValue();
                            int age = Integer.parseInt((String)dataSnapshot.child(mailValue).child("Age").getValue());
                            String gender = (String)dataSnapshot.child(mailValue).child("Gender").getValue();
                            String password = (String)dataSnapshot.child(mailValue).child("Password").getValue();
                            String mailId = (String)dataSnapshot.child(mailValue).child("EmailId").getValue();
                            String phone = (String)dataSnapshot.child(mailValue).child("Phone").getValue();
                            if(passValue.equals(dbPassword))
                            {
                                Intent i = new Intent(MainActivity.this,Welcome.class);
                                Account acc = new Account();
                                acc.setFlag(0);
                                acc.setName(mailValue);
                                acc.setAge(age);
                                acc.setGender(gender);
                                acc.setPassword(password);
                                acc.setEmailId(mailId);
                                acc.setPhoneno(phone);
                                startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        googlebutton.setOnClickListener(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(MainActivity.this,Register.class);
                startActivity(reg);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("HERE", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        if(account!=null)
        {
            Intent i = new Intent(MainActivity.this,Welcome.class);
            Account acc = new Account();
            acc.setFlag(1);
            acc.setName(account.getDisplayName());
            acc.setPhotoUrl(account.getPhotoUrl());
            acc.setEmailId(account.getEmail());
            startActivity(i);
        }

    }
}
