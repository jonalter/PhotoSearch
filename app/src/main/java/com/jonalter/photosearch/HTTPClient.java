package com.jonalter.photosearch;

/**
 * Created by jalter on 11/13/16.
 */

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPClient {
    private static final String BASE_URL = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "86be13345055905306530ce63200afa8";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void searchPhotos(String text, AsyncHttpResponseHandler responseHandler) {
        RequestParams  params = new RequestParams();
        params.put("method", "flickr.photos.search");
        params.put("api_key", API_KEY);
        params.put("format", "json");
        params.put("text", text);
        params.put("nojsoncallback", "1");

        client.get(BASE_URL, params, responseHandler);
    }

    public static String urlFromFlickrPhoto(JSONObject photo, PhotoSize size) throws JSONException {
        return "http://farm" + photo.getString("farm") + ".staticflickr.com/" +
                photo.getString("server") + "/" + photo.getString("id") + "_" +
                photo.getString("secret") + codeFromSize(size) + ".jpg";
    }

    private static String codeFromSize(PhotoSize size) {
        switch (size) {
            case Square:
                return "_s";
            case LargeSquare:
                return "_q";
            case Thumbnail:
                return "_t";
            case Small:
                return "_m";
            case Medium:
                return "";
            case Large:
                return "_b";
            case Original:
                return "_o";
            default:
                return "";
        }
    }

}
