package database;

import fileio.UserInputData;
import user.User;

import java.util.HashMap;
import java.util.List;

/**
 * Contains all the users.<br>
 * Maps the username to the an "User" object.
 */
public final class UserDatabase {
    private final HashMap<String, User> userDatabase = new HashMap<>();

    public UserDatabase(final List<UserInputData> userData, final VideoDatabase videoDatabase,
                        final GenreDatabase genreDatabase) {
        for (UserInputData userEntry : userData) {
            User newUser = new User(userEntry, videoDatabase, genreDatabase);
            userDatabase.put(newUser.getUsername(), newUser);
        }
    }

    public HashMap<String, User> getUserDatabase() {
        return userDatabase;
    }
}
