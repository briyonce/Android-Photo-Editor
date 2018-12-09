package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.Resource;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class NewPhoto extends AppCompatActivity implements OnSaveBitmap {
    private Transitioner transitioner;
    protected LinearLayout cameraLayout;
    protected LinearLayout galleryLayout;
    protected ImageView curr_image;
    protected PhotoEditorView new_image;
    protected Bitmap edited_image;
    protected ImageView[] edited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo);
        edited = new ImageView[6];
        cameraLayout = findViewById(R.id.camera_holder);
        galleryLayout = findViewById(R.id.filter_gallery);
        Bitmap photo = (Bitmap) getIntent().getExtras().getParcelable("photo");
        Log.d("NewPhoto: ", "got created");
        curr_image = findViewById(R.id.curr_photo);
        new_image = findViewById(R.id.recent_photo);
        curr_image.setImageBitmap(photo);
        new_image.getSource().setImageBitmap(photo);
        edited[0] = findViewById(R.id.edited_one);
        edited[1] = findViewById(R.id.edited_two);
        edited[2] = findViewById(R.id.edited_three);
        edited[3] = findViewById(R.id.edited_four);
        edited[4] = findViewById(R.id.edited_five);
        edited[5] = findViewById(R.id.edited_six);
        PhotoEditor pEdit = new PhotoEditor.Builder(this, new_image)
                .setPinchTextScalable(true)
                .build();
        pEdit.setFilterEffect(PhotoFilter.NEGATIVE);
        pEdit.saveAsBitmap(this);
        edited[0].setImageBitmap(edited_image);
        pEdit.setFilterEffect(PhotoFilter.FISH_EYE);
        pEdit.saveAsBitmap(this);
        edited[1].setImageBitmap(edited_image);
        pEdit.setFilterEffect(PhotoFilter.NEGATIVE);
        pEdit.saveAsBitmap(this);
        edited[2].setImageBitmap(edited_image);
        pEdit.setFilterEffect(PhotoFilter.FISH_EYE);
        pEdit.saveAsBitmap(this);
        edited[3].setImageBitmap(edited_image);
        pEdit.setFilterEffect(PhotoFilter.NEGATIVE);
        pEdit.saveAsBitmap(this);
        edited[4].setImageBitmap(edited_image);
        pEdit.setFilterEffect(PhotoFilter.FISH_EYE);
        pEdit.saveAsBitmap(this);
        edited[5].setImageBitmap(edited_image);
        transitioner = new Transitioner(cameraLayout, galleryLayout);
        transitioner.animateTo(30, (long) 500, new BounceInterpolator());

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
                Log.d(MainActivity.TAG, "Exit NewPhoto");
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBitmapReady(Bitmap saveBitmap){
        edited_image = saveBitmap;
    }

    public void onFailure(Exception e){
        Log.d("NewPhoto: ", e.toString());
    }

}

