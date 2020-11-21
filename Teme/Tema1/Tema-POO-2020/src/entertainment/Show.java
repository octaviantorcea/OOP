package entertainment;

import fileio.SerialInputData;
import fileio.ShowInput;

import java.util.ArrayList;

/**
 * Contains information about a show.
 */
public final class Show extends Video {
    private final int nrSeasons;
    private final ArrayList<Season> seasons;

    public Show(final ShowInput showInput) {
        super(showInput);
        this.nrSeasons = ((SerialInputData) showInput).getNumberSeason();
        this.seasons = ((SerialInputData) showInput).getSeasons();

        for (Season season : this.seasons) {
            this.duration += season.getDuration();
        }
    }

    /**
     * Computes the average rating of a show.<br>
     * It's the sum of all the average ratings of the seasons that show has.
     */
    @Override
    public void calculateAverageRating() {
        double sum = 0d;

        for (Season season : seasons) {
            Double seasonSum = 0d;

            for (Double rating : season.getRatings()) {
                seasonSum += rating;
            }

            if (seasonSum == 0) {
                sum += 0;
            } else {
                seasonSum /= season.getRatings().size();
                sum += seasonSum;
            }
        }

        this.avgRating = sum / this.nrSeasons;
    }

    @Override
    public boolean isShow() {
        return true;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }
}
