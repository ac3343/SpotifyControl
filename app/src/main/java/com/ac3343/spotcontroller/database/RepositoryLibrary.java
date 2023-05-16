package com.ac3343.spotcontroller.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RepositoryLibrary {
    private DAOAlbum mDaoAlbum;
    private DAOArtist mDaoArtist;
    private DAOTrack mDaoTrack;
    private LiveData<List<DBAlbum>> mAllAlbums;
    private LiveData<List<DBArtist>> mAllArtists;
    private LiveData<List<DBTrack>> mLibraryTracks;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    RepositoryLibrary(Application application){
        RoomLibrary db = RoomLibrary.getDatabase(application);
        mDaoAlbum = db.daoAlbum();
        mDaoArtist = db.daoArtist();
        mDaoTrack = db.daoTrack();
        mAllAlbums = mDaoAlbum.getAllAlbums();
        mAllArtists = mDaoArtist.getAllArtists();
        mLibraryTracks = mDaoTrack.getLibraryTracks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<DBAlbum>> getAllAlbums(){
        return mAllAlbums;
    }

    LiveData<List<DBArtist>> getAllArtists() {
        return mAllArtists;
    }

    LiveData<List<DBTrack>> getLibraryTracks() {
        return mLibraryTracks;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insertAlbum(DBAlbum album){
        RoomLibrary.databaseWriteExecutor.execute(() -> {
            mDaoAlbum.insert(album);
        });
    }

    void deleteAllAlbums(){
        RoomLibrary.databaseWriteExecutor.execute(()->{
            mDaoAlbum.deleteAll();
        });
    }

    void insertArtist(DBArtist artist){
        RoomLibrary.databaseWriteExecutor.execute(()->{
            mDaoArtist.insert(artist);
        });
    }

    void deleteAllArtist(){
        RoomLibrary.databaseWriteExecutor.execute(()->{
            mDaoArtist.deleteAll();
        });
    }

    LiveData<DBArtist> getArtistWithID(String id){
        return mDaoArtist.getArtistWithID(id);
    }
    LiveData<DBTrack> getTrackWithIDs(String id){return mDaoTrack.getTrackWithIDs(id);}
    LiveData<List<DBAlbum>> getSomeAlbums(String searchTerm){return mDaoAlbum.getSomeAlbums(searchTerm);};
    LiveData<List<DBTrack>> getSomeTracks(String searchTerm){return mDaoTrack.getSomeTracks(searchTerm);};


    LiveData<List<DBTrack>> getAlbumTracks(String albumID){
        return mDaoTrack.getAlbumTracks(albumID);
    }
    void insertTrack(DBTrack track){
        RoomLibrary.databaseWriteExecutor.execute(() -> {
            mDaoTrack.insert(track);
        });
    }

    void deleteAllTracks(){
        RoomLibrary.databaseWriteExecutor.execute(()->{
            mDaoTrack.deleteAll();
        });
    }

    LiveData<List<DBAlbum>> getAlbumsWithID(String[] ids){
        return mDaoAlbum.getAlbumsWithID(ids);
    }

}
