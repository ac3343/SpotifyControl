package com.ac3343.spotcontroller.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "track_table")
public class DBTrack {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="track_id")
    public String mID;
    @ColumnInfo(name = "track_name")
    public String mName;
    @ColumnInfo(name="artist_ids")
    public String mArtistIDs;
    @ColumnInfo(name="artist_display_name")
    public String mArtistName;
    @ColumnInfo(name="album_id")
    public String mAlbumID;
    @ColumnInfo(name="album_position")
    public String mAlbumPos;
    @ColumnInfo(name = "in_library")
    public Boolean mInLibrary;
    @ColumnInfo(name="added_date")
    public String mAddedDate;

    public DBTrack(@NonNull String mID, String mName, String mArtistIDs, String mArtistName, String mAlbumID, String mAlbumPos, Boolean mInLibrary, String mAddedDate){
        this.mID = mID;
        this.mName = mName;
        this.mArtistIDs = mArtistIDs;
        this.mArtistName = mArtistName;
        this.mAlbumID = mAlbumID;
        this.mAlbumPos = mAlbumPos;
        this.mInLibrary = mInLibrary;
        this.mAddedDate = mAddedDate;
    }
}
