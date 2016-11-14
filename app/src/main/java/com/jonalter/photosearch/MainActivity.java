package com.jonalter.photosearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String IMAGE_ADAPTER_SAVE_STATE_KEY = "imageAdapter";
    public static final String EXTRA_URL_ORIGINAL = "com.jonalter.photosearch.URL_ORIGINAL";

    JSONArray photoResults;
    ImageAdapter imageAdapter;

    EditText searchField;
    ImageButton searchButton;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Image Loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        // Get UI Components
        searchField = (EditText) findViewById(R.id.editText);
        gridView = (GridView) findViewById(R.id.gridview);
        searchButton = (ImageButton) findViewById(R.id.imageButton);

        // Restore state to handle rotation for grid view
        imageAdapter = new ImageAdapter(MainActivity.this);
        if (savedInstanceState != null) {
            try {
                String jsonString = savedInstanceState.getString(IMAGE_ADAPTER_SAVE_STATE_KEY);
                if (jsonString != null) {
                    JSONArray photos = new JSONArray(jsonString);
                    imageAdapter.setPhotos(photos);
                    gridView.setAdapter(imageAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Event Listeners
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Key-down on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    search();
                    return true;
                }
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                try {
                    Intent intent = new Intent(MainActivity.this, ImageViewerActivity.class);
                    String url = HTTPClient.urlFromFlickrPhoto(photoResults.getJSONObject(position), PhotoSize.Large);
                    Log.i(TAG, "Loading picture from: " + url);
                    intent.putExtra(EXTRA_URL_ORIGINAL, url);
                    startActivity(intent);
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        JSONArray photos = imageAdapter.getPhotos();
        if (photos != null) {
            state.putString(IMAGE_ADAPTER_SAVE_STATE_KEY, photos.toString());
        }
    }

    private void search() {
        // Validate input
        String searchText = searchField.getText().toString().trim();
        if (searchText.length() == 0) {
            Toast.makeText(MainActivity.this, "Please enter something to search for.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        // Make Request
        HTTPClient.searchPhotos(searchText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    photoResults = response.getJSONObject("photos").getJSONArray("photo");
                    imageAdapter.setPhotos(photoResults);
                    gridView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "searchPhotos error response:" + responseString);
            }
        });
    }

}
