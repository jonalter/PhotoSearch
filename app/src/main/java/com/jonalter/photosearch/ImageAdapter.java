package com.jonalter.photosearch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jalter on 11/13/16.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private JSONArray mPhotos;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void setPhotos(JSONArray photos) {
        mPhotos = photos;
    }

    public JSONArray getPhotos() {
        return mPhotos;
    }

    public int getCount() {
        return mPhotos.length();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
            // Ensure that old images are not displayed while new images are loaded
            imageView.setImageDrawable(null);
        }

        try {
            JSONObject photo = mPhotos.getJSONObject(position);
            String url = HTTPClient.urlFromFlickrPhoto(photo, PhotoSize.Thumbnail);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(url, imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return imageView;
    }

}
