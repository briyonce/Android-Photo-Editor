package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    public MyViewHolder (View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.photo_view);
    }
}
