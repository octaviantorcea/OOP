package database;

import actor.Actor;
import fileio.ActorInputData;

import java.util.HashMap;
import java.util.List;

/**
 * An hashmap that contains all the actors.<br>
 * Maps the name of the actor to an "Actor"object.
 */
public final class ActorDatabase {
    private final HashMap<String, Actor> actorDatabase = new HashMap<>();

    public  ActorDatabase(final List<ActorInputData> actorData) {
        for (ActorInputData actorEntry : actorData) {
            Actor newActor = new Actor(actorEntry);
            actorDatabase.put(newActor.getName(), newActor);
        }
    }

    public HashMap<String, Actor> getActorDatabase() {
        return actorDatabase;
    }
}
