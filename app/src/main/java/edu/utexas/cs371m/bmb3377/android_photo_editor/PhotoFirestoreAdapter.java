package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import edu.utexas.cs371m.bmb3377.android_photo_editor.R;

public class PhotoFirestoreAdapter extends FirestoreRecyclerAdapter<PhotoObject,
        PhotoFirestoreAdapter.PhotoViewHolder> {

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;

        public PhotoViewHolder(View theView) {
            super(theView);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = getAdapterPosition();
                    PhotoObject photoObject = (PhotoObject) getItem(i);
                    Intent intent = new Intent(view.getContext(), NewPhoto.class);
                    Bundle bundle = new Bundle();
                    Log.d(MainActivity.TAG, "Make OnePost");
                    bundle.putParcelable("photoObject", photoObject);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    PhotoFirestoreAdapter(@NonNull FirestoreRecyclerOptions<PhotoObject> options) {
        super(options);
    }
    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position, PhotoObject model) {
        // Bind the PhotoObject object to its Holder
        Log.d(MainActivity.TAG, "PhotoFirestoreAdapter bind " + position);
        Log.d(MainActivity.TAG, "u " + model.getUidOwner() + " photoid " + model.getPhotoId());
        holder.text.setText(model.getTitle());
        Storage.getInstance().displayJpg(model, holder.image);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup group, int i) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.activity_main, group, false);
        Log.d(MainActivity.TAG, "oncreateviewholder "+ i);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        // Called when there is an error getting a query snapshot. You may want to update
        // your UI to display an error message to the user.
        Log.d(MainActivity.TAG, "Error " + e.getMessage());
    }
};
