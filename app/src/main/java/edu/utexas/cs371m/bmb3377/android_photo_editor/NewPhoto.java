package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

public class NewPhoto extends AppCompatActivity {
    private Transitioner transitioner;
    protected LinearLayout cameraLayout;
    protected LinearLayout galleryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo);

        cameraLayout = findViewById(R.id.camera_holder);
        galleryLayout = findViewById(R.id.filter_gallery);

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

}
