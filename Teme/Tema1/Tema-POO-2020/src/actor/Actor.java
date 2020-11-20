package actor;

import java.util.HashMap;
import java.util.HashSet;

import fileio.ActorInputData;

public final class Actor {
    private final String name;
    private final String careerDescription;
    private final HashSet<String> filmography = new HashSet<>();
    private final HashMap<ActorsAwards, Integer> awards = new HashMap<>();
    private Double averageRating = 0d;
    private int totalAwards = 0;

    public Actor(final ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography.addAll(actorData.getFilmography());
        this.awards.putAll(actorData.getAwards());
        computeTotalAwards();
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    private void computeTotalAwards() {
        for (int nrAwards : this.awards.values()) {
            this.totalAwards += nrAwards;
        }
    }

    public int getTotalAwards() {
        return totalAwards;
    }

    public HashMap<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    // for debugging
    @Override
    public String toString() {
        return "Actor{"
                + "name='" + name + '\n'
                + ", careerDescription='" + careerDescription + '\n'
                + ", filmography=" + filmography
                + ", awards=" + awards
                + ", averageRating=" + averageRating
                + '}';
    }
}
