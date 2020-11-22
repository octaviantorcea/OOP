package database;

import fileio.UserInputData;
import user.User;

import java.util.ArrayList;
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

    /**
     * @return an array of all the users that have rated at least one video,
     * sorted (in ascending order) by the numbers of ratings
     */
    public ArrayList<String> getPopularUsers() {
        ArrayList<String> usernames = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();

        for (User user : userDatabase.values()) {
            if (user.getNrOfRatings() > 0) {
                users.add(user);
            }
        }

        // sort them by their number of ratings; if equal -> sort alphabetically
        users.sort((user1, user2) -> {
            int compare = user1.getNrOfRatings() - user2.getNrOfRatings();

            if (compare != 0) {
                return compare;
            } else {
                return user1.getUsername().compareTo(user2.getUsername());
            }
        });

        for (User user : users) {
            usernames.add(user.getUsername());
        }

        return usernames;
    }

    public HashMap<String, User> getUserDatabase() {
        return userDatabase;
    }
}
