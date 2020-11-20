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

    public Actor(final ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography.addAll(actorData.getFilmography());
        this.awards.putAll(actorData.getAwards());
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
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
