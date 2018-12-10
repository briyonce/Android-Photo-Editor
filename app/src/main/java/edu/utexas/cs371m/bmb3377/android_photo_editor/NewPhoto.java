package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.mukesh.image_processing.ImageProcessor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class NewPhoto extends AppCompatActivity {
    protected ImageView curr_image;
    protected Bitmap photo;
    protected ArrayList<FilteredImageOption> edited;
    protected Button cancelButton;
    protected Button doneButton;
    protected RecyclerView imageFiltersRecyclerView;
    private static String TAG = "NewPhoto.java";
    protected Handler handler;
    protected Handler foregroundHandler;
    private static final int DONE_LOADING = 100;
    private ProgressDialog progressDialog;

    class MyCallback implements Handler.Callback {


        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DONE_LOADING:
                    imageFiltersRecyclerView = findViewById(R.id.photo_recycler_view);
                    if(imageFiltersRecyclerView != null && edited != null) {
                        progressDialog.dismiss();
                        imageFiltersRecyclerView.setLayoutManager(new LinearLayoutManager(NewPhoto.this, LinearLayoutManager.HORIZONTAL, false));
                        imageFiltersRecyclerView.setAdapter(new PhotoAdapter(NewPhoto.this, edited, new PhotoAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(FilteredImageOption item) {
                                Toasty.info(NewPhoto.this, item.filterName + " selected", Toast.LENGTH_SHORT, true).show();
                                curr_image.setImageBitmap(item.getPhoto());
                            }
                        }));
                    }
                    break;
            }
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_photo);
        edited = new ArrayList<>();
        photo  = BitmapFactory.decodeByteArray(getIntent()
                .getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
        Log.d(TAG, "photo bitmap got created");
        curr_image = findViewById(R.id.curr_photo);
        curr_image.setImageBitmap(photo);
        curr_image.setScaleType(ImageView.ScaleType.FIT_XY);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading image filters...");
        progressDialog.show();
        MyCallback callback = new MyCallback();
        foregroundHandler = new Handler(callback);
        handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                loadImages();
                foregroundHandler.obtainMessage(DONE_LOADING, true).sendToTarget();
            }
        };
        handler.postDelayed(r, 1000);

        cancelButton = findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });

        doneButton = findViewById(R.id.next_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) curr_image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                Intent startPost = new Intent(getApplicationContext(), PostActivity.class);
                Log.d(TAG, "intent created");
                startPost.putExtra("byteArray", bs.toByteArray());
                Log.d(TAG, "put bitmap into bundle");
                startActivity(startPost);
            }
        });
    }

    private void loadImages() {
        FilteredImageOption filteredImageOption = new FilteredImageOption("No Filter", photo);
        edited.add(filteredImageOption);
        ImageProcessor imageProcessor = new ImageProcessor();
        Bitmap b = imageProcessor.applyBlackFilter(photo);
        FilteredImageOption filteredImageOption1 = new FilteredImageOption("Black Filter", b);
        edited.add(filteredImageOption1);
        b = imageProcessor.emboss(photo);
        FilteredImageOption filteredImageOption2 = new FilteredImageOption("Embossed Filter", b);
        edited.add(filteredImageOption2);
        b = imageProcessor.applySaturationFilter(photo, 5);
        FilteredImageOption filteredImageOption3 = new FilteredImageOption("Saturated Filter", b);
        edited.add(filteredImageOption3);
        b = imageProcessor.applySnowEffect(photo);
        FilteredImageOption filteredImageOption4 = new FilteredImageOption("Snowy Filter", b);
        edited.add(filteredImageOption4);
        b = imageProcessor.doGreyScale(photo);
        FilteredImageOption filteredImageOption5 = new FilteredImageOption("GreyScale Filter", b);
        edited.add(filteredImageOption5);
        b = imageProcessor.doInvert(photo);
        FilteredImageOption filteredImageOption6 = new FilteredImageOption("Inverted Filter", b);
        edited.add(filteredImageOption6);
    }

    private void createDialog() {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
        alertDialogue.setMessage("Are you sure you want to exit? You have unsaved changes.");
        alertDialogue.setCancelable(false);
        alertDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goBack();
            }
        });

        alertDialogue.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogue.create().show();
    }
    {
        // for future reference
//        MediaStore.Images.Media.insertImage(getContentResolver(), /* photo */, "title,", "description");
    }

    //handles back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goBack() {
        Intent returnToMainActivity = new Intent(this, MainActivity.class);
        startActivity(returnToMainActivity);
        finish();
    }
    public void onFailure(Exception e){
        Log.d("NewPhoto: ", e.toString());
    }

}

