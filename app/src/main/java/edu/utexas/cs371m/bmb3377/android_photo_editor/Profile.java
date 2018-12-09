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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Button signOutBut;
    private Button backBut;
    private final String TAG = "Profile.java";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView usernameText;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        circleImageView = findViewById(R.id.profile_image);

        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        if (user.getPhotoUrl() != null) {
            if (user.getPhotoUrl().equals("/https%3A%2F%2Ffirebasestorage.googleapis.com%2Fv0%2Fb%2Fmobile-computing-project.appspot.com%2Fo%2Futex-bevo.png%3Falt%3Dmedia%26token%3D559dd5f0-b294-4a35-968b-0b2003a012c6")) {
//                final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
//                progressDialog.show();
//                String userid = user.getUid();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
//
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("id", userid);
//                map.put("username", user.getDisplayName());
//                map.put("name", user.getDisplayName());
//                map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/mobile-computing-project.appspot.com/o/utex-bevo.png?alt=media&token=559dd5f0-b294-4a35-968b-0b2003a012c6");
//                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        progressDialog.dismiss();
//                        if(task.isSuccessful()) {
//                            Log.d(TAG, "user profile updated successfully");
//                        } else {
//                            Log.d(TAG, "user profile update failure");
//                            Toasty.error(Profile.this, "failed to update database for user", Toast.LENGTH_SHORT, true).show();
//                        }
//                    }
//                });
            }
            getPhotoAsync();
        }


        usernameText = findViewById(R.id.username_text);
        if (user != null) {
            usernameText.setText(user.getDisplayName());
        }

        signOutBut = findViewById(R.id.signout_button);
        signOutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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