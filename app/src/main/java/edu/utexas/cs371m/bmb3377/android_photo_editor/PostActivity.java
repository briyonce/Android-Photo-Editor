package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

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
    Bitmap photo;
    private static String TAG = "PostActivity.java";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_photo);

        storageReference = FirebaseStorage.getInstance().getReference("photos");

        doneBut = findViewById(R.id.button_done);
        editBut = findViewById(R.id.edit_button);
        shareBut = findViewById(R.id.share_button);

        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnHome = new Intent(PostActivity.this, MainActivity.class);
                startActivity(returnHome);
                finish();
            }
        });

        shareBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), photo,"title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri );
                startActivity(Intent.createChooser(intent , "Share"));
            }
        });

        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        photo  = BitmapFactory.decodeByteArray(getIntent()
                .getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
        Log.d(TAG, "photo bitmap got created");
        newImage = findViewById(R.id.finalized_photo);
        newImage.setImageBitmap(photo);
        newImage.setScaleType(ImageView.ScaleType.FIT_XY);

//        CropImage.activity(new Uri.Builder().build())
//                .setAspectRatio(1, 1)
//                .start(PostActivity.this);
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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/photos");

                        String postID = reference.push().getKey();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("postID", postID);
                        map.put("image", myURl);
                        map.put("owner", FirebaseAuth.getInstance().getUid());

                        reference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(getApplicationContext(), "successfully uploaded photo!", Toast.LENGTH_SHORT, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toasty.error(getApplicationContext(), "failed to update profile", Toast.LENGTH_SHORT, true).show();
                                Log.d(TAG, "Photo upload error: " + e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(PostActivity.this, "upload to firebase failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
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
