package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.client.result.EmailDoCoMoResultParser;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView profilePictureView;
    private Button updateBut;
    private Button cancelBut;
    private Button doneBut;

    EditText nameText;
    EditText usernameEditText;
    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;

    private final String TAG = "EditProfileActivity.java";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView editProfilePicText;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        profilePictureView = findViewById(R.id.profile_image);
        editProfilePicText = findViewById(R.id.edit_profile_text);

        editProfilePicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent profileEditorLaunch = new Intent(EditProfileActivity.this, chooseProfilePic.class);
//                startActivity(profileEditorLaunch);
                CropImage.activity(null).setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        if (user.getPhotoUrl() != null) {
            getPhotoAsync();
            Log.d(TAG, "after getPhotoAsync");
        }


        updateBut = findViewById(R.id.update_button);
        updateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update user
                Toasty.success(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT, true).show();
                getPhotoAsync();
            }
        });

        cancelBut = findViewById(R.id.back_button);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if there are unsaved changes ask the user if they're sure they're done
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
            if (url.charAt(0) == '/') {
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
//
            gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // handle success
                    Log.d(TAG, "photo download success");
                    Toasty.info(EditProfileActivity.this, "photo uri: " + uri, Toast.LENGTH_SHORT, true).show();
                    Picasso.with(EditProfileActivity.this).load(uri).into(profilePictureView);
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
            Toasty.info(EditProfileActivity.this, "photo uri is null", Toast.LENGTH_SHORT, true).show();
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
        Intent returnHome = new Intent(this, Profile.class);
        startActivity(returnHome);
    }

}