package com.ac3343.spotcontroller.views;

import android.content.Context;
import android.content.Entity;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LiveData;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.LibraryItems;
import com.ac3343.spotcontroller.SpotifyApplication;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.library.Track;

import java.util.List;
import java.util.Objects;

public class LoadingScrollView extends NestedScrollView {
    LibraryItems typeToLoad;
    LinearLayout albums;
    private boolean gettingNewItems = false;
    private CoreActivity core;
    private SpotifyApplication application;

    private int position;

    List<DBAlbum> activeAlbumList;
    List<DBTrack> activeTrackList;
    private final int NUM_TO_DRAW = 25;
    private final String TAG = "LSV";

    public LoadingScrollView(@NonNull Context context) {
        super(context);
    }

    public LoadingScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(core == null){
            core = (CoreActivity) getContext();
        }
        int maxScrollPos = albums.getMeasuredHeight() - 2010;
        if(t > maxScrollPos * .65){
            CreateItems();
            //gettingNewItems = true;
        }
        //Log.i(TAG, "Current pos: " + t + "/" + maxScrollPos);
    }

    private void CreateItems(){
        int listSize = 0;

        switch (typeToLoad){
            case Album:
                listSize = activeAlbumList.size();
                break;
            case Track:
                listSize = activeTrackList.size();
                break;
            case Playlist:
                break;
            case Artist:
                break;
        }

        if(position * NUM_TO_DRAW > listSize){
            return;
        }
        //Iterates position
        position++;

        int offset = position * NUM_TO_DRAW;

        for(int i = offset; i < (Math.min(listSize, offset + NUM_TO_DRAW)); i++){
            LinearLayout newView;
            switch (typeToLoad){
                case Album:
                    newView = core.CreateAlbumView();
                    AlbumView av = (AlbumView) newView;
                    av.setAlbumInfo(activeAlbumList.get(i));
                    break;
                case Track:
                    newView = core.CreateTrackView();
                    TrackView tv = (TrackView) newView;
                    tv.setTrackInfo(activeTrackList.get(i));
                    break;
                case Playlist:
                case Artist:
                default:
                    continue;
            }

            albums.addView(newView);
        }
    }

    public void setLinearLayout(LinearLayout l, LibraryItems type){
        albums = l;
        typeToLoad = type;
        core = (CoreActivity) getContext();

        position = -1;
        switch (typeToLoad){
            case Album:
                activeAlbumList = core.getLibraryAlbums().getValue();
                if(activeAlbumList == null){
                    core.getLibraryAlbums().observe(core, list ->{
                        activeAlbumList = list;
                        CreateItems();
                        core.getLibraryAlbums().removeObservers(core);
                    });
                }
                else{
                    CreateItems();
                }
                break;
            case Track:
                activeTrackList = core.getLibraryTracks().getValue();
                if(activeTrackList == null){
                    core.getLibraryTracks().observe(core, list -> {
                        activeTrackList = list;
                        Log.i(TAG, "Track list size: " + activeTrackList.size());
                        CreateItems();
                        core.getLibraryAlbums().removeObservers(core);
                    });
                }
                else{
                    CreateItems();
                    Log.i(TAG, "Track list size: " + activeTrackList.size());
                }
                break;
            case Playlist:
                break;
            case Artist:
                break;
        }
    }

    public void search(String term){
        //Clears layout
        albums.removeAllViews();
        position = -1;

        if(Objects.equals(term, "")){
            switch (typeToLoad){

                case Album:
                    activeAlbumList = core.getLibraryAlbums().getValue();
                    break;
                case Track:
                    activeTrackList = core.getLibraryTracks().getValue();
                    break;
                case Playlist:
                case Artist:
                default:
                    break;
            }
            CreateItems();
        }
        else{
            switch (typeToLoad){
                case Album:
                    LiveData<List<DBAlbum>> albumSearch = core.SearchForAlbumName("%" + term + "%");

                    albumSearch.observe(core, list ->{
                        if(list != null && list.size() > 0){
                            activeAlbumList = list;
                            CreateItems();
                        }
                        else{
                            Log.i(TAG, "not a thing");
                        }
                        albumSearch.removeObservers(core);
                    });
                    break;
                case Track:
                    LiveData<List<DBTrack>> trackSearch = core.SearchForTrackName("%" + term + "%");

                    trackSearch.observe(core, list ->{
                        if(list != null && list.size() > 0){
                            activeTrackList = list;
                            CreateItems();
                        }
                        else{
                            Log.i(TAG, "not a thing");
                        }
                        trackSearch.removeObservers(core);
                    });
                    break;
                case Playlist:
                case Artist:
                default:
                    break;
            }

        }
    }
}
