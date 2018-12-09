package mpip.finki.ukim.mk.lab2_1;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Retrofit retrofit;
    MovieApiInterface api;
    String searchString;
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

        //initial before search in app
        searchString="Fast";

        Call<MovieList> call=api.getMovies(searchString);
        logger.log(Level.INFO,"Created");
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()){
                  movies=response.body().getMovie();
                }
                String [] titles=new String[movies.size()];
                for(int i=0;i<movies.size();i++){
                    titles[i]=movies.get(i).getTitle();
                }

                myListView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_expandable_list_item_1,
                        titles

                ));

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        String poraka=" "+movies.size();
        logger.log(Level.INFO,poraka);
    }
}
