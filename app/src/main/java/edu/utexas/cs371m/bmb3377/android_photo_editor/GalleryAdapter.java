package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;

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

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        // Bind the PhotoObject object to its Holder
        Log.d(MainActivity.TAG, "PhotoFirestoreAdapter bind " + position);
//        Log.d(MainActivity.TAG, "u " + model.getUidOwner() + " photoid " + model.getPhotoId());
        holder.tv.setText("new text");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.activity_main, group, false);
        Log.d(MainActivity.TAG, "oncreateviewholder "+ i);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
