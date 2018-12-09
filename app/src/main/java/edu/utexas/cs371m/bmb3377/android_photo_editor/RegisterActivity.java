package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    EditText nameText;
    EditText usernameText;
    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button registerBut;
    Button backBut;

    TextView registerSurprise;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    private final String TAG = "RegisterActivity.java";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.nameText = findViewById(R.id.name_text);
        this.usernameText = findViewById(R.id.username_text);
        this.emailText = findViewById(R.id.email_text);
        this.passwordText = findViewById(R.id.password_text);
        this.confirmPasswordText = findViewById(R.id.password_verify_text);

        this.registerBut = findViewById(R.id.register_button);
        this.backBut = findViewById(R.id.back_button);
        this.registerSurprise = findViewById(R.id.registration_click_text);
        this.registerSurprise.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();

        registerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("registering...");
                progressDialog.show();

                String name = nameText.getText().toString();
                String username = usernameText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String passwordVerification = confirmPasswordText.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordVerification)) {
                    progressDialog.dismiss();
                    Toasty.error(RegisterActivity.this, "Please enter values for all fields",
                            Toast.LENGTH_SHORT, true).show();
                } else if (password.length() < 8) {
                    progressDialog.dismiss();
                    Toasty.error(RegisterActivity.this, "Password must be greater than 8 characters",
                            Toast.LENGTH_SHORT, true).show();
                } else if (!password.equals(passwordVerification)) {
                    progressDialog.dismiss();
                    Toasty.error(RegisterActivity.this, "Password and password confirmation do not match",
                            Toast.LENGTH_SHORT, true).show();
                }  else {
                    registerSurprise.setVisibility(View.VISIBLE);
                    RegisterActivity.this.register(name, username, email, password);
                }
            }
        });

        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    public void register(final String name, final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Uri profile_pic_uri = new Uri.Builder().appendPath("https://firebasestorage.googleapis.com/v0/b/mobile-computing-project.appspot.com/o/utex-bevo.png?alt=media&token=559dd5f0-b294-4a35-968b-0b2003a012c6").build();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).setPhotoUri(profile_pic_uri).build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "user profile updated successfully");
                                progressDialog.dismiss();
                                Intent landingScreenIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(landingScreenIntent);
                                finish();
                            } else {
                                Log.d(TAG, "user profile update failure");
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "this username is already in use.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "This email address is already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Auth.getInstance().user = FirebaseAuth.getInstance().getCurrentUser();
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
        Intent returnToLogin = new Intent(this, LoginActivity.class);
        startActivity(returnToLogin);
    }
}
