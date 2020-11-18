package actor;

import java.util.HashMap;
import java.util.HashSet;

import database.VideoDatabase;
import entertainment.Video;
import fileio.ActorInputData;

public class Actor {
    final private String name;
    final private String careerDescription;
    private HashSet<String> filmography = new HashSet<>();
    private HashMap<ActorsAwards, Integer> awards = new HashMap<>();
    private Double averageRating = 0d;

    public Actor(ActorInputData actorData, VideoDatabase videoDatabase) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();
        this.filmography.addAll(actorData.getFilmography());
        this.awards.putAll(actorData.getAwards());
    }

    public String getName() {
        return name;
    }

    // for debugging
    @Override
    public String toString() {
        return "Actor{" +
                "name='" + name + '\n' +
                ", careerDescription='" + careerDescription + '\n' +
                ", filmography=" + filmography +
                ", awards=" + awards +
                ", averageRating=" + averageRating +
                '}';
    }
}
