package entertainment;

import fileio.ShowInput;

import java.util.HashSet;

/**
 * Provides the skeleton for the "Movie" and "Show classes.<br>
 * Contains common members such as title, year, number of views, etc...
 */
public abstract class Video {
    protected String title;
    protected int year;
    protected HashSet<String> actors = new HashSet<>();
    protected HashSet<String> genres = new HashSet<>();
    protected int views = 0;
    /**
     * Number of times a video has been added to favorites.
     */
    protected int nrOfFav = 0;
    protected Double avgRating = 0d;
    protected int duration = 0;

    /**
     * Calculates the average rating of a video.
     */
    public abstract void calculateAverageRating();

    /**
     * @return true if it's a show, false otherwise
     */
    public abstract boolean isShow();

    public Video(final ShowInput showInput) {
        this.title = showInput.getTitle();
        this.year = showInput.getYear();
        this.actors.addAll(showInput.getCast());
        this.genres.addAll(showInput.getGenres());
    }

    public final String getTitle() {
        return title;
    }

    public final int getNrOfFav() {
        return nrOfFav;
    }

    public final void setNrOfFav(final int nrOfFav) {
        this.nrOfFav = nrOfFav;
    }

    public final int getViews() {
        return views;
    }

    public final void setViews(final int views) {
        this.views = views;
    }

    public final Double getAvgRating() {
        return avgRating;
    }

    public final HashSet<String> getGenres() {
        return genres;
    }

    public final int getYear() {
        return year;
    }

    public final int getDuration() {
        return duration;
    }
}
