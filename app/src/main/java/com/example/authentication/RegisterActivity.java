package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    String sID;
    EditText etName, etEmail, etPass;
    Button regBtn;
    TextView tvAlrReg;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    FirebaseUser fbUser;

    private DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        regBtn = findViewById(R.id.regBtn);
        tvAlrReg = findViewById(R.id.tvAlrReg);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        dbUser = FirebaseDatabase.getInstance().getReference("User");

        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        tvAlrReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    etEmail.setError("Please enter your email.");
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    etPass.setError("Please enter your password.");
                    return;
                }
                if(pass.length()<6){
                    etPass.setError("Password must be more than or equal to 6 characters.");
                }
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
                            sID = firebaseAuth.getUid();
                            saveRealTimeDB(email);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }
                });

            }

        });
    }

    public void saveRealTimeDB(String email) {
        fbUser = firebaseAuth.getCurrentUser();

        String name = etName.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {
            //String id = dbUser.push().getKey();
            UserInfoC user = new UserInfoC(name, email);

            dbUser.child(fbUser.getUid()).setValue(user);
        }
    }

}