package com.jonalter.photosearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by jalter on 11/13/16.
 */

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.EXTRA_URL_ORIGINAL);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, imageView);
    }

}
