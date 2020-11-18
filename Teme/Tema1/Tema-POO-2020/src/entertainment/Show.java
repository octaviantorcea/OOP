package entertainment;

import java.util.ArrayList;

public class Show extends Video {
    private int nrSeasons;
    private int totalDuration;
    private ArrayList<Season> seasons = new ArrayList<>();

    @Override
    public void calculateAverageRating() {
        //magic
        Double PH = 6d;
        setAvgRating(PH);
    }
}
