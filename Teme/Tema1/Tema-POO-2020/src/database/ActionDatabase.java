package database;

import action.Action;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.List;

public class ActionDatabase {
    ArrayList<Action> actionsDatabase = new ArrayList<>();

    public ActionDatabase(List<ActionInputData> actionData) {
        for (ActionInputData actionEntry : actionData) {
            Action newAction = new Action(actionEntry);
            actionsDatabase.add(newAction);
        }
    }

    public ArrayList<Action> getActionsDatabase() {
        return actionsDatabase;
    }

    //for debugging
    public void printActionData() {
        actionsDatabase.forEach(System.out::println);
    }
}
