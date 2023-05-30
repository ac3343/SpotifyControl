package com.ac3343.spotcontroller.callbacks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.LibraryItems;
import com.ac3343.spotcontroller.SpotifyApplication;
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

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class GetLibraryAlbums extends UrlRequest.Callback {
    private static final String TAG = "GetLibraryAlbums";
    private static final Integer BUFFER_SIZE = 524000;
    private String jsonString;
    private CoreActivity app;
    private Thread onSuccess;
    private LibraryItems itemType;
    private int failedCount;

    public GetLibraryAlbums(LibraryItems type, CoreActivity p_app, Thread onSucceed){
        this(type, p_app);
        onSuccess = onSucceed;
    }

    public GetLibraryAlbums(LibraryItems type, CoreActivity p_app){
        itemType = type;
        app = p_app;
        failedCount = 0;
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
        //Log.i(TAG, "onResponseStarted method called.");
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
        //Log.i(TAG, "HTTP Code: " + httpStatusCode);
        jsonString = "";
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        //Log.i(TAG, "onReadCompleted method called.");
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
        //Log.i(TAG, "onSucceeded method called.");
        try {
            JSONObject jbers = new JSONObject(jsonString);
            JSONArray items = jbers.getJSONArray("items");
            int itemTotal = jbers.getInt("total");

            if(onSuccess != null){
                switch (itemType){
                    case Album:
                        if(app.GetReadAlbumCount() != itemTotal){
                            app.SetAlbumCount(itemTotal);
                        }
                        else{
                            return;
                        }
                        break;
                    case Track:
                        if(app.GetReadTrackCount() != itemTotal){
                            app.SetTrackCount(itemTotal);
                        }
                        else{
                            return;
                        }
                        break;
                    case Playlist:
                    case Artist:
                    default:
                        break;
                }

                onSuccess.start();
            }

            //Create list of albums
            for(int i = 0; i < items.length(); i++){
                Looper l = Looper.getMainLooper();
                Handler h = new Handler(l);

                switch (itemType){
                    case Album:
                        Album currentAlbum = new Album(items.getJSONObject(i).getJSONObject("album"));
                        currentAlbum.setAddDate((String) items.getJSONObject(i).get("added_at"));
                        app.AddAlbumToDB(currentAlbum);
                        break;
                    case Track:
                        Track t = new Track(items.getJSONObject(i).getJSONObject("track"));
                        t.setAddedDate((String) items.getJSONObject(i).get("added_at"));
                        DBTrack dbt = new DBTrack(t.getID(), t.getName(), t.getArtistIDs(), t.getArtistString(), t.getAlbumID(), null, false, t.getAddDate());
                        app.AddTrackToDB(dbt);
                        break;
                    default:
                        break;
                }
            }


        } catch (Exception e) {
            if(e.getClass() == JSONException.class){
                Log.e(TAG, "JSON Convert Fucked: " + jsonString);
            }
            request.cancel();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }
}
