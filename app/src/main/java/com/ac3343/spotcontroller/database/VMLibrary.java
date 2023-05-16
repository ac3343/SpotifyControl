package com.ac3343.spotcontroller.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VMLibrary extends AndroidViewModel {
    private RepositoryLibrary mRepository;

    private final LiveData<List<DBAlbum>> mAllAlbums;
    private final LiveData<List<DBTrack>> mLibraryTracks;

    public VMLibrary(Application application){
        super(application);
        mRepository = new RepositoryLibrary(application);
        mAllAlbums = mRepository.getAllAlbums();
        mLibraryTracks = mRepository.getLibraryTracks();
    }

    public LiveData<List<DBAlbum>> getAllAlbums() {
        return mAllAlbums;
    }
    public void insertAlbum(DBAlbum album) {mRepository.insertAlbum(album);}
    public void deleteAllAlbums(){mRepository.deleteAllAlbums();}

    public void insertArtist(DBArtist artist) {mRepository.insertArtist(artist);}
    public void deleteAllArtists(){mRepository.deleteAllArtist();}
    public LiveData<DBArtist> getArtistWithID(String id){return mRepository.getArtistWithID(id);}

    public void insertTrack(DBTrack track) {mRepository.insertTrack(track);}
    public void deleteAllTracks(){mRepository.deleteAllTracks();}
    public LiveData<List<DBTrack>> getAlbumTracks(String albumID){return mRepository.getAlbumTracks(albumID);}
    public LiveData<List<DBAlbum>> getAlbumsWithID(String[] ids){
        return mRepository.getAlbumsWithID(ids);
    }

    public LiveData<DBTrack> getTrackWithIDs(String id){
        return mRepository.getTrackWithIDs(id);
    }
    public LiveData<List<DBAlbum>> getSomeAlbums(String searchTerm){return mRepository.getSomeAlbums(searchTerm);};

    public LiveData<List<DBTrack>> getSomeTracks(String searchTerm){return mRepository.getSomeTracks(searchTerm);};

    public LiveData<List<DBTrack>> getLibraryTracks(){return mLibraryTracks;}
}
