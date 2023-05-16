package com.ac3343.spotcontroller.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAOTrack {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DBTrack dbTrack);

    @Query("DELETE FROM track_table")
    void deleteAll();

    @Query("SELECT * FROM track_table WHERE added_date IS NOT null ORDER BY added_date DESC")
    LiveData<List<DBTrack>> getLibraryTracks();

    @Query("SELECT * FROM track_table WHERE album_id = :albumID")
    LiveData<List<DBTrack>> getAlbumTracks(String albumID);

    @Query("SELECT * FROM track_table WHERE added_date IS NOT null AND (track_name LIKE :searchTerm OR artist_display_name LIKE :searchTerm) ORDER BY added_date DESC")
    LiveData<List<DBTrack>> getSomeTracks(String searchTerm);

    @Query("SELECT * FROM track_table WHERE track_id = :id")
    LiveData<DBTrack> getTrackWithIDs(String id);
}
