package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    protected Auth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        auth = Auth.getInstance();
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder().setAvailableProviders(AuthUI.));
    }
}
