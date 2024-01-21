package com.example.notepadapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editNote extends AppCompatActivity {
    Toolbar toolbar;
    EditText title,content;
    FloatingActionButton saveEditButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        toolbar = findViewById(R.id.toolBarOfEditNote);
        title = findViewById(R.id.tittleEditNote);
        content = findViewById(R.id.ContentEditNote);
        saveEditButton = findViewById(R.id.saveEditnote);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //custom back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        content.setText(intent.getStringExtra("content"));

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternet()) {
                    saveEditButton.setClickable(false);
                    title.setEnabled(false);
                    title.setEnabled(false);
                    String newTitle = title.getText().toString();
                    String newContent = content.getText().toString();

                    if (newTitle.isEmpty() && newContent.isEmpty()) {
                        Toast.makeText(editNote.this, "note pad is empty", Toast.LENGTH_SHORT).show();
                    } else if (newContent.isEmpty()) {
                        Toast.makeText(editNote.this, "enter content", Toast.LENGTH_SHORT).show();
                    } else if (newTitle.isEmpty()) {
                        Toast.makeText(editNote.this, "title is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        //getting the document reference of the current note
                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(intent.getStringExtra("NoteId"));
                        Map<String, Object> note = new HashMap<>();
                        note.put("title", newTitle);
                        note.put("content", newContent);
                        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                saveEditButton.setClickable(true);
                                Toast.makeText(editNote.this, "changes saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(editNote.this, notePadlayout.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveEditButton.setClickable(true);
                                Toast.makeText(editNote.this, "failed to update", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(editNote.this, "check internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                startActivity(new Intent(editNote.this,notePadlayout.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            if(networkInfo.isAvailable()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

}