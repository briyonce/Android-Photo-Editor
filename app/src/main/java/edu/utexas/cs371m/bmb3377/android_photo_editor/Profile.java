package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class Profile extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Button signOutBut;
    private Button backBut;
    private final String TAG = "Profile.java";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView editProfileText;
    private TextView usernameText;
    private TextView bioText;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        circleImageView = findViewById(R.id.profile_image);
        editProfileText = findViewById(R.id.edit_profile_text);

        editProfileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileEditorLaunch = new Intent(Profile.this, EditProfileActivity.class);
                startActivity(profileEditorLaunch);
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        DatabaseReference ref = reference.child("bio");


        bioText = findViewById(R.id.bio_text);


        if (user.getPhotoUrl() != null) {
            getPhotoAsync();
            Log.d(TAG, "after getPhotoAsync");
        }


        usernameText = findViewById(R.id.username_text);
        if (user != null) {
            usernameText.setText(user.getDisplayName());
        }

        signOutBut = findViewById(R.id.signout_button);

        PushDownAnim.setPushDownAnimTo(signOutBut)
        .setScale( MODE_STATIC_DP, PushDownAnim.DEFAULT_PUSH_STATIC  )
                .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        auth.signOut();
                        if (auth.getCurrentUser() == null) {
                            Toasty.success(Profile.this, "Successfully signed out!", Toast.LENGTH_SHORT, true).show();
                            Intent returnHomeIntent = new Intent(Profile.this, MainActivity.class);
                            startActivity(returnHomeIntent);
                        } else {
                            Toasty.error(Profile.this, "Sign out error.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });


        backBut = findViewById(R.id.back_button);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    public void getPhotoAsync() {
        Log.d(TAG, "in getPhotoAsync");

        String url = user.getPhotoUrl().toString();
        Log.d(TAG, "original url: " + url);
        try {
            url = java.net.URLDecoder.decode(url, "UTF-8");
            if (url.charAt(0) == '/' && url.charAt(1) == 'h') {
                url = url.substring(1, url.length());
            }
            Log.d(TAG, "equality: " + url.equals("https://firebasestorage.googleapis.com/v0/b/mobile-computing-project.appspot.com/o/utex-bevo.png?alt=media&token=559dd5f0-b294-4a35-968b-0b2003a012c6"));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "decoding error: " + e.getMessage());
        }
        Log.d(TAG, "url: " + url);
// Create a reference to a file from a Google Cloud Storage URI
        if (url != null) {
            Log.d(TAG, "url is not null");
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            Log.d(TAG, "gsReference created");
            gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // handle success
                    Log.d(TAG, "photo download success");
                    Toasty.info(Profile.this, "photo uri: " + uri, Toast.LENGTH_SHORT, true).show();
                    Picasso.with(Profile.this).load(uri).into(circleImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d(TAG, "error: " + exception.getMessage());

                }
            });
        } else {
            Log.d(TAG, "photo uri is null");
            Toasty.info(Profile.this, "photo uri is null", Toast.LENGTH_SHORT, true).show();

        }
    }

    //handles back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                Log.d(TAG, "Exit NewPhoto");
                goBack();
        }
        return true;
    }

    private void goBack() {
        Intent returnHome = new Intent(this, MainActivity.class);
        returnHome.putExtra("profile", 1);
        startActivity(returnHome);
    }
}