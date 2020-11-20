package database;

import fileio.UserInputData;
import user.User;

import java.util.HashMap;
import java.util.List;

public final class UserDatabase {
    private final HashMap<String, User> userDatabase = new HashMap<>();

    public UserDatabase(final List<UserInputData> userData, final VideoDatabase videoDatabase) {
        for (UserInputData userEntry : userData) {
            User newUser = new User(userEntry, videoDatabase);
            userDatabase.put(newUser.getUsername(), newUser);
        }
    }

    public HashMap<String, User> getUserDatabase() {
        return userDatabase;
    }
}
