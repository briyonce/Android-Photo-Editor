package edu.utexas.cs371m.bmb3377.android_photo_editor;

import android.graphics.Bitmap;

public class FilteredImageOption {
    String filterName;
    Bitmap photo;

    public FilteredImageOption(String filterName, Bitmap photo) {
        this.filterName = filterName;
        this.photo = photo;
    }

    public String getFilterName() {
        return this.filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
