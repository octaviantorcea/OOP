package database;

import fileio.UserInputData;
import user.User;

import java.util.HashMap;
import java.util.List;

public class UserDatabase {
    private HashMap<String, User> userDatabase = new HashMap<>();

    public UserDatabase(List<UserInputData> userData, VideoDatabase videoDatabase) {
        for (UserInputData userEntry : userData) {
            User newUser = new User(userEntry, videoDatabase);
            userDatabase.put(newUser.getUsername(), newUser);
        }
    }

    public HashMap<String, User> getUserDatabase() {
        return userDatabase;
    }

    // for debugging
    public void printUserdata() {
        userDatabase.forEach(((s, user) -> System.out.println(user)));
    }
}
