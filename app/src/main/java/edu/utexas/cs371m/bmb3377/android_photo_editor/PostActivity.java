package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myURl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView newImage;
    TextView title;
    EditText description;
    Button doneBut;
    ImageView fbSharePic;
    ImageView exportSharePic;
    Button editBut;
    Button shareBut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_photo);

        storageReference = FirebaseStorage.getInstance().getReference("photos");

        doneBut = findViewById(R.id.button_done);
        exportSharePic = findViewById(R.id.export_button);
        fbSharePic = findViewById(R.id.facebook_button);
        editBut = findViewById(R.id.edit_button);
        shareBut = findViewById(R.id.share_button);

        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnHome = new Intent(PostActivity.this, MainActivity.class);
                startActivity(returnHome);
            }
        });

        shareBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open system share dialogue - choose either to export to fb or save to system
            }
        });

        exportSharePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save photo to system
                // also save photo to firebase to show in app gallery
                uploadImage();
                Toast.makeText(getApplicationContext(), "Photo saved to image gallery!", Toast.LENGTH_SHORT).show();
            }
        });

        fbSharePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // export photo to fb
            }
        });

        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // THIS NEEDS TO BE CHANGED!!! NEEDS TO RETURN TO PHOTO EDIT ACTIVITY
                Intent returnToEdit = new Intent(PostActivity.this, MainActivity.class);
                startActivity(returnToEdit);
            }
        });

        CropImage.activity(new Uri.Builder().build())
                .setAspectRatio(1, 1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading...");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myURl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

                        String postID = reference.push().getKey();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("postID", postID);
                        map.put("image", myURl);
                        map.put("owner", Auth.getInstance().getUid());

                        reference.child(postID).setValue(map);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "upload to firebase failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            newImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "error in cropimage activity", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
