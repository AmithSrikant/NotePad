package com.example.notepadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    LinearLayout SignupPage;
    TextView forgotButton;

    EditText Email,password;
    Button Loginbutton;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignupPage = findViewById(R.id.SignUPWindowClick);
        forgotButton = findViewById(R.id.forgotPasswordButton);
        Email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        Loginbutton = findViewById(R.id.LoginButton);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarLogin);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            //if already logged in
       if(firebaseUser!=null){
            finish();
            startActivity(new Intent(MainActivity.this,notePadlayout.class));
        }

        SignupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            }
        });
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, forgotActivity.class);
                startActivity(intent);
            }
        });
        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailInString,passwordInString;
                EmailInString = Email.getText().toString().trim();
                passwordInString = password.getText().toString().trim();

                if(EmailInString.isEmpty()&&passwordInString.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter info", Toast.LENGTH_SHORT).show();
                } else if (EmailInString.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                } else if(passwordInString.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    // logging in
                    firebaseAuth.signInWithEmailAndPassword(EmailInString,passwordInString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                    chickMailVerification();
                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "account doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void chickMailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()){
            finish();
            startActivity(new Intent(MainActivity.this,notePadlayout.class));
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}