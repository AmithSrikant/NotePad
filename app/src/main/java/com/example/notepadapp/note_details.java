package com.example.notepadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class note_details extends AppCompatActivity {

    Toolbar toolbar;
    TextView title,content;
    FloatingActionButton editNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        toolbar = findViewById(R.id.toolBarOfNoteDetails);
        title = findViewById(R.id.tittleNoteDetails);
        content = findViewById(R.id.ContentNoteDetails);
        editNoteButton = findViewById(R.id.EditNoteDetailsButton);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //custom back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        Intent NoteData = getIntent();
        String titleTextView = NoteData.getStringExtra("title");
        String contentTextView = NoteData.getStringExtra("content");

        title.setText(titleTextView);
        content.setText(contentTextView);

        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(note_details.this, editNote.class);
                intent.putExtra("title",NoteData.getStringExtra("title"));
                intent.putExtra("content",NoteData.getStringExtra("content"));
                intent.putExtra("NoteId",NoteData.getStringExtra("NoteId"));
                v.getContext().startActivity(intent);
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                startActivity(new Intent(note_details.this,notePadlayout.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}