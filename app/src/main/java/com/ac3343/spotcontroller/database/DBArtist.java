package com.ac3343.spotcontroller.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist_table")
public class DBArtist {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="artist_id")
    public String mID;

    @ColumnInfo(name = "artist_name")
    public String mName;

    @ColumnInfo(name="artist_uri")
    public String mURI;

    public DBArtist(@NonNull String id, String name, String uri){
        this.mID = id;
        this.mName = name;
        this.mURI = uri;
    }
    public DBArtist(){this("null", null, null);};

    @NonNull
    public String getmID() {
        return mID;
    }

    public String getmName() {
        return mName;
    }

    public String getmURI() {
        return mURI;
    }
}
