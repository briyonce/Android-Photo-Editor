package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class Gallery extends AppCompatActivity {

    protected LinearLayout galleryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery);

        galleryLayout = findViewById(R.id.photo_gallery);

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
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        Intent returnHome = new Intent(Gallery.this, MainActivity.class);
        startActivity(returnHome);
    }

    public void loadProfile(View view) {
        Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
        startActivity(profileIntent);
    }

}
