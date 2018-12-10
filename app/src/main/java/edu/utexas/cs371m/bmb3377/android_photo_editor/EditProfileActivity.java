package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
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

    EditText usernameEditText;
    EditText bioText;

    private Uri profileUri;

    private boolean changesMade = false;
    private boolean profilePicUpdated = false;
    private final String TAG = "EditProfileActivity.java";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView editProfilePicText;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        profileUri = user.getPhotoUrl();

        profilePictureView = findViewById(R.id.profile_image);
        editProfilePicText = findViewById(R.id.edit_profile_picture_text);

        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        if (user.getPhotoUrl() != null) {
            getPhotoAsync();
            Log.d(TAG, "after getPhotoAsync");
        }

        editProfilePicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(null).setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        updateBut = findViewById(R.id.update_button);
        updateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update user
                progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setMessage("updating profile...");
                progressDialog.show();
                if (profilePicUpdated) {
                    updateProfilePhoto();
                    profilePicUpdated = false;
                }
                updateProfileInfo();
                progressDialog.dismiss();
                Toasty.success(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT, true).show();
                changesMade = false;
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
                if (changesMade) {
                    createDialog();
                } else {
                    goBack();
                }
            }
        });

        usernameEditText = findViewById(R.id.username_edit_text);
        bioText = findViewById(R.id.bio_text);
    }

    private void createDialog() {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
        alertDialogue.setMessage("Are you sure you want to exit? You have unsaved changes.");
        alertDialogue.setCancelable(false);
        alertDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goBack();
            }
        });

        alertDialogue.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogue.create().show();
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
                changesMade = true;
                profilePicUpdated = true;

            } catch (FileNotFoundException e) {
                Log.d(TAG, "file not found after CropActivity: " + e.getMessage());
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            // do nothing
            Log.d(TAG, "user cancelled new photo upload");
        } else {
            Log.d(TAG, "error after crop activity");
            Toasty.error(this, "error after crop activity", Toast.LENGTH_SHORT, true ).show();
        }
    }

    private void updateProfileInfo() {
        String newUserName = usernameEditText.getText().toString();
        if (!TextUtils.isEmpty(newUserName) && (newUserName.length() <= 20)) {
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(newUserName).build();
            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "user profile updated successfully");
                    } else {
                        Log.d(TAG, "user profile update failure");
                        progressDialog.dismiss();
                        Toasty.error(EditProfileActivity.this, "this username is already in use.", Toast.LENGTH_SHORT, true).show();
                    }
                }
            });

        } else if (newUserName.length() > 20) {
            progressDialog.dismiss();
            Toasty.error(this, "Username must be shorter than 8 characters",
                    Toast.LENGTH_SHORT, true).show();
        }
    }


    private void updateProfilePhoto() {
        if (profilePicUpdated) {
            Log.d(TAG, "updating profile photo...");
            Log.d(TAG, profileUri.toString());
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.buildDrawingCache();
            Bitmap bitmap = profilePictureView.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            final String photoUrl = profilePictureView.getDrawingCache().hashCode() + System.currentTimeMillis() + ".jpg";
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(photoUrl);
            Log.d(TAG, "before uploadTask");
            storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
                                        Log.d(TAG, "user profile photo updated successfully");
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
                            progressDialog.dismiss();
                            Log.d(TAG, "failed to get download uri: " + e.getMessage());
                            Toasty.error(EditProfileActivity.this, "failed to get download uri", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "upload task failure: " + e.getMessage());
                    Toasty.error(EditProfileActivity.this, "error: " + e.getMessage(), Toast.LENGTH_LONG, true).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    Log.d(TAG, "making some progress");
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        } else {
            Log.d(TAG, "no image selected for upload");
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
            progressDialog.dismiss();
            Log.d(TAG, "decoding error: " + e.getMessage());
        }
        Log.d(TAG, "url: " + url);

        if (url != null) {
            Log.d(TAG, "url is not null");
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            Log.d(TAG, "gsReference created");

            gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, "photo download success");
                    Picasso.with(EditProfileActivity.this).load(uri).into(profilePictureView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toasty.error(EditProfileActivity.this, "error getting photo download url", Toast.LENGTH_SHORT, true).show();
                    Log.d(TAG, "error: " + exception.getMessage());
                }
            });
        } else {
            progressDialog.dismiss();
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