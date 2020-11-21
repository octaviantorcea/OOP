package entertainment;

import fileio.MovieInputData;
import fileio.ShowInput;

import java.util.ArrayList;

/**
 * Contains information about a movie.
 */
public final class Movie extends Video {
    private final ArrayList<Double> ratings = new ArrayList<>();

    public Movie(final ShowInput showInput) {
        super(showInput);
        this.duration = ((MovieInputData) showInput).getDuration();
    }

    /**
     * Computes the average. If there are no ratings, the average rating is 0.
     */
    @Override
    public void calculateAverageRating() {
        Double sum = 0d;

        for (Double rating : ratings) {
            sum += rating;
        }

        if (ratings.isEmpty()) {
            this.avgRating = 0d;
        } else {
            this.avgRating = sum / ratings.size();
        }
    }

    @Override
    public boolean isShow() {
        return false;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }
}
