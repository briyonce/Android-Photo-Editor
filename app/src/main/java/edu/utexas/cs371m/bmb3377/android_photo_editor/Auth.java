package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

//Code comes from FCFIREDIST. Authored by Professot Emmet Witchel
public class Auth implements DefaultLifecycleObserver {

    // State of authentication
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;
    protected FirebaseUser user;
    protected FirebaseAuth.AuthStateListener observer;

    private static class Holder {
        public static Auth helper = new Auth();
    }

    // Every time you need the Net object, you must get it via this helper function
    public static Auth getInstance() {
        return Auth.Holder.helper;
    }
    // Call init before using instance
    public static synchronized void init(final AppCompatActivity activity, final FirebaseAuth.AuthStateListener observer) {
        Holder.helper.auth = FirebaseAuth.getInstance();
        Holder.helper.user = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize our reference, which is a photo collection that has per-user subcollections of photo files
        Holder.helper.observer = observer;
        Holder.helper.authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Holder.helper.auth = firebaseAuth;
                Holder.helper.user = firebaseAuth.getCurrentUser();
                // This way, I know user is set properly before we call
                observer.onAuthStateChanged(null);
                if( Holder.helper.user == null ){
                    // User is signed out
                    Log.d(MainActivity.TAG, "onAuthStateChanged:signed_out");
                    // Documentation for AuthUI says detach listener when calling AuthUI
                    // Let's get you signed in/signed up
                    activity.startActivity(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().createSignInIntentBuilder().build());
                }
            }
        };
    }
    public boolean signedIn() {
        return user != null;
    }
    public String getDisplayName() {
        if( signedIn() ) {
            return user.getDisplayName();
        }
        return null;
    }
    public String getEmail() {
        if( signedIn() ) {
            return user.getEmail();
        }
        return null;
    }
    public String getUid() {
        if( signedIn() ) {
            return user.getUid();
        }
        return null;
    }
    public void updateProfile(UserProfileChangeRequest request) {
        if( signedIn() ) {
            user.updateProfile(request)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(MainActivity.TAG, "User profile updated.");
                                observer.onAuthStateChanged(auth);
                            }
                        }
                    });
        }
    }
    public void signOut() {
        auth.signOut();
    }
    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.d(MainActivity.TAG, "Auth onDestroy ");
        auth.removeAuthStateListener(authListener);
        owner.getLifecycle().removeObserver(this);
    }
    @Override
    public void onStart(LifecycleOwner owner) {
        Log.d(MainActivity.TAG, "Auth onStart ");
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop(LifecycleOwner owner) {
        Log.d(MainActivity.TAG, "Auth onStop ");
        auth.removeAuthStateListener(authListener);
    }
    @Override
    public void onPause(LifecycleOwner owner) {}
    @Override
    public void onResume(LifecycleOwner owner) {}
    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.d(MainActivity.TAG, "Auth onCreate ");
    }
}
