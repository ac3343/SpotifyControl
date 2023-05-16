package com.ac3343.spotcontroller.library;

import android.util.Log;

import org.json.JSONObject;

public class Artist {
    String mName;
    String mURI;
    String mID;

    public Artist(JSONObject artist){
        mName = "artist";
        try {
            mName = (String) artist.get("name");
            mURI = (String) artist.get("uri");
            mID = (String) artist.get("id");
        }catch (Exception e){
            String message = "Failed to create Artist: " + e.getMessage();
            Log.i("ARTIST", message);
        }
    }

    public Artist(String name, String URI, String ID){
        mName = name;
        mID = ID;
        mURI = URI;
    }

    public String getName(){
        return mName;
    }
    public String getID(){return mID;}
    public String getURI(){return mURI;}
}
