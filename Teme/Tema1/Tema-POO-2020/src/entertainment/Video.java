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
    protected Double avgRating = 0d;
    protected int duration = 0;

    public abstract void calculateAverageRating();

    public void readVideo(final ShowInput showInput) {
        this.title = showInput.getTitle();
        this.year = showInput.getYear();
        this.actors.addAll(showInput.getCast());
        this.genres.addAll(showInput.getGenres());
    }

    public String getTitle() {
        return title;
    }

    public int getNrOfFav() {
        return nrOfFav;
    }

    public void setNrOfFav(final int nrOfFav) {
        this.nrOfFav = nrOfFav;
    }

    public int getViews() {
        return views;
    }

    public void setViews(final int views) {
        this.views = views;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public HashSet<String> getGenres() {
        return genres;
    }

    // for debugging
    @Override
    public String toString() {
        return "Video{"
                + "title='" + title + '\''
                + ", year=" + year
                + ", actors=" + actors
                + ", genres=" + genres
                + ", views=" + views
                + ", nrOfFav=" + nrOfFav
                + ", avgRating=" + avgRating
                + '}';
    }
}
