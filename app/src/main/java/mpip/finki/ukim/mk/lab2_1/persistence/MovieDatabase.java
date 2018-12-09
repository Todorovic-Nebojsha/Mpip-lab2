package mpip.finki.ukim.mk.lab2_1.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;

@Database(entities = {Movie.class}, version = 1,exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
}
