package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Button signOutBut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        circleImageView = findViewById(R.id.profile_image);
//        circleImageView.setImageBitmap(null); // this will change when we get a user profile photo

        signOutBut = findViewById(R.id.signout_button);
        signOutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.getInstance().signOut();
                Intent returnHomeIntent = new Intent(Profile.this, MainActivity.class);
                startActivity(returnHomeIntent);
            }
        });
    }
}
