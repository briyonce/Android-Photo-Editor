package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder{
        // XXX This ViewHolder class is built in to the RecyclerView.
        //   Put what you want in it, then initialize them in the constructor
        //   Set an onclicklistener on the view to swapItem with the clicked position
        ImageView iv;
        TextView tv;

        public ViewHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.image);
            tv = view.findViewById(R.id.img_desc);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                }
            });
        }
    }
}
