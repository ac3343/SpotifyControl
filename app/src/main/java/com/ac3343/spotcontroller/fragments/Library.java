package com.ac3343.spotcontroller.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.SpotifyApplication;
import com.ac3343.spotcontroller.callbacks.GetItemsWithIDs;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.databinding.FragmentLibraryBinding;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.views.AlbumView;

import org.json.JSONObject;

import java.io.Console;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Library extends Fragment {
    View view;
    final String  TAG = "LIB";
    public Library(){
        super(R.layout.fragment_library);
    }
    private final String RP_FILE = "recentlyplayed";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Button albumsButton = (Button) view.findViewById(R.id.button9);
        albumsButton.setOnClickListener(view -> NavHostFragment.findNavController(Library.this)
                .navigate(R.id.action_library_to_albums2));
        Button tracksButton = (Button) view.findViewById(R.id.button8);
        tracksButton.setOnClickListener(view -> NavHostFragment.findNavController(Library.this)
                .navigate(R.id.action_library_to_tracks2));

        CreateRPSection();
    }

    public void CreateRPSection(){
        CoreActivity core = (CoreActivity) view.getContext();
        List<String> albums = core.getRecentlyPlayed();
        StringBuilder ids = new StringBuilder();
        LiveData<List<DBAlbum>> dbAlbms = core.GetAlbumsWithID(albums.toArray(new String[0]));

        LinearLayout list = (LinearLayout) view.findViewById(R.id.recentlyPlayed);
        list.removeAllViews();

        dbAlbms.observe(core, dbAlbums -> {
            Log.i("library", "RP albums asked for: " + albums.size() + ", Albums gotten:" + dbAlbums.size());

            for(int i = 0; i < albums.size(); i++){
                int index = i;
                List<DBAlbum> thing = dbAlbums.stream().filter(a -> Objects.equals(a.getmID(), albums.get(index))).collect(Collectors.toList());

                AlbumView newView = core.CreateAlbumView();
                if(thing.size() > 0){
                    DBAlbum dba = thing.get(0);
                    newView.setAlbumInfo(dba);
                    newView.makeRecentlyPlayed();
                }
                else {
                    StringBuilder request = new StringBuilder();
                    request.append("https://api.spotify.com/v1/albums/");
                    request.append("?ids=");
                    request.append(albums.get(index));

                    GetItemsWithIDs callback = new GetItemsWithIDs(core, newView);

                    core.MakeURLRequest(request.toString(), callback);
                }
                list.addView(newView);
            }

            dbAlbms.removeObservers(core);
        });


        for(int i = 0; i < albums.size(); i++){
//            Album a = albums.get(i);
//            AlbumView newView = core.CreateAlbumView();
//            DBAlbum dba = new DBAlbum(a.getID(), a.getName(), a.getArtistIDs(), a.getMediumURI(), a.getPlayURI(), a.getRelease(), a.getFirstArtist());
//            newView.setAlbumInfo(dba);
//
//            list.addView(newView);
        }
    }
}