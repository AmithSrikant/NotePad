package com.example.notepadapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class newNote extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FloatingActionButton saveNoteButton;
    EditText tittleEdit,contentEdit;
    FirebaseFirestore firebaseFirestore;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        saveNoteButton = findViewById(R.id.saveCreateNote);
        tittleEdit = findViewById(R.id.tittleCreateNote);
        contentEdit = findViewById(R.id.ContentCreateNote);
        toolbar = findViewById(R.id.toolBarOfCreateNote);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //custom back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tittleEdit.getText().toString();
                String content = contentEdit.getText().toString();
                if (title.isEmpty() && content.isEmpty()) {
                    Toast.makeText(newNote.this, "write a note first", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    Toast.makeText(newNote.this, "enter tittle", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(newNote.this, "enter content", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkInternet()) {
                        tittleEdit.setEnabled(false);
                        contentEdit.setEnabled(false);
                        saveNoteButton.setClickable(false);
                    /*firebaseFirestore.collection("notes")
                    *makes like a book containing all user notes
                    *
                    /*document(firebaseUser.getUid()).collection("myNotes").document()
                    *gets the user id of every user and makes separate chapter or note for every user
                    */
                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                        Map<String, Object> note = new HashMap<>();
                        note.put("title", title);
                        note.put("content", content);

                        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                saveNoteButton.setClickable(true);
                                Toast.makeText(newNote.this, "saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(newNote.this, notePadlayout.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveNoteButton.setClickable(true);
                                Toast.makeText(newNote.this, "Failed to save", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(newNote.this, "check network", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                startActivity(new Intent(newNote.this,notePadlayout.class));
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