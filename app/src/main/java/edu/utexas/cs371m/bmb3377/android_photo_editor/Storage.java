package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

// Store files in firebase storage
public class Storage {
    StorageReference photoStorage;
    Context context;
    final String TAG = "Storage.java";
    private static class Holder {
        public static Storage helper = new Storage();
    }

    // Every time you need the Net object, you must get it via this helper function
    public static Storage getInstance() {
        return Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init(Context context) {
        Holder.helper.context = context;
        // Initialize our reference, which is a photo collection that has per-user subcollections of photo files
        Holder.helper.photoStorage = FirebaseStorage.getInstance().getReference().child("photos");
    }
    protected StorageReference fileStorage(PhotoObject photoObject) {
        return photoStorage
                .child(photoObject.getUidOwner())
                .child(photoObject.getPhotoId() + ".jpg");
    }

    public void uploadJpg (PhotoObject photoObject, byte[] data) {
        Firestore.getInstance().savePhoto(photoObject);
        // XXX Write me
        StorageReference storageReference = photoStorage.child(photoObject.getUidOwner() + "/" + photoObject.getPhotoId());
        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Long transferred = taskSnapshot.getBytesTransferred();
                Log.d(TAG, "Transferred " + transferred + " bytes! ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PHOTO UPLOAD FAILED", e.getMessage());
            }
        });
    }
    public void displayJpg(PhotoObject photoObject, final ImageView imageView) {
        // XXX Write me
        StorageReference storageReference = photoStorage.child(photoObject.getUidOwner() + "/" + photoObject.getPhotoId());
        // https://stackoverflow.com/questions/45273521/getting-image-from-firebase-storage
        final long ONE_MEGABYTE = 1024*1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                Log.d(TAG, "Transferred " + "successfully pulled photo from firestore");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PHOTO DISPLAY    FAILED", e.getMessage());
            }
        });
    }
}
