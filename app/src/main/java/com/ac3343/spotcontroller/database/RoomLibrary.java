package com.ac3343.spotcontroller.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DBAlbum.class, DBArtist.class, DBTrack.class}, version = 4, exportSchema = false)
public abstract class RoomLibrary extends RoomDatabase {
    public abstract DAOAlbum daoAlbum();
    public abstract DAOArtist daoArtist();
    public abstract DAOTrack daoTrack();

    private static volatile RoomLibrary INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static RoomLibrary getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (RoomLibrary.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomLibrary.class, "library_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `album_table` ADD COLUMN `artist_display_name` TEXT");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `track_table` (`track_id` TEXT NOT NULL, track_name TEXT, artist_ids TEXT, artist_display_name TEXT, album_id TEXT, album_position TEXT, in_library INTEGER, PRIMARY KEY(`track_id`))");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `album_table` ADD COLUMN `added_date` TEXT");
            database.execSQL("ALTER TABLE `album_table` ADD COLUMN `track_ids` TEXT");
            database.execSQL("ALTER TABLE `track_table` ADD COLUMN `added_date` TEXT");
        }
    };
}
