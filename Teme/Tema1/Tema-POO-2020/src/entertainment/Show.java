package entertainment;

import fileio.SerialInputData;
import fileio.ShowInput;

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

    @Override
    public void readVideo(ShowInput showInput) {
        super.readVideo(showInput);
        this.nrSeasons = ((SerialInputData)showInput).getNumberSeason();
        this.seasons = ((SerialInputData)showInput).getSeasons();
    }
}
