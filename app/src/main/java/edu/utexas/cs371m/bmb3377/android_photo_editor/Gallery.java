package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Gallery extends AppCompatActivity {

    protected LinearLayout galleryLayout;
    protected Button cancelButton;
    protected Button profileButton;
    protected ProgressDialog progressDialog;
    protected Handler handler;
    protected Handler foregroundHandler;
    protected RecyclerView gallery;
    protected ArrayList<Drawable> demo_images;
    private static final int DONE_LOADING = 100;

    class MyCallback implements Handler.Callback {


        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DONE_LOADING:
                    gallery = findViewById(R.id.photo_grid);
                    progressDialog.dismiss();
                    if(gallery != null && demo_images != null) {
                        progressDialog.dismiss();
                        gallery.setLayoutManager(new LinearLayoutManager(Gallery.this, LinearLayoutManager.HORIZONTAL, false));
                        gallery.setAdapter(new ImageAdapter(Gallery.this, demo_images, new ImageAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Drawable item) {
                                        Toasty.info(Gallery.this, "photo selected", Toast.LENGTH_SHORT, true).show();
                                    }
                                }));
                    }
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery);

        galleryLayout = findViewById(R.id.photo_gallery);
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                startActivity(profileIntent);
            }
        });
        demo_images = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading photos...");
        progressDialog.show();
        Gallery.MyCallback callback = new Gallery.MyCallback();
        foregroundHandler = new Handler(callback);
        handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                loadImages();
                foregroundHandler.obtainMessage(DONE_LOADING, true).sendToTarget();
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void loadImages() {
        demo_images.add(getResources().getDrawable(R.drawable.dog1));
        demo_images.add(getResources().getDrawable(R.drawable.dog2));
        demo_images.add(getResources().getDrawable(R.drawable.dog3));
        demo_images.add(getResources().getDrawable(R.drawable.dog4));
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photos");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photos/");
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
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        finish();
    }

    public void loadProfile(View view) {

    }

}
