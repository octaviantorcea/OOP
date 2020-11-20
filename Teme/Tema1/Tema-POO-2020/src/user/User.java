package user;

import database.VideoDatabase;
import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.UserInputData;
import static common.Constants.BASIC;
import static common.Constants.PREMIUM;

import java.util.HashMap;
import java.util.HashSet;

public final class User {
    private final String username;
    private final boolean subscription;
    private final HashSet<Video> favVideos = new HashSet<>();
    private final HashMap<Video, Integer> viewedList = new HashMap<>();
    private int nrOfRatings = 0;
    private final HashSet<String> ratedMovies = new HashSet<>();
    private final HashSet<String> ratedShows = new HashSet<>();

    private boolean readSubscription(final UserInputData userData) {
        if (userData.getSubscriptionType().equals(BASIC)) {
            return false;
        } else if (userData.getSubscriptionType().equals(PREMIUM)) {
            return true;
        } else {
            System.out.println("Wrong subscription type");
            return false;
        }
    }

    /**
     * Puts favorite videos from UserInputData in favVideo HashSet.<br>
     * Also increments the number of times a video has been favored.
     * @param userData data that needs to be put nicely
     * @param videoDatabase class where is stored all the videos
     */
    private void readFavVideos(final UserInputData userData, final VideoDatabase videoDatabase) {
        for (String title : userData.getFavoriteMovies()) {
            Video favVideo = videoDatabase.getVideoDatabase().get(title);
            favVideos.add(favVideo);
            favVideo.setNrOfFav(favVideo.getNrOfFav() + 1);
        }
    }

    /**
     * Reads the "history" attribute of UserInputData and puts every video from history in
     * viewedList.<br>
     * Also updates the views of every video added.
     * @param userData data that needs to be put nicely
     * @param videoDatabase class where is stored all the videos
     */
    private void readViewedList(final UserInputData userData, final VideoDatabase videoDatabase) {
        userData.getHistory().forEach(((title, views) -> {
            Video viewedVideo = videoDatabase.getVideoDatabase().get(title);
            viewedVideo.setViews(viewedVideo.getViews() + views);
            viewedList.put(viewedVideo, views);
        }));
    }

    /**
     * Increments the number of times that user rated a video.<br>
     * Adds the movie in the list of rated movies.<br>
     * Adds the grade in the list of grades of that movie.<br>
     * Computes the new avgRating oh that movie.
     * @param movie the movie that is rated
     * @param rating the grade that is attributed to the movie
     */
    public void rateMovie(final Video movie, final Double rating) {
        nrOfRatings++;
        ratedMovies.add(movie.getTitle());
        ((Movie) movie).getRatings().add(rating);
        movie.calculateAverageRating();
    }


    /**
     * Increments the number of times that user rated a video.<br>
     * Adds the show in the list of rated shows.<br>
     * Adds the grade in the list of grades of that show.<br>
     * Computes the new avgRating of that show.
     * @param season the season that is rated
     * @param show the show that is rated
     * @param rating the grade that is attributed to the season of the show
     */
    public void rateShow(final int season, final Video show, final Double rating) {
        nrOfRatings++;
        ratedShows.add(show.getTitle() + season);
        ((Show) show).getSeasons().get(season - 1).getRatings().add(rating);
        show.calculateAverageRating();
    }

    public User(final UserInputData userData, final VideoDatabase videoDatabase) {
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

    public HashSet<String> getRatedMovies() {
        return ratedMovies;
    }

    public HashSet<String> getRatedShows() {
        return ratedShows;
    }

    public boolean getSubscription() {
        return this.subscription;
    }

    public int getNrOfRatings() {
        return nrOfRatings;
    }

    // for debugging
    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", subscription=" + subscription
                + ", favVideos=" + favVideos
                + ", viewedList=" + viewedList
                + ", nrOfRatings=" + nrOfRatings
                + '}';
    }
}
