package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context c;
    ArrayList<FilteredImageOption> filteredImages;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FilteredImageOption item);
    }

    public PhotoAdapter(Context c, ArrayList<FilteredImageOption> photos, OnItemClickListener listener) {
        this.listener = listener;
        this.c = c;
        this.filteredImages = photos;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FilteredImageOption option = filteredImages.get(position);
        holder.bind(option, listener);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list_thumbnail_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return filteredImages.size();
    }
}
