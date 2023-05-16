package com.ac3343.spotcontroller.views;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.library.Track;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

public class TrackView extends LinearLayout {
    private TextView trackName;
    private TextView artistName;
    private DBTrack savedTrack;

    public TrackView(Context context) {
        super(context);
        super.setOrientation(LinearLayout.HORIZONTAL);

        trackName = new TextView(context);
        artistName = new TextView(context);

        trackName.setAllCaps(false);
        artistName.setAllCaps(false);
        trackName.setSingleLine(true);
        artistName.setSingleLine(true);
        artistName.setTextColor(getResources().getColor(R.color.cardview_light_background));
        trackName.setTextColor(getResources().getColor(R.color.cardview_light_background));
        artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        trackName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.addView(trackName);
        textLayout.addView(artistName);
        textLayout.setPadding(10, 0, 0, 0);

        setPadding(0, 5, 0, 5);

        addView(textLayout);

        setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));



    }

    public void setTrackInfo(DBTrack info){
        savedTrack = info;
        trackName.setText(info.mName );
        artistName.setText(info.mArtistName);

        invalidate();
        requestLayout();
    }

    public void AddSearchDetails(String imageURI){
        String searchText = "Song - " + savedTrack.mArtistName;
        artistName.setText(searchText);

        ImageView albumCover = new ImageView(getContext());
        albumCover.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        addView(albumCover, 0);

        CoreActivity core = (CoreActivity) getContext();
        SpotifyAppRemote remote = core.getRemote();

        if(remote != null){
            remote.getImagesApi().getImage(new ImageUri(imageURI), Image.Dimension.THUMBNAIL).setResultCallback((imageBitMap) ->{
                albumCover.setImageBitmap(imageBitMap);
            });
        }{
            core.setOnRemoteConnect(() ->{
                core.getRemote().getImagesApi().getImage(new ImageUri(imageURI), Image.Dimension.THUMBNAIL).setResultCallback((imageBitMap) ->{
                    albumCover.setImageBitmap(imageBitMap);
                });
            });
        }

        invalidate();
        requestLayout();
    }

    public DBTrack getTrack(){
        return savedTrack;
    }
}
