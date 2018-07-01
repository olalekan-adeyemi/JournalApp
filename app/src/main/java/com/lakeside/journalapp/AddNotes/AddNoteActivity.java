package com.lakeside.journalapp.AddNotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lakeside.journalapp.Data.Note;
import com.lakeside.journalapp.R;
import com.lakeside.journalapp.Utils.databaseUtils;

import java.util.Date;
import java.util.List;


public class AddNoteActivity extends AppCompatActivity {

    private EditText mTitle, mDescription;

    // Class constant for logging
    private static final String TAG = AddNoteActivity.class.getSimpleName();

    //extra for note ID received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Constant for default note ID to be used when not in update mode
    private static final String DEFAULT_NOTE_ID = "new_note";
    // Extra for the note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceNoteId";

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    Note mNote;
    List<Note> mNoteintent;

    private String mNoteId = DEFAULT_NOTE_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //Get the views for the note saving action
        initViews();

        //instantiate firebaseAuth and Firebase Database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase  = databaseUtils.getDatabase();

        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)){
            mNoteId = savedInstanceState.getString(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {

            if(mNoteId == DEFAULT_NOTE_ID) {
                Log.d(TAG, "First value of string before getting intent " + mNoteId);
                mNoteId = intent.getStringExtra(EXTRA_NOTE_ID);
                Log.d(TAG, "Actively retrieving a specific task from the Firebase " + mNoteId);

                //Retrieve item from Firebase by passing the note ID
                String uid = mAuth.getCurrentUser().getUid();
                mReference = mFirebaseDatabase.getReference("notes").child(uid);
                DatabaseReference noteReference = mReference.child(mNoteId);

                noteReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            String title = dataSnapshot.child("title").getValue(String.class);
                            String description = dataSnapshot.child("description").getValue(String.class);
                            //now populate the fields in the view by the returned values
                            updateUI(title, description);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

                Log.d(TAG, "Note reference key is " + noteReference);

            }

        }


    }

    private void updateUI(String title, String description) {

        //Log.d(TAG, "update UI called " + title  + " " + description);
        //if a value is returned for the note. update the fields with such values accordingly
        mTitle.setText(title);
        mDescription.setText(description);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(INSTANCE_NOTE_ID, mNoteId);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        mTitle = (EditText) findViewById(R.id.editTextNoteTitle);
        mDescription = (EditText) findViewById(R.id.editTextNotekDescription);


        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.fab_save);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save note to Firebase database. Saves when device is offline
                saveNote();
            }
        });
    }


    private void saveNote() {

        if(mNoteId == DEFAULT_NOTE_ID) {
            //Send new data set to firebase
            mNote = new Note();
            Date date = new Date();
            String upDatedAt = String.valueOf(date.getTime());
            //the index of note updated
            String uid;
            //mAuth = FirebaseAuth.getInstance();
            //mFirebaseDatabase  = databaseUtils.getDatabase();
            if (mAuth.getCurrentUser() != null) {

                //mReference.setValue("test data in database");
                mNote.setTitle(mTitle.getText().toString());
                mNote.setDescription(mDescription.getText().toString());
                mNote.setUpDatedAt(upDatedAt);

                uid = mAuth.getCurrentUser().getUid();
                mReference = mFirebaseDatabase.getReference("notes").child(uid);

                //Get the new push Reference
                DatabaseReference pushRef = mReference.push();
                //Get the referenceKey for the push
                String pushKey = pushRef.getKey();

                //Set the noteId with the new pushKey reference
                mNote.setNoteId(pushKey);

                //Set the note data to push into the note object
                //Note note = new Note(mTitle.getText().toString(), mDescription.getText().toString(), upDatedAt );
                //pushRef.setValue(note);

                // pushing new note with new push Key to 'notes' node using the userId
                pushRef.setValue(mNote);

                Toast.makeText(getApplicationContext(), "Note has been successfully Saved", Toast.LENGTH_SHORT).show();
                finish();
            }

        }else {
            //Update the note set on firebase
            mNote = new Note();
            Date date = new Date();
            String upDatedAt = String.valueOf(date.getTime());
            //the index of note updated
            final DatabaseReference noteUpdateReference = mReference.child(mNoteId);

            noteUpdateReference.child("title").setValue(mTitle.getText().toString());
            noteUpdateReference.child("description").setValue(mDescription.getText().toString());
            noteUpdateReference.child("upDatedAt").setValue(upDatedAt);
            //Using object to update the note value
            /*HashMap<String,Object> result = new HashMap<>();
            result.put("title", mTitle.getText().toString());
            FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child(mNoteId).updateChildren(result);*/

            Toast.makeText(getApplicationContext(), mTitle.getText().toString() + " has been updated", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

}
