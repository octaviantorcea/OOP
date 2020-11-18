package actor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import entertainment.Video;
import fileio.ActorInputData;

public class Actor {
    final private String name;
    final private String careerDescription;
    private Double averageRating = 0d;
    private HashSet<Video> filmography = new HashSet<>();
    private HashMap<ActorsAwards, Integer> awards = new HashMap<>();

    public Actor(ActorInputData actorData) {
        this.name = actorData.getName();
        this.careerDescription = actorData.getCareerDescription();



        this.awards.putAll(actorData.getAwards());
    }
}
