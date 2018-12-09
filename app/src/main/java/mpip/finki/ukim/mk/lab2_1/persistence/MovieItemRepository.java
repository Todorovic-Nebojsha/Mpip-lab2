package mpip.finki.ukim.mk.lab2_1.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;

public class MovieItemRepository {
    private String DB_NAME = "Movies";

    private MovieDatabase movieItemDatabase;

    private Context context;

    public MovieItemRepository(Context context) {
        movieItemDatabase = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).build();
    }

    public void insertItem(final Movie item) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                movieItemDatabase.movieDao().insert(item);
                return null;
            }

        }.execute();
    }

    public LiveData<List<Movie>> listAllMovieItems() {
        return movieItemDatabase.movieDao().getAll();
    }
    //nov metod za prikaz Details
    public LiveData<List<Movie>> getMoviesByImdbId(String imdbId){
        return movieItemDatabase.movieDao().getMoviesByImdbId(imdbId);
    }

    public List<Movie> listAllMovieItemsSync(){
        return  movieItemDatabase.movieDao().getAllSync();
    }

    public void deleteAll() {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                movieItemDatabase.movieDao().deleteAll();
                return null;
            }
        }.execute();
    }


}
