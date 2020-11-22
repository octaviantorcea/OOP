package database;

import action.Action;
import actor.Actor;
import actor.ActorsAwards;
import fileio.ActorInputData;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static common.Constants.AWARD_LIST;

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
     * @return an array of strings containing the names of all the actors sorted
     * (in ascending order) by their average grade (and with grade > 0)
     */
    public ArrayList<String> getAvgQuery(final VideoDatabase videoDatabase) {
        ArrayList<String> actorsNames = new ArrayList<>();
        ArrayList<Actor> actors = new ArrayList<>();

        actorDatabase.values().forEach(actor -> actor.computeActorGrade(videoDatabase));
        actorDatabase.values().forEach(actor -> {
            if (actor.getAverageRating() > 0) {
                actors.add(actor);
            }
        });

        // sorting them by their grade; if equal -> sort them alphabetically
        actors.sort((actor1, actor2) -> {
            int compute = actor1.getAverageRating().compareTo(actor2.getAverageRating());

            if (compute != 0) {
                return compute;
            } else {
                return actor1.getName().compareTo(actor2.getName());
            }
        });

        actors.forEach(actor -> actorsNames.add(actor.getName()));

        return actorsNames;
    }

    /**
     * @param action the action that contains the awards given as filters
     * @return an array of string containing the names of all the actors that
     * have won the awards given as filters, sorted (in ascending order) by
     * their total awards
     */
    public ArrayList<String> getAwardsQuery(final Action action) {
        ArrayList<String> actorsNames = new ArrayList<>();
        ArrayList<Actor> actors = new ArrayList<>();
        ArrayList<ActorsAwards> awards = new ArrayList<>();

        action.getFilters().get(AWARD_LIST).forEach(string ->
                awards.add(Utils.stringToAwards(string)));

        for (Actor actor : actorDatabase.values()) {
            if (actor.getAwards().keySet().containsAll(awards)) {
                actors.add(actor);
            }
        }

        // sort them by the number of their awards; if equal -> sort them alphabetically
        actors.sort((actor1, actor2) -> {
            int compare = actor1.getTotalAwards() - actor2.getTotalAwards();

            if (compare != 0) {
                return compare;
            } else {
                return actor1.getName().compareTo(actor2.getName());
            }
        });

        actors.forEach(actor -> actorsNames.add(actor.getName()));

        return actorsNames;
    }

    /**
     * @param action the action that contains the words given as filters
     * @return an array of string containing the names of all the actors that
     * have in their description all the words given as filters, sorted in
     * alphabetically order.
     */
    public ArrayList<String> getDescriptionQuery(final Action action) {
        ArrayList<String> actors = new ArrayList<>();
        ArrayList<String> filterWords = new ArrayList<>(action.getFilters().get(2));

        for (Actor actor : actorDatabase.values()) {

            ArrayList<String> careerDescription;
            careerDescription = Utils.stringToArray(actor.getCareerDescription());

            if (careerDescription.containsAll(filterWords)) {
                actors.add(actor.getName());
            }
        }

        actors.sort(String::compareTo);

        return actors;
    }
}
