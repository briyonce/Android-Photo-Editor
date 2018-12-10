package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context c;
    ArrayList<Bitmap> filteredEffects;

    public PhotoAdapter(Context c, ArrayList<Bitmap> photos) {
        this.c = c;
        this.filteredEffects = photos;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bitmap currentBitmap = filteredEffects.get(position);
        holder.image.setImageBitmap(currentBitmap);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list_thumbnail_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return filteredEffects.size();
    }
}
