package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.Resource;
import com.mukesh.image_processing.ImageProcessor;

import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class NewPhoto extends AppCompatActivity {
    private Transitioner transitioner;
    protected LinearLayout cameraLayout;
    protected LinearLayout galleryLayout;
    protected ImageView curr_image;
    protected PhotoEditorView new_image;
    protected Bitmap edited_image;
    protected ImageView[] edited;
    protected Button cancelButton;
    protected Button doneButton;
    private static String TAG = "NewPhoto.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo);
//        edited = new ImageView[6];
//        cameraLayout = findViewById(R.id.camera_holder);
//        galleryLayout = findViewById(R.id.filter_gallery);
        Bitmap photo  = BitmapFactory.decodeByteArray(getIntent()
                .getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
        Log.d(TAG, "photo bitmap got created");
        curr_image = findViewById(R.id.curr_photo);
        curr_image.setImageBitmap(photo);
        curr_image.setScaleType(ImageView.ScaleType.FIT_XY);
//        edited[0] = findViewById(R.id.edited_one);
//        edited[1] = findViewById(R.id.edited_two);
//        edited[2] = findViewById(R.id.edited_three);
//        edited[3] = findViewById(R.id.edited_four);
//        edited[4] = findViewById(R.id.edited_five);
//        edited[5] = findViewById(R.id.edited_six);
//
//        edited_image = photo;
//        ImageProcessor imageProcessor = new ImageProcessor();
//        Bitmap b = photo;
//        imageProcessor.tintImage(edited_image, 5);
//        edited[1].setImageBitmap(edited_image);
////        Bitmap blurBitmap = photo;
//        imageProcessor.applyGaussianBlur(edited_image);
//        edited[2].setImageBitmap(edited_image);
////        Bitmap saturationBitmap = photo;
//        imageProcessor.applySaturationFilter(edited_image, 5);
//        edited[3].setImageBitmap(edited_image);
//        imageProcessor.createContrast(edited_image, 3.5);
//        edited[4].setImageBitmap(edited_image);
//        imageProcessor.applySnowEffect(edited_image);
//        edited[5].setImageBitmap(edited_image);
//        imageProcessor.doInvert(edited_image);
//        curr_image.setImageBitmap(edited_image);
////

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

