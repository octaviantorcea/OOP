package entertainment;

import fileio.MovieInputData;
import fileio.ShowInput;

import java.util.ArrayList;

public final class Movie extends Video {
    private final ArrayList<Double> ratings = new ArrayList<>();

    @Override
    public void calculateAverageRating() {
        Double sum = 0d;

        for (Double rating : ratings) {
            sum += rating;
        }

        this.avgRating = sum / ratings.size();
    }

    @Override
    public void readVideo(final ShowInput showInput) {
        super.readVideo(showInput);
        this.duration = ((MovieInputData) showInput).getDuration();
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    // for debugging
    @Override
    public String toString() {
        return super.toString() + "Movie{"
                + "duration=" + duration
                + ", ratings=" + ratings
                + '}';
    }
}
