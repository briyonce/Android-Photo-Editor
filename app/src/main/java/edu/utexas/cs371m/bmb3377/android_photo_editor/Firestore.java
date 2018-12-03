package edu.utexas.cs371m.bmb3377.android_photo_editor;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

public class Firestore {

    protected FirebaseFirestore db;
    protected Auth auth;
    protected String TAG = "Firestore.java";

    private static class Holder {
        public static Firestore helper = new Firestore();
    }
    // Every time you need the Net object, you must get it via this helper function
    public static Firestore getInstance() {
        return Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init(Auth auth) {
        Holder.helper.db = FirebaseFirestore.getInstance();
        if( Holder.helper.db == null ) {
            Log.d(MainActivity.TAG, "XXX FirebaseFirestore is null!");
        }
        Holder.helper.auth = auth;
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        Holder.helper.db.setFirestoreSettings(settings);
    }

    void savePhoto(PhotoObject photoMeta) {
        Log.d(MainActivity.TAG,
                String.format("savePhoto uid: %s %s",
                        photoMeta.getUidOwner(),
                        photoMeta.getPhotoId()));
        // XXX Write me
        db.collection("users/" + photoMeta.getUidOwner() + "/photos").document(photoMeta.getPhotoId())
                .set(photoMeta)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    Query getPhotoQuery() {
        String user = auth.user.getUid();
        Log.d(TAG, "USER NAME: " + user);

        // XXX Write me and the query shouldn't stay null
        Query query = db.collection("users/" + user + "/photos");
        return query;
    }

    Query getCommentQuery(PhotoObject photoObject) {
        // XXX Write me and the query shouldn't stay null
        Query query = db.collection(("comments/" + photoObject.getPhotoId() + "/comments"));
        return query;
    }
}
