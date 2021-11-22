package com.uoit.noteme.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uoit.noteme.R;
import com.uoit.noteme.database.NotesDatabase;
import com.uoit.noteme.entities.Note;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class NewNoteActivity extends AppCompatActivity {
    private Note alreadyAvailableNote;
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView image;

    private AlertDialog DeleteNoteAlert;

    private String selectedNoteColor;
    private String ImagePath;

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int picture_id = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        image = findViewById(R.id.image);

        textDateTime = findViewById(R.id.textDateTime);
        textDateTime.setText(new SimpleDateFormat(
                "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date().getTime())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveNote());

        selectedNoteColor = "#333333";
        ImagePath = " ";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        initMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            image.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            image.setVisibility(View.VISIBLE);
            ImagePath = alreadyAvailableNote.getImagePath();
        }
    }

    private void saveNote() {
        final String noteTitle = inputNoteTitle.getText().toString().trim();
        final String noteSubtitle = inputNoteSubtitle.getText().toString().trim();
        final String noteText = inputNoteText.getText().toString().trim();
        final String dateTimeStr = textDateTime.getText().toString().trim();

        if (noteTitle.isEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(noteTitle);
        note.setSubtitle(noteSubtitle);
        note.setNoteText(noteText);
        note.setDateTime(dateTimeStr);
        note.setColor(selectedNoteColor);
        note.setImagePath(ImagePath);

        if(alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent PostExecuteIntent = new Intent();
                setResult(RESULT_OK, PostExecuteIntent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }

    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutColor);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        if(alreadyAvailableNote !=null && alreadyAvailableNote.getColor() != null) {
            String colour = alreadyAvailableNote.getColor();
            if(!colour.trim().isEmpty()) {
                switch (colour) {
                    case "#FFFF00":
                        layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                        break;
                    case "#7B99F4":
                        layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                        break;
                    case "#98D319":
                        layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                        break;
                    case "#19CED3":
                        layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                        break;
                    default:
                        layoutMiscellaneous.findViewById(R.id.viewColor1).performClick();
                        break;
                }
            }
        }

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#333333";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#FFFF00";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#7B99F4";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#98D319";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#19CED3";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            setSubtitleIndicatorColor();
        });

        // This is the onclick method for the add image layout
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            NewNoteActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        // OnClick method for adding an image through camera
        layoutMiscellaneous.findViewById(R.id.add_image_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add Image through camera button clicked from NewNoteActivity");
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, picture_id);
            }
        });

        if(alreadyAvailableNote != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDelete();
                }
            });
        }
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    // intent to redirect to image selection
    private void selectImage() {
        Intent imgIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imgIntent, REQUEST_CODE_SELECT_IMAGE);
    }

    // Show delete button only when a note already exists
    private void showDelete() {
        if(DeleteNoteAlert == null) {
            AlertDialog.Builder DeleteAlertBuilder = new AlertDialog.Builder(NewNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            DeleteAlertBuilder.setView(view);
            DeleteNoteAlert = DeleteAlertBuilder.create();

            if(DeleteNoteAlert.getWindow() != null) {
                DeleteNoteAlert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            //delete button
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNote extends AsyncTask<Void,Void,Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent DeleteIntent = new Intent();
                            DeleteIntent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, DeleteIntent);
                            finish();
                        }
                    }
                    new DeleteNote().execute();
                }
            });

            //cancel delete button
            view.findViewById(R.id.textCancelDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteNoteAlert.dismiss();
                }
            });
        }

        DeleteNoteAlert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else{
                Toast.makeText(this, "Error: Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Display image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    try{
                        InputStream is = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        image.setImageBitmap(bitmap);
                        image.setVisibility(View.VISIBLE);

                        ImagePath = getPathFromUri(selectedImageUri);


                    } catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (requestCode == picture_id) {
            System.out.println("This should be a camera image");
            Bitmap camera_capture = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(camera_capture);
            image.setVisibility(View.VISIBLE);

            // Get the path for the image
            Uri selectedImageUri = getImageUri(getApplicationContext(), camera_capture);
            ImagePath = getPathFromUri(selectedImageUri);
            System.out.println(ImagePath);
            
        }
    }

    // Get image uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // Get path of image selected
    private String getPathFromUri(Uri contentUri){
        String path;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if(cursor == null){
            path = contentUri.getPath();
        } else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            path = cursor.getString(index);
            cursor.close();
        }
        return path;
    }
}