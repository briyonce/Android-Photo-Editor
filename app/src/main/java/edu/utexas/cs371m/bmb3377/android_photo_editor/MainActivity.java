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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.support.v4.app.FragmentActivity;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;


public class MainActivity extends AppCompatActivity implements
        FirebaseAuth.AuthStateListener {
    public static String TAG = "MainActivity.java";
    protected Button galleryButton;
    protected Button cameraButton;
    protected Button profileButton;
    protected Button existingPhotoButton;
    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        this.galleryButton = findViewById(R.id.photo_gallery_button);
        this.cameraButton = findViewById(R.id.new_photo_button);
        this.profileButton = findViewById(R.id.profile_button);
        this.existingPhotoButton = findViewById(R.id.existing_photo_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCamera = new Intent(getApplicationContext(), NewPhoto.class);
                startActivity(openCamera);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(getApplicationContext(), Gallery.class);
                startActivity(openGallery);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openProfile = new Intent(getApplicationContext(), Profile.class);
                startActivity(openProfile);
            }
        });

        existingPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        auth = Auth.getInstance();
        auth.init(this, this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(auth);
        if (!auth.signedIn()) {
            // User is signed out
            Log.d(MainActivity.TAG, "onAuthStateChanged:signed_out");
            // Let's get you signed in/signed up
            Intent startLogin = new Intent(this, LoginActivity.class);
            startActivity(startLogin);

        }

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
//                name.setText(displayName);
            }
        }
    }
}
