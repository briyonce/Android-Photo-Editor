package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context c;
    ArrayList<Drawable> images;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Drawable item);
    }

    public ImageAdapter(Context c, ArrayList<Drawable> photos, OnItemClickListener listener) {
        this.listener = listener;
        this.c = c;
        this.images = photos;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Drawable image = images.get(position);
//        holder.image.setImageDrawable(image);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list_thumbnail_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
