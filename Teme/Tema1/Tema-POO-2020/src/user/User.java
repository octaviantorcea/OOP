package user;

import database.VideoDatabase;
import entertainment.Video;
import fileio.UserInputData;
import static common.Constants.BASIC;
import static common.Constants.PREMIUM;

import java.util.HashMap;
import java.util.HashSet;

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

    private void readFavVideos(UserInputData userData, VideoDatabase videoDatabase) {
        for (String title : userData.getFavoriteMovies()) {
            Video favVideo = videoDatabase.getVideoDatabase().get(title);
            favVideos.add(favVideo);
            favVideo.setNrOfFav(favVideo.getNrOfFav() + 1);
        }
    }

    private void readViewedList(UserInputData userData, VideoDatabase videoDatabase) {
        userData.getHistory().forEach(((title, views) -> {
            Video viewedVideo = videoDatabase.getVideoDatabase().get(title);
            viewedVideo.setViews(viewedVideo.getViews() + views);
            viewedList.put(viewedVideo, views);
        }));
    }

    public User(UserInputData userData, VideoDatabase videoDatabase) {
        this.username = userData.getUsername();
        this.subscription = readSubscription(userData);
        readFavVideos(userData, videoDatabase);
        readViewedList(userData, videoDatabase);
    }

    public String getUsername() {
        return username;
    }

    public HashSet<Video> getFavVideos() {
        return favVideos;
    }

    public HashMap<Video, Integer> getViewedList() {
        return viewedList;
    }

    // for debugging
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", subscription=" + subscription +
                ", favVideos=" + favVideos +
                ", viewedList=" + viewedList +
                ", nrOfRatings=" + nrOfRatings +
                '}';
    }
}
