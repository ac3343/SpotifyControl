package com.ac3343.spotcontroller.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAOAlbum {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DBAlbum dbAlbum);

    @Query("DELETE FROM album_table")
    void deleteAll();

    @Query("SELECT * FROM album_table WHERE added_date IS NOT null ORDER BY added_date DESC")
    LiveData<List<DBAlbum>> getAllAlbums();

    @Query("SELECT * FROM album_table WHERE added_date IS NOT null AND (album_name LIKE :searchTerm OR artist_display_name LIKE :searchTerm) ORDER BY added_date DESC")
    LiveData<List<DBAlbum>> getSomeAlbums(String searchTerm);

    @Query("SELECT * FROM album_table WHERE album_id IN (:ids)")
    LiveData<List<DBAlbum>> getAlbumsWithID(String[] ids);
}
