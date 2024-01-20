package com.example.notepadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotActivity extends AppCompatActivity {

    Button backButton,RecoverButton;
    EditText forgotEmail;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        backButton = findViewById(R.id.forgotbackButton);
        RecoverButton = findViewById(R.id.RecoverPasswordButton);
        forgotEmail = findViewById(R.id.ForgotEmail);
        firebaseAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forgotActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        RecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailInString = forgotEmail.getText().toString().trim();
                if(EmailInString.isEmpty()){
                    Toast.makeText(forgotActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
                else{
                    //recover code
                    firebaseAuth.sendPasswordResetEmail(EmailInString).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(forgotActivity.this, "Email send", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotActivity.this,MainActivity.class));
                            }else{
                                Toast.makeText(forgotActivity.this, "Failed Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}