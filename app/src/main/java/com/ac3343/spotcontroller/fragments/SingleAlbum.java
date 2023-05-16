package com.ac3343.spotcontroller.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.LibraryItems;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.callbacks.GetTrack;
import com.ac3343.spotcontroller.callbacks.JSONObjectCallback;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.library.Track;
import com.ac3343.spotcontroller.views.LoadingScrollView;
import com.ac3343.spotcontroller.views.TrackView;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

import java.util.List;

public class SingleAlbum extends Fragment {
    public SingleAlbum(){
        super(R.layout.fragment_tracks);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CoreActivity daddy = (CoreActivity) view.getContext();
        DBAlbum album = daddy.getCurrentAlbum();
        if(album == null){
            return;
        }
        ImageView imView = (ImageView) view.findViewById(R.id.albumImage);
        daddy.getRemote()
                .getImagesApi()
                .getImage(
                        new ImageUri(album.getmImageURI()),
                        Image.Dimension.LARGE)
                .setResultCallback(imView::setImageBitmap);

        LinearLayout albumInfoView = view.findViewById(R.id.albumInfo);
        LiveData<List<DBTrack>> dbTracks = daddy.GetAlbumTracks(album.mID);

        String trackIDString = album.mTrackIDs;
        if(trackIDString != null){
            String[] trackIDs = album.mTrackIDs.split(",");
            for (int i = 0; i < trackIDs.length; i++) {
                TrackView newView = daddy.CreateTrackView();
                albumInfoView.addView(newView);

                int index = i;
                LiveData<DBTrack> dbtLive = daddy.getTrackWithIDs(trackIDs[i]);
                dbtLive.observe(daddy, t->{
                    if(t != null){
                        newView.setTrackInfo(t);
                    }
                    else{
                        daddy.MakeURLRequest("https://api.spotify.com/v1/tracks/" + trackIDs[index], new GetTrack(daddy, newView));
                    }

                    newView.setOnClickListener(v -> {
                        TrackView clickedView = (TrackView) v;
                        DBTrack savedTrack = clickedView.getTrack();
                        daddy.getRemote().getPlayerApi().skipToIndex("spotify:album:" + savedTrack.mAlbumID, index);
                        daddy.addToRecentlyPlayed(album.getmID());
                    });
                });

            }
        }
        else{
            daddy.GetAlbumTracks(album);
        }

//        dbTracks.observe(daddy, dbTracks1 -> {
//            if(dbTracks1.size() > 0){
//                Log.i("SingleAlbum", "Hell yeah");
//
//                for (DBTrack t:
//                     dbTracks1) {
//                    TrackView newView = daddy.CreateTrackView();
//                    newView.setTrackInfo(t);
//                    albumInfoView.addView(newView);
//
//                    newView.setOnClickListener(v -> {
//                        TrackView clickedView = (TrackView) v;
//                        DBTrack savedTrack = clickedView.getTrack();
//                        daddy.getRemote().getPlayerApi().skipToIndex("spotify:album:" + savedTrack.mAlbumID, Integer.parseInt(t.mAlbumPos));
//                        daddy.addToRecentlyPlayed(album.getmID());
//                    });
//                }
//            }
//            else{
//                daddy.GetAlbumTracks(album);
//            }
//            dbTracks.removeObservers(daddy);
//        });

//        Track[] tracks = album.getTracks();
//        if(tracks != null){
//            for (int i = 0; i < album.getTrackCount(); i++) {
//                Track t = tracks[i];
//                TrackView newView = daddy.CreateTrackView();
//                newView.setTrackInfo(t);
//                albumInfoView.addView(newView);
//
//                int playIndex = i;
//                newView.setOnClickListener(v -> {
//                    TrackView clickedView = (TrackView) v;
//                    Track savedTrack = clickedView.getTrack();
//                    daddy.getRemote().getPlayerApi().skipToIndex(album.getPlayURI(), playIndex);
//                    daddy.addToRecentlyPlayed(album);
//                });
//            }
//        }
//        else{
//            daddy.GetAlbumTracks(album);
//        }
    }


}