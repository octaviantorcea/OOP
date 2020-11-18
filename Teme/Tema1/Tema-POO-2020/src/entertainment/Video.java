package entertainment;

import java.util.HashSet;

public abstract class Video implements Ratable{
    protected String title;
    protected String year;
    protected HashSet<String> actors = new HashSet<>();
    protected HashSet<Genre> genres = new HashSet<>();
    protected int views = 0;
    protected int nrOfFav = 0;
    protected Double avgRating;

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public abstract void calculateAverageRating();
}
