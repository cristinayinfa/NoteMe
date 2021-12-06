package com.uoit.noteme.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.uoit.noteme.R;

import java.io.File;
import java.io.FileOutputStream;

public class CanvasDrawActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_draw);
    }

    public void save_image(View v) {
        System.out.println("Save canvas image button clicked");
        Bitmap flowchart = screenShot(findViewById(R.id.canvas_draw_view));
        MediaStore.Images.Media.insertImage(getContentResolver(), flowchart, "NoteImage", null);
//        Intent back_new_note = new Intent(getApplicationContext(), NewNoteActivity.class);
//        startActivity(back_new_note);
        finish();
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        System.out.println("Height: " + view.getHeight() + " Width: " + view.getWidth());
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory() + "/flowchart.jpg");

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}