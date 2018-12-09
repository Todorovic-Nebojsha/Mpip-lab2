package mpip.finki.ukim.mk.lab2_1.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Movies")
public class Movie {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "Title")
    @SerializedName("Title")
    private String title;
    @ColumnInfo(name="Year")
    @SerializedName("Year")
    private String year;
    @ColumnInfo(name="imdbID")
    @SerializedName("imdbID")
    private String imdbId;
    @ColumnInfo(name="Type")
    @SerializedName("Type")
    private String type;
    @ColumnInfo(name="Poster")
    @SerializedName("Poster")
    private String imageUrl;



    public Movie(String title, String year, String imdbId, String type, String imageUrl) {
        this.title = title;
        this.year = year;
        this.imdbId = imdbId;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}