package user;

import database.GenreDatabase;
import database.VideoDatabase;
import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.UserInputData;
import utils.Utils;

import static common.Constants.BASIC;
import static common.Constants.PREMIUM;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Contains info about an user.
 */
public final class User {
    private final String username;
    private final boolean subscription;
    private final HashSet<Video> favVideos = new HashSet<>();
    /**
     * Retains the videos that have been seen and the amount of times that
     * user watched that video.
     */
    private final HashMap<Video, Integer> viewedList = new HashMap<>();
    /**
     * The number of times this user has rated a video.
     */
    private int nrOfRatings = 0;
    /**
     * List of movies this user has rated.
     */
    private final HashSet<String> ratedMovies = new HashSet<>();
    /**
     * List of shows this user has rated.
     */
    private final HashSet<String> ratedShows = new HashSet<>();

    public User(final UserInputData userData, final VideoDatabase videoDatabase,
                final GenreDatabase genreDatabase) {
        this.username = userData.getUsername();
        this.subscription = readSubscription(userData);
        readFavVideos(userData, videoDatabase);
        readViewedList(userData, videoDatabase, genreDatabase);
    }

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
     * Increments the number of times a video has been favored.
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
     * Updates the views of every video added.
     * Updates the views of each genre of the videos added.
     * @param userData data that needs to be put nicely
     * @param videoDatabase class where is stored all the videos
     */
    private void readViewedList(final UserInputData userData, final VideoDatabase videoDatabase,
                                final GenreDatabase genreDatabase) {
        userData.getHistory().forEach(((title, views) -> {
            Video viewedVideo = videoDatabase.getVideoDatabase().get(title);
            viewedVideo.setViews(viewedVideo.getViews() + views);
            viewedList.put(viewedVideo, views);

            viewedVideo.getGenres().forEach(genre ->
                    genreDatabase.getGenreDatabase().put(Utils.stringToGenre(genre),
                    genreDatabase.getGenreDatabase().get(Utils.stringToGenre(genre)) + views));
        }));
    }

    /**
     * Adds and increments number of times this video was added to a user's
     * favorite list.
     * @param video the video that is added to favorite list
     */
    public void addVideoInFavList(final Video video) {
        this.favVideos.add(video);
        video.incNrOfFav();
    }

    /**
     * Increments the views of the video and the genres of that video.<br>
     * Puts the video in the viewed list of the user if it wasn't watched.
     * @param video the video that is viewed
     * @param genreDatabase the database of genres
     */
    public void watchVideo(final Video video, final GenreDatabase genreDatabase) {
        video.incViews();

        video.getGenres().forEach(genreName ->
                genreDatabase.getGenreDatabase().put(Utils.stringToGenre(genreName),
                        genreDatabase.getGenreDatabase().get(Utils.stringToGenre(genreName)) + 1));

        if (!this.viewedList.containsKey(video)) {
            this.viewedList.put(video, 1);
        } else {
            this.viewedList.put(video, this.viewedList.get(video) + 1);
        }
    }

    /**
     * Increments the number of times that user rated a video.<br>
     * Adds the movie in the list of rated movies.<br>
     * Adds the grade in the list of grades of that movie.<br>
     * Computes the new avgRating of that movie.
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
}
