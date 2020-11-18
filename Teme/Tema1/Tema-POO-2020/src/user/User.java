package user;

import entertainment.Video;
import fileio.UserInputData;
import common.Constants;

import java.util.HashMap;
import java.util.HashSet;

import static common.Constants.*;

public class User {
    final private String username;
    boolean subscription;
    private HashSet<Video> favVideos = new HashSet<>();
    private HashMap<Video, Integer> viewedList = new HashMap<>();
    private int nrOfRatings = 0;

    private boolean readSubscription(UserInputData userData) {
        if (userData.getSubscriptionType().equals(BASIC)) {
            return false;
        } else if (userData.getSubscriptionType().equals(PREMIUM)) {
            return true;
        } else {
            System.out.println("Wrong subscription type");
            return false;
        }
    }

    private void readFavVideos(UserInputData userData) {

    }

    public User(UserInputData userData) {
        this.username = userData.getUsername();
        this.subscription = readSubscription(userData);

    }
}
