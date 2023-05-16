package com.ac3343.spotcontroller.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAOArtist {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DBArtist artist);

    @Query("DELETE FROM artist_table")
    void deleteAll();

    @Query("SELECT * FROM artist_table")
    LiveData<List<DBArtist>> getAllArtists();

    @Query("SELECT * FROM artist_table WHERE artist_id = :thisid")
    LiveData<DBArtist> getArtistWithID(String thisid);
}
