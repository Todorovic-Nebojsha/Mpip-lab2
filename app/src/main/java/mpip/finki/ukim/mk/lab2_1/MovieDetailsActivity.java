package mpip.finki.ukim.mk.lab2_1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import mpip.finki.ukim.mk.lab2_1.Model.Movie;
import mpip.finki.ukim.mk.lab2_1.persistence.MovieItemRepository;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView title;
    private TextView year;
    private TextView imDbId;
    private TextView type;
    MovieItemRepository movieItemRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        initViews();
        bindViews();

    }

    public void initViews(){
        imageView=(ImageView) findViewById(R.id.movieImg);
        title=(TextView) findViewById(R.id.movieTitle);
        year=(TextView) findViewById(R.id.movieYear);
        imDbId=(TextView) findViewById(R.id.movieImdbId);
        type=(TextView) findViewById(R.id.movieType);
    }

    public void bindViews(){
        String searchImDbId=getIntent().getStringExtra(Intent.EXTRA_TEXT);
        //Toast.makeText(MovieDetailsActivity.this,searchImDbId,Toast.LENGTH_LONG).show();
        movieItemRepository=new MovieItemRepository(MovieDetailsActivity.this);

        LiveData<List<Movie>> ldItems = movieItemRepository.getMoviesByImdbId(searchImDbId);

        ldItems.observe(MovieDetailsActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> items) {
                if (items == null || items.size() == 0) {
                    // search(searchText);
                } else {
                    Movie m=items.get(0);
                    Glide.with(MovieDetailsActivity.this)
                            .load(m.getImageUrl())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .crossFade()
                            .into(imageView);
                    title.setText("Title: "+m.getTitle());
                    imDbId.setText("ImDbId: "+m.getImdbId());
                    year.setText("Year: "+m.getYear());
                    type.setText("Type: "+m.getType());
                    //Toast.makeText(MainActivity.this,"Synced",Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}
