package com.example.notepadapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class notePadlayout extends AppCompatActivity {

    FloatingActionButton newnoteButton;
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_padlayout);

        newnoteButton = findViewById(R.id.addnewNoteButton);
        setSupportActionBar(findViewById(R.id.toolBar));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        newnoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(notePadlayout.this, newNote.class));
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model)
            {
                ImageView menuThreeDot = holder.itemView.findViewById(R.id.editTheNoteTHREEDOT);

                int color = randomColorGenrater();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(color,null));
                holder.title.setText(model.getTitle());
                holder.content.setText(model.getContent());

                //getting current note id
                String NoteId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(notePadlayout.this, note_details.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("NoteId",NoteId);
                        v.getContext().startActivity(intent);
                    }
                });

                menuThreeDot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                        popupMenu.inflate(R.menu.action_on_note_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item){
                                int menuItemId = item.getItemId();
                                if(menuItemId==R.id.editNote){
                                    Intent intent =new Intent(notePadlayout.this,editNote.class);
                                    intent.putExtra("title",model.getTitle());
                                    intent.putExtra("content",model.getContent());
                                    intent.putExtra("NoteId",NoteId);
                                    v.getContext().startActivity(intent);
                                }else if (menuItemId==R.id.deleteNote){
                                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(NoteId);
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(notePadlayout.this,"deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(notePadlayout.this,"failed to deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layouts,parent,false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView = findViewById(R.id.notePadRecyclerView);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView title,content;
        LinearLayout mnote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.noteTitle);
            content = itemView.findViewById(R.id.noteContent);
            mnote = itemView.findViewById(R.id.noteLinear);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.supportbarmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(notePadlayout.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
            noteAdapter.stopListening();
    }
    private  int randomColorGenrater(){
        List<Integer> randomColor = new ArrayList<>();
        randomColor.add(R.color.color1);
        randomColor.add(R.color.color2);
        randomColor.add(R.color.color3);
        randomColor.add(R.color.color4);
        randomColor.add(R.color.color5);
        randomColor.add(R.color.color6);
        randomColor.add(R.color.color7);
        randomColor.add(R.color.color8);
        randomColor.add(R.color.color9);
        randomColor.add(R.color.color10);
        randomColor.add(R.color.color11);

        Random random = new Random();
        int randomNumber = random.nextInt(randomColor.size());
        return randomColor.get(randomNumber);
    }
}