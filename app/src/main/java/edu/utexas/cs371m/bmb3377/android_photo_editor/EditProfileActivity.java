package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;
import com.google.zxing.client.result.EmailDoCoMoResultParser;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

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

    private Uri profileUri;

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

        profileUri = user.getPhotoUrl();

        profilePictureView = findViewById(R.id.profile_image);

        if (profileUri != null) {
            getPhotoAsync();
        }
        
        editProfilePicText = findViewById(R.id.edit_profile_picture_text);

        editProfilePicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(null).setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        if (user.getPhotoUrl() != null) {
//            getPhotoAsync();
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

        cancelBut = findViewById(R.id.cancel_button);
        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        doneBut = findViewById(R.id.done_button);
        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if there are unsaved changes ask the user if they're sure they're done
                goBack();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                profileUri = result.getUri();
                final InputStream imageStream = getContentResolver().openInputStream(profileUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePictureView.setImageBitmap(selectedImage);
//                hasImageChanged = true;

                updateProfilePhoto();
                getPhotoAsync();
            } catch (FileNotFoundException e) {

            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            // do nothing
        } else {
            Toasty.error(this, "error after crop activity", Toast.LENGTH_SHORT, true ).show();
        }
    }

    private void updateProfilePhoto() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("updating profile photo...");
        progressDialog.show();
        Log.d(TAG, "in updateProfilePhoto");
        Log.d(TAG, profileUri.toString());
        if (profileUri != null) {
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.buildDrawingCache();
            Bitmap bitmap = profilePictureView.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();


            Log.d(TAG, "profileUri != null.");
            final String photoUrl = profilePictureView.getDrawingCache().hashCode() + System.currentTimeMillis() + ".jpg";
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(photoUrl);
            Log.d(TAG, "before uploadTask");
            final UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    HashMap<String, Object> map = new HashMap<>();
                    Log.d(TAG, "new uri: " + photoUrl);

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "successfully received download url: " + uri.toString());
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.d(TAG, "user profile updated successfully");
                                        progressDialog.dismiss();
                                        Toasty.success(EditProfileActivity.this, "successfully updated user profile pic!", Toast.LENGTH_SHORT, true).show();
                                    } else {
                                        Log.d(TAG, "user profile update failure");
                                        progressDialog.dismiss();
                                        Toasty.error(EditProfileActivity.this, "user profile update failure", Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "failed to get download uri: " + e.getMessage());
                            Toasty.error(EditProfileActivity.this, "failed to get download uri", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "upload task failure: " + e.getMessage());
                    Toasty.error(EditProfileActivity.this, "error: " + e.getMessage(), Toast.LENGTH_LONG, true).show();
                }
            });
        } else {
            Log.d(TAG, "no image selected for upload");
            Toasty.info(this, "No Image Selected!", Toast.LENGTH_SHORT, true).show();
        }
    }


    public void getPhotoAsync() {
        Log.d(TAG, "in getPhotoAsync");
        String url = profileUri.toString();
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