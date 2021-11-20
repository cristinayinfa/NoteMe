package com.uoit.noteme.activites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.uoit.noteme.R;
import com.uoit.noteme.adapters.NotesAdapter;
import com.uoit.noteme.database.NotesDatabase;
import com.uoit.noteme.entities.Note;
import com.uoit.noteme.listeners.NotesListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int NoteClickedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(new Intent(
                getApplicationContext(), NewNoteActivity.class), REQUEST_CODE_ADD_NOTE)
        );

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0) {
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        NoteClickedIndex = position;
        Intent UpdateNoteIntent = new Intent(getApplicationContext(), NewNoteActivity.class);
        UpdateNoteIntent.putExtra("isViewOrUpdate", true);
        UpdateNoteIntent.putExtra("note", note);
        startActivityForResult(UpdateNoteIntent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(int requestCode, boolean isNoteDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNoteTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getNotesDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> note_list) {
                super.onPostExecute(note_list);
                if(requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, note_list.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if(requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(NoteClickedIndex);

                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(NoteClickedIndex);
                    } else {
                        noteList.add(NoteClickedIndex, note_list.get(NoteClickedIndex));
                        notesAdapter.notifyItemChanged(NoteClickedIndex);
                    }
                } else if(requestCode == REQUEST_CODE_SHOW_NOTES) {
                    noteList.addAll(note_list);
                    notesAdapter.notifyDataSetChanged();
                }
            }
        }

        new GetNoteTask().execute();
    }

    // Method to export a JSON file
    public void export_json(View v) {

        System.out.println("Export JSON Method called");

        JSONObject export_object = new JSONObject(); // Object for the file
        JSONArray notes_array = new JSONArray(); // Array to store individual note objects

        // loop through the list of all notes
        for (Note note_object: noteList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", note_object.getTitle().toString());
                jsonObject.put("subtitle", note_object.getSubtitle().toString());
                jsonObject.put("text", note_object.getNoteText().toString());
                jsonObject.put("image path", note_object.getImagePath().toString());
                notes_array.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(note_object.toString());
        }

        // Once everything has been added to the notes_array JSON array, add it to the object
        try {
            export_object.put("Notes", notes_array);
            System.out.println("Main object added to the export");
            System.out.println(export_object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Save the object to file
        try {
            FileOutputStream fos = openFileOutput("Notes.json", Context.MODE_PRIVATE);
            System.out.println("JSON file created");
            try {
                fos.write(export_object.toString().getBytes(StandardCharsets.UTF_8));
                System.out.println("JSON written to file");

            } catch (Exception e) {
                System.out.println("Could not create JSON file");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Could not create JSON file");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if(data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted",false));
            }
        }
    }
}