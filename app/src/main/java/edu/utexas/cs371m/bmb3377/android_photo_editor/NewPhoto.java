package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //Log.d(MainActivity.TAG, "Exit OnePost");
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
