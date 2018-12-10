package mpip.finki.ukim.mk.lab2_1.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mpip.finki.ukim.mk.lab2_1.MainActivity;
import mpip.finki.ukim.mk.lab2_1.Model.Movie;
import mpip.finki.ukim.mk.lab2_1.Model.MovieList;
import mpip.finki.ukim.mk.lab2_1.R;
import mpip.finki.ukim.mk.lab2_1.Repository.MovieApiInterface;
import mpip.finki.ukim.mk.lab2_1.persistence.MovieItemRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadMovieService extends IntentService {
    public static final String DATABASE_UPDATED="mpip.finki.ukim.mk.lab2_1.DATABASE_UPDATED";

    private MovieApiInterface api;

    private MovieItemRepository movieItemRepository;

    public DownloadMovieService(){
        super("Download and save movies");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(MovieApiInterface.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(MovieApiInterface.class);

        movieItemRepository = new MovieItemRepository(DownloadMovieService.this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        NotificationUtils.makeNotification(this,
//                "my_channel_05",
//                "Downloading",
//                "Downloading Countries from API",
//                R.drawable.ic_search_black_24dp,
//                new Intent(this, MainActivity.class),
//                1234,
//                b -> b.setAutoCancel(true));
      Bundle b=intent.getExtras();
      //final String scroll=b.getString("Scroll");
      int page_num=1;
      String search=b.getString("search");
        //List<Movie> movies =new ArrayList<>();
        api.getMovies(search,page_num).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()){
                    if(!(response.body().getMovies()!=null || response.body().getMovies().size()>0)) {
                        movieItemRepository.deleteAll();
                        for (Movie m : response.body().getMovies()) {
                            movieItemRepository.insertItem(m);
                        }
                    }
                    sendDatabaseUpdatedBroadcast();
                }

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendDatabaseUpdatedBroadcast() {
        sendBroadcast(new Intent(DATABASE_UPDATED));
    }


    }
