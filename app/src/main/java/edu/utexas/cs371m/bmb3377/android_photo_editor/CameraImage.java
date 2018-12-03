package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.graphics.drawable.BitmapDrawable;

public class CameraImage extends Fragment {

//    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
//    Button button;
//    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_main,
                container, false);
//
//        button = (Button) rootView.findViewById(R.id.button);
//        imageView = (ImageView) rootView.findViewById(R.id.imageview);
//
//        button.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,
//                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//
//            }
//        });
//
//        return rootView;
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

//                imageView.setImageBitmap(bitmap);

            }
//        }
    }
}
