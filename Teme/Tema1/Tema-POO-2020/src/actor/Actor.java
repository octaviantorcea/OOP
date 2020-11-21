package actor;

import java.util.HashMap;
import java.util.HashSet;

import database.VideoDatabase;
import entertainment.Video;
import fileio.ActorInputData;

/**
 * Information about an Actor.
 */
public final class Actor {
    private final String name;
    private final String careerDescription;
    /**
     * Set of films the actor played in.
     */
    private final HashSet<String> filmography = new HashSet<>();
    /**
     * The awards an actor has received.<br>
     * Maps the award with the number of times it was awarded.
     */
    private final HashMap<ActorsAwards, Integer> awards = new HashMap<>();
    /**
     * Average rating based on all the films (the actor played in) that have
     * been rated at least once.
     */
    private Double averageRating = 0d;
    private int totalAwards = 0;

    public Actor(final ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography.addAll(actorData.getFilmography());
        this.awards.putAll(actorData.getAwards());
        computeTotalAwards();
    }

    /**
     * Computes the average grade for an Actor based on the grades of the
     * videos they played in.<br>
     * Only videos that are graded are considered.
     * @param videoDatabase the video database
     */
    public void computeActorGrade(final VideoDatabase videoDatabase) {
        double sumGrade = 0;
        int ratedVideos = 0;

        for (String title : this.getFilmography()) {
            Video video = videoDatabase.getVideoDatabase().get(title);

            if (video != null && video.getAvgRating() > 0) {
                sumGrade += video.getAvgRating();
                ratedVideos++;
            }
        }

        if (ratedVideos == 0) {
            this.setAverageRating(0d);
        } else {
            this.setAverageRating(sumGrade / ratedVideos);
        }
    }

    private void computeTotalAwards() {
        for (int nrAwards : this.awards.values()) {
            this.totalAwards += nrAwards;
        }
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public int getTotalAwards() {
        return totalAwards;
    }

    public HashMap<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setAverageRating(final Double averageRating) {
        this.averageRating = averageRating;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public HashSet<String> getFilmography() {
        return filmography;
    }
}
