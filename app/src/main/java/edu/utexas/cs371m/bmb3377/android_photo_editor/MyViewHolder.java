package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.Application;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;
    CheckBox checkBox;
    public MyViewHolder (View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.photo_view);
        name = itemView.findViewById(R.id.filter_name_text);
        checkBox = itemView.findViewById(R.id.checkbox);
    }

    public void bind(final FilteredImageOption item, final PhotoAdapter.OnItemClickListener listener) {
        this.image.setImageBitmap(item.photo);
        this.name.setText(item.getFilterName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}
