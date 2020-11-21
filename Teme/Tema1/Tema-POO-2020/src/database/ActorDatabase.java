package database;

import actor.Actor;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An hashmap that contains all the actors.<br>
 * Maps the name of the actor to an "Actor"object.
 */
public final class ActorDatabase {
    private final HashMap<String, Actor> actorDatabase = new HashMap<>();

    public ActorDatabase(final List<ActorInputData> actorData) {
        for (ActorInputData actorEntry : actorData) {
            Actor newActor = new Actor(actorEntry);
            actorDatabase.put(newActor.getName(), newActor);
        }
    }

    /**
     * @param videoDatabase the video database
     * @return an array of string containing the names of all the actors sorted
     * by their average grade (and with grade > 0)
     */
    public ArrayList<String> getAvgQuery(final VideoDatabase videoDatabase) {
        actorDatabase.values().forEach(actor -> actor.computeActorGrade(videoDatabase));

        ArrayList<Actor> actors = new ArrayList<>();

        actorDatabase.values().forEach(actor -> {
            if (actor.getAverageRating() > 0) {
                actors.add(actor);
            }
        });

        actors.sort((actor1, actor2) -> {
            int compute = actor1.getAverageRating().compareTo(actor2.getAverageRating());

            if (compute != 0) {
                return compute;
            } else {
                return actor1.getName().compareTo(actor2.getName());
            }
        });

        ArrayList<String> actorsNames = new ArrayList<>();
        actors.forEach(actor -> actorsNames.add(actor.getName()));

        return actorsNames;
    }

    public HashMap<String, Actor> getActorDatabase() {
        return actorDatabase;
    }
}
