package com.ac3343.spotcontroller;

import static com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID;
import static com.spotify.sdk.android.auth.AccountsQueryParameters.REDIRECT_URI;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.ac3343.spotcontroller.fragments.Library;
import com.ac3343.spotcontroller.library.Album;
import com.ac3343.spotcontroller.library.Track;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpotifyApplication extends Application {
    private List<Album> savedAlbums;
    private Track[] trackArray;
    private Album[] albumArray;
    private List<Album> recentlyPlayed;
    private SpotifyAppRemote mSpotifyAppRemote;

    private static final String CLIENT_ID = "8b2c16b69f9c49188f16d3c9eaebc154";
    private static final String REDIRECT_URI = "http://com.ac3343.spotcontroller/callback";
    private boolean rpNeedsUpdate;
    private boolean allAlbumsGenerated;

    private final String RP_FILE = "recentlyplayed";

    @Override
    public void onCreate() {
        super.onCreate();
        //getRemote();
        rpNeedsUpdate = false;
        allAlbumsGenerated = false;
    }

    public Album[] getSavedAlbums(){
        return albumArray;
    }

    public Track[] getSavedTracks(){
        return trackArray;
    }

    public void saveAlbum(Album a, final int position){
        albumArray[position] = a;
    }

    public boolean isSavedAlbumsSet(){
        return albumArray != null;
    }

    public void createSavedAlbumsArray(final int albumCount){
        albumArray = new Album[albumCount];
    }

    public int getAlbumCount(){
        return albumArray.length;
    }

    public void addToRecentlyPlayed(Album a){
        if(recentlyPlayed == null){
            recentlyPlayed = new ArrayList<>();
            FileInputStream fis = null;
            try {
                fis = openFileInput(RP_FILE);
                Scanner scanner = new Scanner(fis);
                scanner.useDelimiter("\\Z");
                String content = scanner.next();
                scanner.close();

                String[] fileAlbums = content.split("\n");
                for(int i = 0; i < fileAlbums.length; i++){
                    JSONObject newJSON = new JSONObject(fileAlbums[i]);
                    Album newAl = new Album(newJSON);
                    recentlyPlayed.add(newAl);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(recentlyPlayed.indexOf(a) == 0){
            return;
        }
        recentlyPlayed.remove(a);
        recentlyPlayed.add(0, a);
        rpNeedsUpdate = true;

        StringBuilder rpString = new StringBuilder();

        for(int i = 0; i < recentlyPlayed.size(); i++){
            rpString.append(recentlyPlayed.get(i).getStorableString());
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

    public List<Album> getRecentlyPlayed(){
        return recentlyPlayed;
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

    public int getTrackCount(){
        return trackArray.length;
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
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("SpotifyApplication", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
        return mSpotifyAppRemote;
    }

    public boolean isRpNeedsUpdate() {
        return rpNeedsUpdate;
    }

    public void setAllAlbumsGenerated(boolean newVal){
        allAlbumsGenerated = newVal;
    }

    public boolean isAllAlbumsGenerated(){
        return allAlbumsGenerated;
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
