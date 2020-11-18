package entertainment;

import fileio.MovieInputData;
import fileio.ShowInput;

import java.util.ArrayList;

public class Movie extends Video {
    private int duration;
    private ArrayList<Double> ratings = new ArrayList<>();

    @Override
    public void calculateAverageRating() {
        //magic
        Double PH = 6d;
        setAvgRating(PH);
    }

    @Override
    public void readVideo(ShowInput showInput) {
        super.readVideo(showInput);
        this.duration = ((MovieInputData)showInput).getDuration();
    }

    // for debugging
    @Override
    public String toString() {
        return super.toString() + "Movie{" +
                "duration=" + duration +
                ", ratings=" + ratings +
                '}';
    }
}
