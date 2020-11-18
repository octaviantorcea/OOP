package entertainment;

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
}
