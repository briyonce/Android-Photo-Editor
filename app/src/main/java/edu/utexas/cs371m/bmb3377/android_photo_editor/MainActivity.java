package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.Activity;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import in.shadowfax.proswipebutton.ProSwipeButton;


public class MainActivity extends AppCompatActivity implements
        FirebaseAuth.AuthStateListener {
    public static String TAG = "MainActivity.java";
    protected Button galleryButton;
    protected Button cameraButton;
    protected Button profileButton;
    protected Button existingPhotoButton;
    private FirebaseAuth auth;
    private static final int CAMERA_REQ = 1888;
    private Bitmap newPhotoBitmap;
    public static String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        this.profileButton = findViewById(R.id.profile_button);

//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent openCamera = new Intent(getApplicationContext(), NewPhoto.class);
//                startActivity(openCamera);
//            }
//        });

        final ProSwipeButton proNewBtn = findViewById(R.id.photo_gallery_button);
        proNewBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
                        Intent openGallery = new Intent(getApplicationContext(), Gallery.class);
                        startActivity(openGallery);
                        proNewBtn.showResultIcon(true);
                    }
                }, 2000);

            }
        });

        final ProSwipeButton proProfileBtn =  findViewById(R.id.edit_photo_button);
        proProfileBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn.
                CropImage.activity(null).setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(MainActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton

                        proProfileBtn.showResultIcon(true);
                    }
                }, 2000);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                startActivity(profileIntent);
            }
        });


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // User is signed out
            Log.d(MainActivity.TAG, "onAuthStateChanged:signed_out");
            // Let's get you signed in/signed up
            Intent startLogin = new Intent(this, LoginActivity.class);
            startActivity(startLogin);
        } else {
            Intent ret = getIntent();
            if (ret != null) {
                int fromProfile = ret.getExtras().getInt("profile");
                int fromRegister = ret.getExtras().getInt("register");
                int fromLogin = ret.getExtras().getInt("login");
                if (fromProfile == 1) {
                    // do nothing
                } else if (fromRegister == 1) {
                    userEmail = ret.getExtras().getString("email");
                    Toasty.success(this, "Welcome to " + auth.getCurrentUser().getDisplayName() + "!", Toast.LENGTH_SHORT, true).show();
                } else if (fromLogin == 1) {
                    userEmail = ret.getExtras().getString("email");
                    Drawable icon = getResources().getDrawable(R.drawable.blank_person);
                    Toasty.normal(getApplicationContext(), "Welcome back " + auth.getCurrentUser().getDisplayName(), icon).show();
                } else {
                    Toast.makeText(this, "Welcome back " + auth.getCurrentUser().getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Welcome back " + auth.getCurrentUser().getDisplayName() + "!", Toast.LENGTH_SHORT).show();
            }

        }

    }



    public void onAuthStateChanged(FirebaseAuth dontuse) {
        if ( auth.getCurrentUser()!= null ) {
            // User is signed in
            updateDisplayUserProfile();
            Log.d(MainActivity.TAG, String.format("%s/%s/%s is signed in",
                    auth.getCurrentUser().getDisplayName(),
                    auth.getCurrentUser().getEmail(),
                    auth.getUid()));
        }
    }

    private void updateServerUserProfile(String userName, Uri photoUri) {
        if( auth.getCurrentUser() != null) {
            Log.d(TAG, String.format("%s new display name", userName));
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    // A null photoUri will overwrite, no .setPhotoUri will not overwrite
                    //.setPhotoUri(photoUri)
                    .build();
            auth.getCurrentUser().updateProfile(request);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                Uri picUri = result.getUri();
                final InputStream imageStream = getContentResolver().openInputStream(picUri);
                newPhotoBitmap = BitmapFactory.decodeStream(imageStream);
                MediaStore.Images.Media.insertImage(getContentResolver(), newPhotoBitmap, "title,", "description");
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                newPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                Log.d(TAG, "about to create intent");
                Intent start_edit = new Intent(getApplicationContext(), NewPhoto.class);
                Log.d(TAG, "intent created");
                start_edit.putExtra("byteArray", bs.toByteArray());
                Log.d(TAG, "put bitmap into bundle");
                startActivity(start_edit);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "file not found after CropActivity: " + e.getMessage());
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            // do nothing
            Log.d(TAG, "user cancelled new photo upload");
            Intent returnHome = new Intent(this, MainActivity.class);
            startActivity(returnHome);
        } else {
            Log.d(TAG, "error after crop activity");
            Toasty.error(this, "error after crop activity", Toast.LENGTH_SHORT, true ).show();
        }
    }

    private void updateDisplayUserProfile() {
        if( auth.getCurrentUser() != null ) {
            String displayName = auth.getCurrentUser().getDisplayName();
            if( displayName != null && displayName.length() > 0 ) {
//                name.setText(displayName);
            }
        }
    }


}
