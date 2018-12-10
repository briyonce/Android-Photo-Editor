package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.Resource;
import com.mukesh.image_processing.ImageProcessor;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class NewPhoto extends AppCompatActivity {
    private Transitioner transitioner;
    protected LinearLayout cameraLayout;
    protected LinearLayout galleryLayout;
    protected ImageView curr_image;
    protected PhotoEditorView new_image;
    protected Bitmap edited_image;
    protected ArrayList<Bitmap> edited;
    protected Button cancelButton;
    protected Button doneButton;
    protected RecyclerView imageFiltersRecyclerView;
    private static String TAG = "NewPhoto.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo);
        edited = new ArrayList<Bitmap>();
        Bitmap photo  = BitmapFactory.decodeByteArray(getIntent()
                .getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
        Log.d(TAG, "photo bitmap got created");
        curr_image = findViewById(R.id.curr_photo);
        curr_image.setImageBitmap(photo);
        curr_image.setScaleType(ImageView.ScaleType.FIT_XY);
        edited_image = photo;
        ImageProcessor imageProcessor = new ImageProcessor();
        Bitmap b = imageProcessor.applyBlackFilter(photo);
        edited.add(b);

        edited.add(b);
        edited.add(b);
        edited.add(b);
        edited.add(b);
        edited.add(b);

        imageFiltersRecyclerView = findViewById(R.id.photo_recycler_view);
        imageFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageFiltersRecyclerView.setAdapter(new PhotoAdapter(this, edited));
//        imageFiltersRecyclerView.getLayoutParams().height = 80;
//        imageFiltersRecyclerView.getLayoutParams().width = 60;
//        edited_image = photo;
//        ImageProcessor imageProcessor = new ImageProcessor();
//        Bitmap b = photo;
//        imageProcessor.applyBlackFilter(b);
//        edited[0].setImageBitmap(b);
//        imageProcessor.tintImage(edited_image, 5);
//        edited[1].setImageBitmap(edited_image);
//        imageProcessor.applyGaussianBlur(edited_image);
//        edited[2].setImageBitmap(edited_image);
//        imageProcessor.applySaturationFilter(edited_image, 5);
//        edited[3].setImageBitmap(edited_image);
//        imageProcessor.createContrast(edited_image, 3.5);
//        edited[4].setImageBitmap(edited_image);
//        imageProcessor.applySnowEffect(edited_image);
//        edited[5].setImageBitmap(edited_image);
//        imageProcessor.doInvert(edited_image);
//        curr_image.setImageBitmap(edited_image);

        cancelButton = findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });

        doneButton = findViewById(R.id.next_button);
    }

    private void createDialog() {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
        alertDialogue.setMessage("Are you sure you want to exit? You have unsaved changes.");
        alertDialogue.setCancelable(false);
        alertDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goBack();
            }
        });

        alertDialogue.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogue.create().show();
    }
    {
        // for future reference
//        MediaStore.Images.Media.insertImage(getContentResolver(), /* photo */, "title,", "description");
    }

    //handles back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goBack() {
        Intent returnToMainActivity = new Intent(this, MainActivity.class);
        startActivity(returnToMainActivity);
        finish();
    }
    public void onFailure(Exception e){
        Log.d("NewPhoto: ", e.toString());
    }

}

