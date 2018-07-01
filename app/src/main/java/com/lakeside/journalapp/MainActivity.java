package com.lakeside.journalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lakeside.journalapp.Adapter.NotesAdapter;
import com.lakeside.journalapp.AddNotes.AddNoteActivity;
import com.lakeside.journalapp.Auth.GoogleAuth;
import com.lakeside.journalapp.Data.Note;
import com.lakeside.journalapp.Utils.databaseUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GoogleAuth implements NotesAdapter.ItemClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    private final Context mContext = this;
    private RecyclerView mRecyclerView;
    private TextView mUserName;
    private NotesAdapter mNotesAdapter;
    private List<Note> mNotes;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserName = findViewById(R.id.note_owner_tv);
        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.notes_list_rv);

        //Instantiate the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase  = databaseUtils.getDatabase();
        //mFirebaseDatabase.setPersistenceEnabled(true);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //We get the signed in user here
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //checking if user is signed in, then save theuser details to firebase
                if(user != null){

                    fetchUserNotes();
                    updateUI();
                    //user is signed in. update the UI with notes in database.
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                   //Date upDatedAt = new Date();
                   //Date today = Calendar.getInstance().getTime();


                }else {
                    //user account not found or user is signed out. redirect user to sign in page before continuing to use app
                    Intent signInintent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signInintent);
                }
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Launch the Add Note Activity
                Intent addNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(addNoteIntent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }


    private void updateUI() {

       mUserName.setText(getString(R.string.welcome_message, mAuth.getCurrentUser().getDisplayName()));


        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //Initialize adatper and attach it to the recyclerView
        mNotesAdapter = new NotesAdapter(MainActivity.this, (NotesAdapter.ItemClickListener) this);
        mRecyclerView.setAdapter(mNotesAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        /*if(mAuthStateListener != null) {
            FirebaseAuth.getInstance().signOut();
        }*/
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchUserNotes();
    }

    private void fetchUserNotes() {
        String uid = mAuth.getCurrentUser().getUid();
        mReference = mFirebaseDatabase.getReference("notes").child(uid);
        //Log.d("TodoApp", "getUser " + uid);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mNotes = new ArrayList<Note>();
                //mNotes.clear();
                //Log.w("JournalApp", "getUser:onCancelled " + dataSnapshot.toString());
                //Log.w("JournalApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Note note = data.getValue(Note.class);
                    mNotes.add(note);
                }

                mNotesAdapter.setNote(mNotes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onItemClickListener(String noteId) {
        //Launch the NoteDetail Activity
        Intent noteDetailIntent = new Intent(MainActivity.this, AddNoteActivity.class);
        noteDetailIntent.putExtra(AddNoteActivity.EXTRA_NOTE_ID, noteId);
        startActivity(noteDetailIntent);
        //Toast.makeText(MainActivity.this, "The note at value  " + noteId, Toast.LENGTH_LONG ).show();
    }
}
