package com.ac3343.spotcontroller.callbacks;

import android.os.Handler;
import android.os.Looper;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.library.Track;
import com.ac3343.spotcontroller.views.TrackView;

import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class GetTrack extends JSONObjectCallback{
    private TrackView tv;
    public GetTrack(CoreActivity p_app, TrackView p_tv) {
        super(p_app);
        tv = p_tv;
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        super.onSucceeded(request, info);
        Track t = new Track(returnedJSON);
        DBTrack dbTrack = new DBTrack(t.getID(), t.getName(), t.getArtistIDs(), t.getArtistString(), t.getAlbumID(), null, false, null);
        Looper l = Looper.getMainLooper();
        Handler h = new Handler(l);
        h.post(() -> {
            tv.setTrackInfo(dbTrack);
        });
    }
}
