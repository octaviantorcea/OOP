package entertainment;

import fileio.ShowInput;

import java.util.HashSet;

public abstract class Video {
    protected String title;
    protected int year;
    protected HashSet<String> actors = new HashSet<>();
    protected HashSet<String> genres = new HashSet<>();
    protected int views = 0;
    protected int nrOfFav = 0;
    protected Double avgRating;

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public abstract void calculateAverageRating();

    public void readVideo(ShowInput showInput) {
        this.title = showInput.getTitle();
        this.year = showInput.getYear();
        this.actors.addAll(showInput.getCast());
        this.genres.addAll(showInput.getGenres());
    }
}
