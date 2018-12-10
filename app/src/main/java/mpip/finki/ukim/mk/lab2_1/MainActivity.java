package mpip.finki.ukim.mk.lab2_1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import mpip.finki.ukim.mk.lab2_1.Service.DownloadMovieService;
import mpip.finki.ukim.mk.lab2_1.persistence.MovieItemRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static mpip.finki.ukim.mk.lab2_1.Service.DownloadMovieService.DATABASE_UPDATED;

public class MainActivity extends AppCompatActivity {
    private UpdatedItemsBroadcastReceiver receiver;

    Retrofit retrofit;
    MovieItemRepository movieItemRepository;

    private String searchString;
    private ProgressBar progressBar;
    private FloatingActionButton fab = null;
    private SearchView searchView = null;

    CardViewAdapter cardViewAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    public Boolean noMovies=false;
    List<Movie> movies;
    Logger logger;

    //Variables for paggination
    private int page_num=1;
    private boolean isLoading=true;
    private int pastVisibleItems,visibleItemCount,totalItemCount,previous_total=0;
    private int view_treshold=10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        receiver=new UpdatedItemsBroadcastReceiver();


        logger=Logger.getLogger(MainActivity.class.getName());
        movies=new ArrayList<>();


        retrofit=new Retrofit.Builder()
                .baseUrl(MovieApiInterface.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        movieItemRepository=new MovieItemRepository(MainActivity.this);

        movieItemRepository.deleteAll();

        initViews();

        //movieItemRepository.deleteAll();  //proverka sto ke se desi ako e baza prazna
        initList();

        bindEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DATABASE_UPDATED);
        this.registerReceiver(receiver,filter);

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
                if(searchString!=null && searchString.length()>0 && !noMovies) {
                    Intent intent = new Intent(MainActivity.this, DownloadMovieService.class);
                    intent.putExtra("search", searchString);
                    MainActivity.this.startService(
                            intent);
                    Snackbar.make(view, "Syncing", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                // search proba ovoj treba da bide refresh!!!
//                if(searchString!=null && searchString.length()>0){
//                    search(searchString);
//                }
                else{
                    Toast.makeText(MainActivity.this,"You have not made a search,you cannot sync!",Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount=linearLayoutManager.getChildCount();
                totalItemCount=linearLayoutManager.getItemCount();
                pastVisibleItems=linearLayoutManager.findLastVisibleItemPosition();

                if(dy>0){
                    if(isLoading){
                        if(totalItemCount>previous_total){
                            isLoading=false;
                            previous_total=totalItemCount;
                        }
                        if(!isLoading&&(totalItemCount-visibleItemCount)<=(pastVisibleItems+view_treshold)){
                            page_num++;
                            performPaggination();
                            isLoading=true;
                        }
                    }
                }
            }
        });

    }

    public void search(String search){
        progressBar.setVisibility(View.VISIBLE);

        page_num=1;
        isLoading=true;
        pastVisibleItems=0;visibleItemCount=0;totalItemCount=0;previous_total=0;

        logger.log(Level.INFO,"search:"+search);
        MovieApiInterface api;
        api=retrofit.create(MovieApiInterface.class);
       api.getMovies(search,page_num).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                movieItemRepository.deleteAll();

                if(response.isSuccessful()){
                    //if(response.body().getResponse()=="False")
                    movies=response.body().getMovies();

                    if(movies==null || movies.size()==0){
                        Toast.makeText(getApplicationContext(),"No movies found for this search",Toast.LENGTH_SHORT).show();
                        noMovies=true;
                    }
                    else {
                        noMovies=false;
                        for (Movie m : movies) {
                            movieItemRepository.insertItem(m);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
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
                if (items == null ) {
                   // search(searchText);
                    progressBar.setVisibility(View.GONE);
                } else {
                    cardViewAdapter.updateData(items);
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(MainActivity.this,"Synced",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    public void performPaggination(){
        progressBar.setVisibility(View.VISIBLE);

        //logger.log(Level.INFO,"search:"+searchString);
        MovieApiInterface api;
        api=retrofit.create(MovieApiInterface.class);
        api.getMovies(searchString,page_num).enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()){
                    movies.addAll(response.body().getMovies());
                    //movieItemRepository.deleteAll();
                    if(movies==null||movies.size()==0){
                        noMovies=true;
                        Toast.makeText(MainActivity.this, "There are no more movies...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        noMovies=false;
                        for (Movie m : movies) {
                            movieItemRepository.insertItem(m);
                        }
                        Toast.makeText(MainActivity.this, "page: " + page_num, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"There are no more movies...",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public class UpdatedItemsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            movieItemRepository.listAllMovieItems().observe(MainActivity.this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> flickrItems) {
                    if(flickrItems!=null && flickrItems.size()>0) {
                        cardViewAdapter.updateData(flickrItems);
                        Toast.makeText(MainActivity.this, "Synced", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "There are no movies found...", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}