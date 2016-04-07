package com.example.jera.projectapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab;

    EditText etTitle, etDesc;

    String title, note;
    long time;

    boolean editingNote;
    Button play,stop,record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //record

        play=(Button)findViewById(R.id.button3);
        stop=(Button)findViewById(R.id.button2);
        record=(Button)findViewById(R.id.button);

        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }

                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder  = null;

                stop.setEnabled(false);
                play.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });



        //Finish record------------------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.addnote_toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_clear_24dp);
        getSupportActionBar().setTitle("Add new note");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTitle = (EditText) findViewById(R.id.addnote_title);
        etDesc = (EditText) findViewById(R.id.addnote_desc);

        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);


        //  handle intent

//        editingNote = getIntent() != null;
        editingNote = getIntent().getBooleanExtra("isEditing", false);
        if (editingNote) {
            title = getIntent().getStringExtra("note_title");
            note = getIntent().getStringExtra("note");
            time = getIntent().getLongExtra("note_time", 0);

            etTitle.setText(title);
            etDesc.setText(note);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add note to DB

                String newTitle = etTitle.getText().toString();
                String newDesc = etDesc.getText().toString();
                long newTime = System.currentTimeMillis();


                /**
                 * TODO: Check if note exists before saving
                 */
                if (!editingNote) {
                    Log.d("Note", "saving");
                    Note note = new Note(newTitle, newDesc, newTime);
                    note.save();
                } else {
                    Log.d("Note", "updating");

//                    List<Note> notes = Note.findWithQuery(Note.class, "where title = ?", title);
                    List<Note> notes = Note.find(Note.class, "title = ?", title);
                    if (notes.size() > 0) {

                        Note note = notes.get(0);
                        Log.d("got note", "note: " + note.title);
                        note.title = newTitle;
                        note.note = newDesc;
                        note.time = newTime;

                        note.save();

                    }

                }

                finish();


            }
        });


    }
}
