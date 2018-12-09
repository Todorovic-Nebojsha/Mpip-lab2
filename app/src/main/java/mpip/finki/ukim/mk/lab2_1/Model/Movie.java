package mpip.finki.ukim.mk.lab2_1.Model;

import com.google.gson.annotations.SerializedName;


public class Movie {
    @SerializedName("Title")
    private String title;
    @SerializedName("Year")
    private String year;
    @SerializedName("imdbID")
    private String imdbId;
    @SerializedName("Type")
    private String type;
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
}
