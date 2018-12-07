package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name_text;
    EditText username_text;
    EditText email_text;
    EditText password_text;

    Button register_button;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.name_text = findViewById(R.id.name_text);
        this.username_text = findViewById(R.id.username_text);
        this.email_text = findViewById(R.id.email_text);
        this.password_text = findViewById(R.id.password_text);

        this.register_button = findViewById(R.id.register_button);

        auth = FirebaseAuth.getInstance();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("registering...");
                progressDialog.show();

                String name = name_text.getText().toString();
                String username = username_text.getText().toString();
                String email = email_text.getText().toString();
                String password = password_text.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter values for all fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password must be greater than 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterActivity.this.register(name, username, email, password);
                }
            }
        });
    }

    public void register(String name, final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    String userid = user.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", userid);
                    map.put("username", username.toLowerCase());
                    map.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/mobile-computing-project.appspot.com/o/utex-bevo.png?alt=media&token=559dd5f0-b294-4a35-968b-0b2003a012c6");

                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent landingScreenIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(landingScreenIntent);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "this username is already in use.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Either the username or password are invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Auth.getInstance().user = FirebaseAuth.getInstance().getCurrentUser();
    }
}