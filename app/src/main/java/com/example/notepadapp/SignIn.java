package com.example.notepadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    LinearLayout LoginPage;
    Button backButton,SignupButton;
    EditText Email,password;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        LoginPage = findViewById(R.id.LoginWindowClick);
        backButton = findViewById(R.id.SignInbackButton);
        SignupButton = findViewById(R.id.SignButton);
        Email = findViewById(R.id.SignUpEmail);
        password = findViewById(R.id.SignUpPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        LoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInString = Email.getText().toString().trim();
                String PasswordInString = password.getText().toString().trim();
                if(emailInString.isEmpty()&&PasswordInString.isEmpty()){
                    Toast.makeText(SignIn.this, "Enter info", Toast.LENGTH_SHORT).show();
                } else if (emailInString.isEmpty()) {
                    Toast.makeText(SignIn.this, "enter email", Toast.LENGTH_SHORT).show();
                } else if (PasswordInString.isEmpty()) {
                    Toast.makeText(SignIn.this, "enter password", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.createUserWithEmailAndPassword(emailInString,PasswordInString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignIn.this, "done", Toast.LENGTH_SHORT).show();
                                SendEmailVerification();
                            }else{
                                Toast.makeText(SignIn.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    //email verification
    private void SendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SignIn.this, "verify your email by clicking the link in your email", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignIn.this, MainActivity.class));
                }
            });
        }else{
            Toast.makeText(this, "Email verification failed", Toast.LENGTH_SHORT).show();
        }
    }
}