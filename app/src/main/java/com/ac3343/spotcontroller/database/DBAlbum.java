package com.ac3343.spotcontroller.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "album_table")
public class DBAlbum {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="album_id")
    public String mID;
    @ColumnInfo(name = "album_name")
    public String mName;
    @ColumnInfo(name="artist_ids")
    public String mArtistIDs;
    @ColumnInfo(name="artist_display_name")
    public String mArtistName;
    @ColumnInfo(name="album_image")
    public String mImageURI;
    @ColumnInfo(name="album_uri")
    public String mPlayURI;
    @ColumnInfo(name="album_release")
    public String mDate;
    @ColumnInfo(name="added_date")
    public String mAddedDate;
    @ColumnInfo(name="track_ids")
    public String mTrackIDs;

    public DBAlbum(@NonNull String albumId, String name, String artistIDs, String imageURI, String playURI, String date, String artistName, String addedDate, String trackIDs){
        this.mID = albumId;
        this.mName = name;
        this.mArtistIDs = artistIDs;
        this.mImageURI = imageURI;
        this.mPlayURI = playURI;
        this.mDate = date;
        this.mArtistName = artistName;
        this.mAddedDate = addedDate;
        this.mTrackIDs = trackIDs;
    }

    public DBAlbum(){
        this("null", null, null, null, null, null, null, null, null);
    }

    @NonNull
    public String getmID() {
        return this.mID;
    }

    public String getmName() {
        return this.mName;
    }

    public String getmArtistIDs() {
        return this.mArtistIDs;
    }

    public String getmImageURI() {
        return this.mImageURI;
    }

    public String getmPlayURI() {
        return this.mPlayURI;
    }

    public String getmDate() {
        return this.mDate;
    }
    public String  getMediumURI(){
        return mImageURI.split(",")[1];
    }

    public String getmArtistName(){return this.mArtistName;}
    public String getmAddedDate(){return this.mAddedDate;}
    public String getmTrackIDs(){return this.mTrackIDs;}
}
