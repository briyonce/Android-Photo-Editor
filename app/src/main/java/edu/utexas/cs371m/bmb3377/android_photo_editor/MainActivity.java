package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;


public class MainActivity extends AppCompatActivity implements
        FirebaseAuth.AuthStateListener {
    public static String TAG = "GalleryAdapter.java";
    protected Button galleryButton;
    protected Button cameraButton;
    private Auth auth;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.galleryButton = findViewById(R.id.photo_gallery_button);
        this.cameraButton = findViewById(R.id.new_photo_button);

        cameraButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(getApplicationContext(), NewPhoto.class);
                startActivity(openGallery);
            }
        });
        
        auth = Auth.getInstance();
        auth.init(this, this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(auth);

    }

    public void onAuthStateChanged(FirebaseAuth dontuse) {
        if ( auth.signedIn() ) {
            // User is signed in
            updateDisplayUserProfile();
            Log.d(MainActivity.TAG, String.format("%s/%s/%s is signed in",
                    auth.getDisplayName(),
                    auth.getEmail(),
                    auth.getUid()));
        }
    }

    private void updateServerUserProfile(String userName, Uri photoUri) {
        if( auth.signedIn() ) {
            Log.d(TAG, String.format("%s new display name", userName));
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    // A null photoUri will overwrite, no .setPhotoUri will not overwrite
                    //.setPhotoUri(photoUri)
                    .build();
            auth.updateProfile(request);
        }
    }

    private void updateDisplayUserProfile() {
        if( auth.signedIn() ) {
            String displayName = auth.getDisplayName();
            if( displayName != null && displayName.length() > 0 ) {
                name.setText(displayName);
            }
        }
    }
}
