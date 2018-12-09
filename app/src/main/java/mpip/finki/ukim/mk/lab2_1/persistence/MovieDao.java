package mpip.finki.ukim.mk.lab2_1.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;

@Dao
public interface MovieDao {

    @Insert
    public void insert(Movie m);

    //@Deprecated
    @Query("SELECT * FROM Movies c ORDER BY c.title")
    List<Movie> getAllSync();

    @Query("SELECT * FROM Movies c ORDER BY c.title")
    LiveData<List<Movie>> getAll();

    //nov metod za prikaz Details
    @Query("SELECT * From Movies c WHERE c.imdbID=:imdbId")
    LiveData<List<Movie>> getMoviesByImdbId(String imdbId);

    @Query("DELETE from Movies")
    void deleteAll();


}
