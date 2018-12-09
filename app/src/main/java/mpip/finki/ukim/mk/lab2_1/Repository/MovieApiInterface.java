package mpip.finki.ukim.mk.lab2_1.Repository;

import java.util.List;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;
import mpip.finki.ukim.mk.lab2_1.Model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiInterface {
    String Base_URL="http://www.omdbapi.com/";

    @GET("?i=tt3896198&apikey=bd67919d&type=movie&plot=short")
    Call<MovieList> getMovies(@Query("s")String s,@Query("p") int p);


}
