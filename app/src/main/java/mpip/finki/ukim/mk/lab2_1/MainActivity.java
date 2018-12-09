package mpip.finki.ukim.mk.lab2_1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;
import mpip.finki.ukim.mk.lab2_1.Model.MovieList;

import mpip.finki.ukim.mk.lab2_1.Repository.MovieApiInterface;
import mpip.finki.ukim.mk.lab2_1.persistence.MovieItemRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Retrofit retrofit;
    MovieItemRepository movieItemRepository;
    MovieApiInterface api;
    List<Movie> movies;
    ListView myListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myListView=(ListView) findViewById(R.id.listView);


        Logger logger=Logger.getLogger(MainActivity.class.getName());


        movies=new ArrayList<>();
        retrofit=new Retrofit.Builder()
                .baseUrl(MovieApiInterface.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api=retrofit.create(MovieApiInterface.class);

        movieItemRepository=new MovieItemRepository(MainActivity.this);

        initList("Fast");

    }




    public void search(String search){
        Call<MovieList> call=api.getMovies(search);
        //logger.log(Level.INFO,"Created");
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()){
                    movies=response.body().getMovies();
                    movieItemRepository.deleteAll();
                    for(Movie m : movies){
                        movieItemRepository.insertItem(m);
                    }

                }




            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initList(String search){
        LiveData<List<Movie>> ldItems = movieItemRepository.listAllMovieItems();

        ldItems.observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> items) {
                if (items == null || items.size() == 0) {
                    search("Fast");
                } else {
                    //cardViewAdapter.updateData(items);
                    String [] titles=new String[items.size()];
                    for(int i=0;i<items.size();i++){
                        titles[i]=items.get(i).getTitle();
                    }
                    //Toast.makeText(getApplicationContext(),items.get(0).getType()+"",Toast.LENGTH_SHORT).show();
                   myListView.setAdapter(new ArrayAdapter<String>(
                           getApplicationContext(),
                           android.R.layout.simple_expandable_list_item_1,
                           titles
                   ));

                }
            }
        });

    }
}