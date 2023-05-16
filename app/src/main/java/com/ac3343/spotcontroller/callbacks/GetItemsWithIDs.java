package com.ac3343.spotcontroller.callbacks;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.views.AlbumView;

import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetItemsWithIDs extends JSONObjectCallback{
    AlbumView av;
    public GetItemsWithIDs(CoreActivity p_app, AlbumView p_av) {
        super(p_app);
        av = p_av;
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        super.onSucceeded(request, info);
        try {
            JSONArray albums = returnedJSON.getJSONArray("albums");
            for(int i = 0; i < albums.length(); i++){
                JSONObject jsonAlbum = albums.getJSONObject(i);
                Album currentAlbum = new Album(jsonAlbum);
                app.AddAlbumToDB(currentAlbum);
                Looper l = Looper.getMainLooper();
                Handler h = new Handler(l);
                h.post(() -> {
                    av.setAlbumInfo(new DBAlbum(currentAlbum.getID(), currentAlbum.getName(), currentAlbum.getArtistIDs(),
                            currentAlbum.getMediumURI(), currentAlbum.getPlayURI(), currentAlbum.getRelease(), currentAlbum.getFirstArtist(), null, null));
                });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
