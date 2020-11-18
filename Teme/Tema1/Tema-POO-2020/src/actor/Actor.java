package actor;

import java.util.HashMap;
import java.util.HashSet;

import entertainment.Video;

public class Actor {
    private String name;
    private String careerDescription;
    private Double averageRating;
    private HashSet<Video> filmography = new HashSet<>();
    private HashMap<ActorsAwards, Integer> awards = new HashMap<>();
}
