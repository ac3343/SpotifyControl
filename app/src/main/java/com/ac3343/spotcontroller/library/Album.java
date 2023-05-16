package com.ac3343.spotcontroller.library;

import android.net.Uri;
import android.util.Log;

import com.ac3343.spotcontroller.database.DBAlbum;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

enum AlbumTypes{
    Album,
    Single,
    Compilation
}

public class Album {
    private String mName;
    private int mtrackCount;
    private Track[] mTracks;
    private String[] mImageURLs;
    private String mRelease;
    private Artist[] mArtists;
    private JSONObject source;
    private String mPlayURI;
    private String mId;
    private String mArtistIds;
    private String mAddDate;

    public Album(JSONObject album){
        source = album;
        try {
            mName = (String) album.get("name");
            mtrackCount = (Integer) album.get("total_tracks");
            JSONArray images  = album.getJSONArray("images");
            mRelease = (String) album.get("release_date");
            mPlayURI = (String) album.get("uri");
            mId = (String) album.get("id");


            mImageURLs = new String[3];
            for(int i = 0; i < 3; i++){
                mImageURLs[i] = (String) images.getJSONObject(i).get("url");
            }

            JSONArray artists  = album.getJSONArray("artists");
            mArtists = new Artist[artists.length()];
            StringBuilder artistIDs = new StringBuilder();
            for(int i = 0; i < artists.length(); i++){
                mArtists[i] = new Artist(artists.getJSONObject(i));
                artistIDs.append(mArtists[i].mID);
                artistIDs.append(',');
            }
            mArtistIds = artistIDs.toString();

            if(album.has("tracks")){
                JSONArray tracks = album.getJSONObject("tracks").getJSONArray("items");
                mTracks = new Track[tracks.length()];
                for(int i = 0; i < tracks.length(); i++){
                    mTracks[i] = new Track(tracks.getJSONObject(i));
                }
            }

        }catch (Exception e){
            String message = "Failed to create Album: " + e.getMessage();
            Log.i("ALBUM", message);
        }
    }

    public Album(DBAlbum dba){
        mName = dba.getmName();
        mRelease = dba.mDate;
        mPlayURI = dba.mPlayURI;
        mId = dba.mID;
        mImageURLs = new String[3];
        mImageURLs[1] = dba.mImageURI;
        mArtistIds = dba.mArtistIDs;
    }

    public void SetTracks(Track[] tracks){
        mTracks = tracks;
    }
    public void setAddDate(String s){mAddDate = s;}

    public String getName(){
        return mName;
    }
    public String getFirstArtist(){
        return mArtists[0].getName();
    }

    public String getSmallURI(){
        return mImageURLs[2];
    }
    public String getMediumURI(){
        return mImageURLs[1];
    }
    public String getLargeURI(){
        return mImageURLs[0];
    }
    public String getStorableString(){return source.toString();}
    public int getTrackCount(){return mTracks.length;}
    public Track[] getTracks(){return mTracks;}
    public String getPlayURI(){return mPlayURI;}
    public String getID(){return mId;}

    public String getRelease() {
        return mRelease;
    }
    public String getArtistIDs(){
        return mArtistIds;
    }

    public Artist[] getArtists(){
        return mArtists;
    }

    public String getAddDate() {
        return mAddDate;
    }
}
