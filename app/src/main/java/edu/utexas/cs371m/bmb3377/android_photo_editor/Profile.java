package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private CircleImageView circleImageView;
    private Button deleteBut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        circleImageView = findViewById(R.id.profile_image);
//        circleImageView.setImageBitmap(null); // this will change when we get a user profile photo

        deleteBut = findViewById(R.id.delete_button);
    }
}
