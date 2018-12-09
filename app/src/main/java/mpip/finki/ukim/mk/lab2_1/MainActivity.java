package mpip.finki.ukim.mk.lab2_1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mpip.finki.ukim.mk.lab2_1.Adapter.CardViewAdapter;
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

    private String searchString;
    private ProgressBar progressBar;
    private FloatingActionButton fab = null;
    private SearchView searchView = null;

    CardViewAdapter cardViewAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    List<Movie> movies;
    Logger logger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger=Logger.getLogger(MainActivity.class.getName());
        movies=new ArrayList<>();


        retrofit=new Retrofit.Builder()
                .baseUrl(MovieApiInterface.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        movieItemRepository=new MovieItemRepository(MainActivity.this);
        initViews();

        //movieItemRepository.deleteAll();  //proverka sto ke se desi ako e baza prazna
        initList();

        bindEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Get the SearchView and set the searchable configuration
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchString=s;
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void initViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressBar = findViewById(R.id.progressBar);
        fab = findViewById(R.id.fab);
    }

    private void bindEvents(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CardViewActivity.this,DownloadFlickrItemsService.class);
//                intent.putExtra(getString(R.string.download_flickr_tag),"ФИНКИ");
//                CardViewActivity.this.startService(
//                        intent);
//                Snackbar.make(view, "Syncing", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // search proba ovoj treba da bide refresh!!!
                if(searchString!=null && searchString.length()>0){
                    search(searchString);
                }
                else{
                    Toast.makeText(MainActivity.this,"You have not made a search,you cannot sync!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void search(String search){
        logger.log(Level.INFO,"search:"+search);
        MovieApiInterface api;
        api=retrofit.create(MovieApiInterface.class);
       api.getMovies(search).enqueue(new Callback<MovieList>() {
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
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    public void initList(){
        recyclerView = findViewById(R.id.recyclerView);
        cardViewAdapter=new CardViewAdapter(MainActivity.this,movies);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cardViewAdapter);


        LiveData<List<Movie>> ldItems = movieItemRepository.listAllMovieItems();

        ldItems.observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> items) {
                if (items == null || items.size() == 0) {
                   // search(searchText);
                } else {
                    cardViewAdapter.updateData(items);
                    //Toast.makeText(MainActivity.this,"Synced",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}