package database;

import actor.Actor;
import fileio.ActorInputData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ActorDatabase {
    private HashMap<String, Actor> actorDatabase = new HashMap<>();

    public  ActorDatabase(List<ActorInputData> actorData, VideoDatabase videoDatabase) {
        for (ActorInputData actorEntry : actorData) {
            Actor newActor = new Actor(actorEntry, videoDatabase);
            actorDatabase.put(newActor.getName(), newActor);
        }
    }

    // for debugging
    public void printActorData() {
        actorDatabase.forEach(((s, actor) -> System.out.println(actor)));
    }
}
