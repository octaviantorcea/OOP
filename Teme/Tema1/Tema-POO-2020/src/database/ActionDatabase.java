package database;

import action.Action;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.List;

/**
 * An arraylist with all the actions.
 */
public final class ActionDatabase {
    private final ArrayList<Action> actionsDatabase = new ArrayList<>();

    public ActionDatabase(final List<ActionInputData> actionData) {
        for (ActionInputData actionEntry : actionData) {
            Action newAction = new Action(actionEntry);
            actionsDatabase.add(newAction);
        }
    }

    public ArrayList<Action> getActionsDatabase() {
        return actionsDatabase;
    }
}
