package com.ac3343.spotcontroller.library;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Track {
    private String mSoloUri;
    private String mName;
    private Artist[] mArtists;
    private int mTrackNumber;
    private int mDiscNumber;
    private String mID;
    private JSONObject source;
    private String mArtistIds;
    private String mAddDate;
    private String mAlbumID;


    public Track(JSONObject track){
        try {
            mName = (String) track.get("name");
            mSoloUri = (String) track.get("uri");
            JSONArray artists  = track.getJSONArray("artists");
            mArtists = new Artist[artists.length()];
            for(int i = 0; i < artists.length(); i++){
                mArtists[i] = new Artist(artists.getJSONObject(i));
            }
            mTrackNumber = track.getInt("track_number");
            mDiscNumber = track.getInt("disc_number");
            source = track;
            mID = (String) track.get("id");

            if(!track.isNull("album")){
                mAlbumID = (String) track.getJSONObject("album").get("id");
            }

            StringBuilder artistIDs = new StringBuilder();
            for(int i = 0; i < artists.length(); i++){
                mArtists[i] = new Artist(artists.getJSONObject(i));
                artistIDs.append(mArtists[i].mID);
                artistIDs.append(',');
            }
            mArtistIds = artistIDs.toString();


        }catch (Exception e){
            String message = "Failed to create Track: " + e.getMessage();
            Log.i("TRACK", message);
        }
    }

    public String getName(){return mName;}
    public String getURI(){return mSoloUri;}
    public String getID(){return mID;}
    public String getArtistString(){ return mArtists[0].getName();}

    public String getStorableString(){return source.toString();}
    public String getArtistIDs(){return mArtistIds;}

    public String getAddDate() {
        return mAddDate;
    }

    public String getAlbumID() {
        return mAlbumID;
    }

    public int getAlbumPos() {
        return mTrackNumber + mDiscNumber;
    }

    public void setAddedDate(String ad){mAddDate = ad;}
}
