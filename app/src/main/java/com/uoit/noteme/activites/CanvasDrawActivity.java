package com.uoit.noteme.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uoit.noteme.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class CanvasDrawActivity extends AppCompatActivity {
    CanvasDraw canvasDraw;
    Button saveBtn, rectBtn, ovalBtn, dmdBtn, lineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_draw);

        canvasDraw = (CanvasDraw) findViewById(R.id.canvas_draw_view);

        saveBtn = (Button) findViewById(R.id.save_image_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("flowchart", screenShot(canvasDraw.save()));
                setResult(Activity.RESULT_OK, resultIntent);

                finish();
            }
        });

        ViewTreeObserver vto = canvasDraw.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                canvasDraw.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = canvasDraw.getMeasuredWidth();
                int height = canvasDraw.getMeasuredHeight();
                canvasDraw.init(height, width);
            }
        });

        initOptionDrawer();
    }

    public void initOptionDrawer() {
        lineBtn = (Button) findViewById(R.id.draw_line);
        dmdBtn = (Button) findViewById(R.id.draw_diamond);
        ovalBtn = (Button) findViewById(R.id.draw_oval);
        rectBtn = (Button) findViewById(R.id.draw_rectangle);

        lineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dmdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ovalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public byte[] screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = baos.toByteArray();

        System.out.println("dimensions"+ bitmap.getWidth()+" "+bitmap.getHeight());

        return img;
    }

    public byte[] screenShot(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = baos.toByteArray();

        System.out.println("dimensions"+ bitmap.getWidth()+" "+bitmap.getHeight());

        return img;
    }
}