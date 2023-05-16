package com.ac3343.spotcontroller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ac3343.spotcontroller.callbacks.GetAlbumTracks;
import com.ac3343.spotcontroller.callbacks.GetLibraryAlbums;
import com.ac3343.spotcontroller.callbacks.SearchCallback;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.database.DBArtist;
import com.ac3343.spotcontroller.database.DBTrack;
import com.ac3343.spotcontroller.database.VMLibrary;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.library.Artist;
import com.ac3343.spotcontroller.library.Track;
import com.ac3343.spotcontroller.views.AlbumView;
import com.ac3343.spotcontroller.views.TrackView;
import com.google.android.gms.net.CronetProviderInstaller;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CoreActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "8b2c16b69f9c49188f16d3c9eaebc154";
    private static final String REDIRECT_URI = "http://com.ac3343.spotcontroller/callback";
    private static final int REQUEST_CODE = 1337;
    AuthorizationResponse authResponse;
    private int trackPosition;
    private int albumPosition;
    private boolean initialCallDone;
    private SpotifyApplication spotApp;
    private DBAlbum currentAlbum;
    private Track[] trackArray;
    private Album[] albumArray;
    private List<String> recentlyPlayed;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean rpNeedsUpdate;
    private boolean allAlbumsGenerated;
    private final String RP_FILE = "recentlyplayed";
    private final String TRACKS_FILE = "savedtracks";
    private final String ALBUMS_FILE = "savedalbums";
    List<Runnable> onRemoteConnect;
    List<Runnable> onAuthConnect;
    private VMLibrary mVMLibrary;
    private int albumCount;
    private int trackCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        onRemoteConnect = new ArrayList<>();
        onAuthConnect = new ArrayList<>();

        mVMLibrary = new ViewModelProvider(this).get(VMLibrary.class);
        mVMLibrary.getAllAlbums().observe(this, albums -> {
            //Log.i("AlbumDB", "added album");
            //Log.i("AlbumDB", "Albums size: " + String.valueOf(albums.size()));

        });

        recentlyPlayed = new ArrayList<>();
        FileInputStream fis = null;

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment primaryNav = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(primaryNav != null){
                    NavHostFragment.findNavController(primaryNav)
                            .navigate(R.id.tosearch);
                }
            }
        });

        Button libraryButton = (Button) findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment primaryNav = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(primaryNav != null){
                    NavHostFragment.findNavController(primaryNav)
                            .navigate(R.id.tolibrary);
                }
            }
        });

        try {
            fis = openFileInput(RP_FILE);
            Scanner scanner = new Scanner(fis);
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            scanner.close();

            String[] fileAlbums = content.split("\n");
            recentlyPlayed.addAll(Arrays.asList(fileAlbums));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.i("CORE", "Gibberish finished");
    }

    @Override
    protected void onStart() {
        super.onStart();
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "user-library-read"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
        //spotApp = (SpotifyApplication) getApplication();
        rpNeedsUpdate = false;
        allAlbumsGenerated = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setAllAlbumsGenerated(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            if(data != null)
                Log.i("LOGIN", "request returned: " + resultCode);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    authResponse = response;
                    Log.i("MAIN", "Web Auth gotten");

                    Thread getLibraryAlbumsThread = new Thread(()->{
                        //mVMLibrary.deleteAllAlbums();

                        LibraryItems type = LibraryItems.Album;
                        int limit = GetLimit(type);
                        String requestURL = "https://api.spotify.com/v1/me/";
                        requestURL += GetRequstType(type);
                        requestURL += "limit=";
                        requestURL += limit;
                        final String requestURLBase = requestURL;

                        Thread getTheRest = new Thread(()->{
                           for(int i = limit; i < albumCount - limit; i += limit){
                               String newRequestURL = requestURLBase;
                               newRequestURL +="&offset=";
                               newRequestURL += i;
                               GetLibraryAlbums callback = new GetLibraryAlbums(type, this);
                               MakeURLRequest(newRequestURL, callback);
                           }
                        });

                        GetLibraryAlbums callback = new GetLibraryAlbums(type, this, getTheRest);
                        MakeURLRequest(requestURL, callback);
                    });

                    Thread getLibraryTracksThread = new Thread(()->{
                        //mVMLibrary.deleteAllAlbums();

                        LibraryItems type = LibraryItems.Track;
                        int limit = GetLimit(type);
                        String requestURL = "https://api.spotify.com/v1/me/";
                        requestURL += GetRequstType(type);
                        requestURL += "limit=";
                        requestURL += limit;
                        final String requestURLBase = requestURL;

                        Thread getTheRest = new Thread(()->{
                            for(int i = limit; i < trackCount - limit; i += limit){
                                String newRequestURL = requestURLBase;
                                newRequestURL +="&offset=";
                                newRequestURL += i;
                                GetLibraryAlbums callback = new GetLibraryAlbums(type, this);
                                MakeURLRequest(newRequestURL, callback);
                            }
                        });

                        GetLibraryAlbums callback = new GetLibraryAlbums(type, this, getTheRest);
                        MakeURLRequest(requestURL, callback);
                    });

                    getLibraryAlbumsThread.start();
                    getLibraryTracksThread.start();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;
                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private int GetLimit(LibraryItems type){
        switch(type){
            case Album:
                return 5;
            case Track:
                return 25;
            case Artist:
                return 7;
            case Playlist:
                return 7;
            default:
                return 0;
        }
    }

    private String GetRequstType(LibraryItems type){
        switch(type){
            case Album:
                return "albums?";
            case Track:
                return "tracks?";
            case Artist:
                return "following?type=artist&";
            case Playlist:
                return "playlists?";
            default:
                return "";
        }
    }
    public boolean isSavedAlbumsSet(){
        return albumArray != null;
    }

    public void createSavedAlbumsArray(final int albumCount){
        albumArray = new Album[albumCount];
    }
    public DBAlbum getCurrentAlbum(){
        return currentAlbum;
    }

    public void saveTrack(Track t, final int position){
        trackArray[position] = t;
    }

    public boolean isSavedTracksSet(){
        return trackArray != null;
    }

    public void createSavedTracksArray(final int trackCount){
        trackArray = new Track[trackCount];
    }
    public List<String> getRecentlyPlayed(){
        return recentlyPlayed;
    }

    public AlbumView CreateAlbumView(){
        AlbumView newView = new AlbumView(this);
        View.OnClickListener openAlbum = view -> {
            AlbumView clickedView = (AlbumView) view.getParent();
            DBAlbum savedAlbum = clickedView.getAlbum();
            if(savedAlbum != null){
                currentAlbum = savedAlbum;
                Fragment primaryNav = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(primaryNav != null){
                    NavHostFragment.findNavController(primaryNav)
                            .navigate(R.id.opensinglealbum);
                }
            }
        };

        View.OnClickListener removeAlbum = view -> {
            AlbumView clickedView = (AlbumView) view.getParent();
            DBAlbum savedAlbum = clickedView.getAlbum();
            recentlyPlayed.removeIf(album -> Objects.equals(album, savedAlbum.getmID()));
            saveRecentlyPlayed();
            clickedView.removeAllViews();
        };
        newView.setOnClicks(openAlbum, removeAlbum);

        return newView;
    }

    public TrackView CreateTrackView(){
        TrackView newView = new TrackView(this);
        newView.setOnClickListener(view -> {
            TrackView clickedView = (TrackView) view;
            DBTrack savedTrack = clickedView.getTrack();
            if(savedTrack != null){
                getRemote().getPlayerApi().play("spotify:track:" + savedTrack.mID);
            }
        });

        return newView;
    }

    public void saveAlbum(Album a, final int position){
        albumArray[position] = a;
    }
    public SpotifyAppRemote getRemote(){
        if(mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected())
            return mSpotifyAppRemote;

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("SpotifyApplication", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        //connected();
                        for (Runnable r:
                             onRemoteConnect) {
                            r.run();
                        }

                        for (Runnable r:
                                onAuthConnect) {
                            r.run();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("SpotifyApplication", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
        return mSpotifyAppRemote;
    }

    public void setAllAlbumsGenerated(boolean newVal){
        allAlbumsGenerated = newVal;
    }
    public boolean areAllItemsGenerated(int newPosition, LibraryItems type){
        if(type == LibraryItems.Album){
            if(albumArray != null){
                return newPosition >= albumArray.length;
            }
            else{
                return false;
            }
        }
        else if(type == LibraryItems.Track){
            if(trackArray != null){
                return newPosition >= trackArray.length;
            }
            else{
                return false;
            }
        }
        else{
            return true;
        }
    }
    public int getAlbumCount(){
        return albumArray.length;
    }

    public void addToRecentlyPlayed(String a){
        if(recentlyPlayed.indexOf(a) == 0){
            return;
        }
        recentlyPlayed.remove(a);
        recentlyPlayed.add(0, a);
        rpNeedsUpdate = true;

        saveRecentlyPlayed();
    }

    public void removeFromRecentlyPlayed(String a){
        recentlyPlayed.remove(a);
        rpNeedsUpdate = true;

        saveRecentlyPlayed();
    }

    private void saveRecentlyPlayed(){
        StringBuilder rpString = new StringBuilder();

        for(int i = 0; i < recentlyPlayed.size(); i++){
            rpString.append(recentlyPlayed.get(i));
            rpString.append('\n');
        }

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(RP_FILE, Context.MODE_PRIVATE);
            fos.write(rpString.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setOnRemoteConnect(Runnable r){
        onRemoteConnect.add(r);
    }

    public void search(String s){
        CronetProviderInstaller.installProvider(getApplicationContext());
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(getApplicationContext());
        CronetEngine cronetEngine = myBuilder.build();

        Executor executor = Executors.newSingleThreadExecutor();

        String requestURL = "https://api.spotify.com/v1/search";
        requestURL += "?q=";
        requestURL += s;
        requestURL += "&type=album,track,artist";
        requestURL += "&limit=5";

        SearchCallback callback = new SearchCallback(this);

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                requestURL, callback, executor);

        UrlRequest request = requestBuilder.addHeader("Authorization", "Bearer " + authResponse.getAccessToken()).build();

        request.start();
    }

    public void GetAlbumTracks(DBAlbum dba){
        String requestURL = "https://api.spotify.com/v1/albums/";
        requestURL += dba.getmID();
        requestURL += "/tracks";

        GetAlbumTracks callback = new GetAlbumTracks(this, dba);

        MakeURLRequest(requestURL, callback);
    }

    public void AddAlbumToDB(Album a){
        StringBuilder trackIDs = new StringBuilder();
        for(Track t:
        a.getTracks()){
            trackIDs.append(t.getID());
            trackIDs.append(',');
        }
        DBAlbum dba = new DBAlbum(a.getID(), a.getName(), a.getArtistIDs(), a.getMediumURI(), a.getPlayURI(), a.getRelease(), a.getFirstArtist(), a.getAddDate(),trackIDs.toString());
        mVMLibrary.insertAlbum(dba);
    }
    public void AddTrackToDB(DBTrack t){
        mVMLibrary.insertTrack(t);
    }
    public void AddArtistToDB(Artist a){
        DBArtist dba = new DBArtist(a.getID(), a.getName(), a.getURI());
        mVMLibrary.insertArtist(dba);
    }

    public LiveData<DBTrack> getTrackWithIDs(String id){return mVMLibrary.getTrackWithIDs(id);}
    public LiveData<List<DBAlbum>> getLibraryAlbums(){return mVMLibrary.getAllAlbums();}
    public LiveData<List<DBTrack>> getLibraryTracks(){return mVMLibrary.getLibraryTracks();}

    public LiveData<List<DBTrack>> GetAlbumTracks(String albumID){
        return mVMLibrary.getAlbumTracks(albumID);
    }

    public LiveData<List<DBAlbum>> GetAlbumsWithID(String[] ids){
        return mVMLibrary.getAlbumsWithID(ids);
    }

    public LiveData<List<DBAlbum>> SearchForAlbumName(String name){
        return mVMLibrary.getSomeAlbums(name);
    }

    public LiveData<List<DBTrack>> SearchForTrackName(String name){
        return mVMLibrary.getSomeTracks(name);
    }

    public void MakeURLRequest(String requestURL, UrlRequest.Callback callback){
        Runnable r = () -> {
            CronetProviderInstaller.installProvider(getApplicationContext());
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(getApplicationContext());
            CronetEngine cronetEngine = myBuilder.build();

            Executor executor = Executors.newSingleThreadExecutor();

            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    requestURL, callback, executor);

            UrlRequest request = requestBuilder.addHeader("Authorization", "Bearer " + authResponse.getAccessToken()).build();

            request.start();
        };

        if(authResponse != null){
            r.run();
        }
        else{
            onAuthConnect.add(r);
        }
    }

    public void SetAlbumCount(int count){
        albumCount = count;
    }
    public void SetTrackCount(int count) { trackCount = count;}
}