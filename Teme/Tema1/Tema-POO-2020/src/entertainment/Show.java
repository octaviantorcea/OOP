package entertainment;

import fileio.SerialInputData;
import fileio.ShowInput;

import java.util.ArrayList;

public final class Show extends Video {
    private int nrSeasons;
    private ArrayList<Season> seasons = new ArrayList<>();

    @Override
    public void calculateAverageRating() {
        double sum = 0d;

        for (Season season : seasons) {
            Double seasonSum = 0d;

            for (Double rating : season.getRatings()) {
                seasonSum += rating;
            }

            if (seasonSum == 0) {
                sum = 0;
            } else {
                seasonSum /= season.getRatings().size();
                sum += seasonSum;
            }
        }

        this.avgRating = sum / this.nrSeasons;
    }

    @Override
    public void readVideo(final ShowInput showInput) {
        super.readVideo(showInput);
        this.nrSeasons = ((SerialInputData) showInput).getNumberSeason();
        this.seasons = ((SerialInputData) showInput).getSeasons();

        for (Season season : this.seasons) {
            this.duration += season.getDuration();
        }
    }

    @Override
    public boolean isShow() {
        return true;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    // for debugging
    @Override
    public String toString() {
        return super.toString() + "Show{"
                + "nrSeasons=" + nrSeasons
                + ", seasons=" + seasons
                + '}';
    }
}
