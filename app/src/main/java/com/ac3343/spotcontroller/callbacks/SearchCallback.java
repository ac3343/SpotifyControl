package com.ac3343.spotcontroller.callbacks;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.library.Artist;
import com.ac3343.spotcontroller.library.Track;
import com.ac3343.spotcontroller.views.AlbumView;
import com.ac3343.spotcontroller.views.TrackView;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SearchCallback extends UrlRequest.Callback {
    private final String TAG = "SEARCH";
    private static final Integer BUFFER_SIZE = 524000;
    String jsonString;
    private View[] itemViews;
    private CoreActivity core;

    public SearchCallback(CoreActivity p_core){
        core = p_core;
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        //Log.i(TAG, "onRedirectReceived method called.");
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        Log.i(TAG, "onResponseStarted method called.");
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes for the read() method. The same buffer
        // with data is passed to the onReadCompleted() method.

        int httpStatusCode = info.getHttpStatusCode();
        if (httpStatusCode == 200) {
            // The request was fulfilled. Start reading the response.
            request.read(ByteBuffer.allocateDirect(BUFFER_SIZE));
        } else if (httpStatusCode == 503) {
            // The service is unavailable. You should still check if the request
            // contains some data.
            request.read(ByteBuffer.allocateDirect(BUFFER_SIZE));
            Log.i(TAG, "We're cooked bud");
        }
        else{
            Log.i(TAG, "bad status code: " +httpStatusCode);
        }

        jsonString = "";
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        Log.i(TAG, "onReadCompleted method called.");
        // The response body is available, process byteBuffer
        byteBuffer.rewind();
        String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
        String trimmed = s.trim();
        jsonString += trimmed;

        // Continue reading the response body by reusing the same buffer
        // until the response has been completed.
        byteBuffer.clear();
        request.read(ByteBuffer.allocateDirect(BUFFER_SIZE));
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
        try{
            JSONObject jbers = new JSONObject(jsonString);
            JSONArray tracks = jbers.getJSONObject("tracks").getJSONArray("items");
            JSONArray artists = jbers.getJSONObject("artists").getJSONArray("items");
            JSONArray albums = jbers.getJSONObject("albums").getJSONArray("items");

            Looper l = Looper.getMainLooper();
            Handler h = new Handler(l);

            LinearLayout trackList = core.findViewById(R.id.trackSearch);
            h.post(trackList::removeAllViews);
            for(int i = 0; i < tracks.length(); i++){
                Track t = new Track(tracks.getJSONObject(i));
                TrackView tv = core.CreateTrackView();
                DBTrack dbt = new DBTrack(t.getID(), t.getName(), t.getArtistIDs(), t.getArtistString(), null, String.valueOf(i), false, null);
                tv.setTrackInfo(dbt);
                tv.AddSearchDetails(tracks.getJSONObject(i).getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url"));
                h.post(() ->{
                    trackList.addView(tv);
                });
                Log.i(TAG, t.getName());
            }

            for(int i = 0; i < artists.length(); i++){
                Artist a =  new Artist(artists.getJSONObject(i));
                Log.i(TAG, a.getName());
            }

            LinearLayout albumList = core.findViewById(R.id.albumSearch);
            h.post(albumList::removeAllViews);
            for(int i = 0; i < albums.length(); i++){
                Album a = new Album(albums.getJSONObject(i));
                AlbumView av = core.CreateAlbumView();
                DBAlbum dba = new DBAlbum(a.getID(), a.getName(), a.getArtistIDs(), a.getMediumURI(), a.getPlayURI(), a.getRelease(), a.getFirstArtist(), null, null);
                av.setAlbumInfo(dba);
                av.AddSearchDetails();
                h.post(()->{
                    albumList.addView(av);
                });
                Log.i(TAG, a.getName());
            }
        }catch (Exception e){
            if(e.getClass() == JSONException.class){
                Log.e(TAG, "JSON Convert Fucked: " + jsonString);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }
}
