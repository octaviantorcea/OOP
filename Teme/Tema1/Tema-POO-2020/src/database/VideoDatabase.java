package database;

import action.Action;
import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static common.Constants.MOVIES;

/**
 * Contains all the videos.<br>
 * Videos are stored in the order they are given.<br>
 * Maps the title of the video to a "Video" object.
 */
public final class VideoDatabase {
    private final LinkedHashMap<String, Video> videoDatabase = new LinkedHashMap<>();

    public VideoDatabase(final List<MovieInputData> movieData,
                         final List<SerialInputData> serialData) {
        for (MovieInputData movieEntry : movieData) {
            Movie newMovie = new Movie(movieEntry);
            videoDatabase.put(newMovie.getTitle(), newMovie);
        }

        for (SerialInputData serialEntry : serialData) {
            Show newShow = new Show(serialEntry);
            videoDatabase.put(newShow.getTitle(), newShow);
        }
    }

    /**
     * @param action the action that contains the filters and tells the query
     *               if it's for movies or shows
     * @return an array string containing the titles of all the rated
     * movies/shows sorted (in ascending order) by their average rating
     */
    public ArrayList<String> getRatedVideos(final Action action) {
        ArrayList<String> videosTitle = new ArrayList<>();
        ArrayList<Video> ratedVideos = new ArrayList<>();

        for (Video video : videoDatabase.values()) {
            if (video.getAvgRating() > 0 && video.isFiltered(action)) {
                ratedVideos.add(video);
            }
        }

        // sort them by their garde; if equal -> sort alphabetically
        ratedVideos.sort((video1, video2) -> {
            int compare = video1.getAvgRating().compareTo(video2.getAvgRating());

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        removeUnnecessary(ratedVideos, action);
        videoToString(ratedVideos, videosTitle);

        return videosTitle;
    }

    /**
     * @param action the action that contains the filters and tells the query
     *               if it's for movies or shows
     * @return an array string containing the titles of all the movies/shows
     * that have been added to favorite list at least once, sorted
     * (in ascending order) by their number of times it has been added to
     * favorite list
     */
    public ArrayList<String> getFavVideos(final Action action) {
        ArrayList<String> videosTitle = new ArrayList<>();
        ArrayList<Video> favVideos = new ArrayList<>();

        for (Video video : videoDatabase.values()) {
            if (video.getNrOfFav() > 0 && video.isFiltered(action)) {
                favVideos.add(video);
            }
        }

        favVideos.sort((video1, video2) -> {
            int compare = video1.getNrOfFav() - video2.getNrOfFav();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        removeUnnecessary(favVideos, action);
        videoToString(favVideos, videosTitle);

        return videosTitle;
    }

    /**
     * @param action the action that contains the filters and tells the query
     *               if it's for movies or shows
     * @return an array string containing the titles of all the movies/shows
     * sorted (in ascending order) by their duration
     */
    public ArrayList<String> getLongVideos(final Action action) {
        ArrayList<String> videosTitle = new ArrayList<>();
        ArrayList<Video> longVideos = new ArrayList<>();

        for (Video video : videoDatabase.values()) {
            if (video.isFiltered(action)) {
                longVideos.add(video);
            }
        }

        // sort them by their duration; if equal -> sort alphabetically
        longVideos.sort((video1, video2) -> {
            int compare = video1.getDuration() - video2.getDuration();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        removeUnnecessary(longVideos, action);
        videoToString(longVideos, videosTitle);

        return videosTitle;
    }

    /**
     * @param action the action that contains the filters and tells the query
     *               if it's for movies or shows
     * @return an array string containing the titles of all the movies/shows
     * that have been viewed at least once, sorted (in ascending order) by their views
     */
    public ArrayList<String> getMostViewedVideos(final Action action) {
        ArrayList<String> videosTitle = new ArrayList<>();
        ArrayList<Video> mostViewedVideos = new ArrayList<>();

        for (Video video : videoDatabase.values()) {
            if (video.getViews() > 0 && video.isFiltered(action)) {
                mostViewedVideos.add(video);
            }
        }

        // sort them by their views; if equal -> sort alphabetically
        mostViewedVideos.sort((video1, video2) -> {
            int compare = video1.getViews() - video2.getViews();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        removeUnnecessary(mostViewedVideos, action);
        videoToString(mostViewedVideos, videosTitle);

        return videosTitle;
    }

    /**
     * verifies if the query is for movies or for shows
     */
    private void removeUnnecessary(final ArrayList<Video> videos, final Action action) {
        if (action.getObjectType().equals(MOVIES)) {
            videos.removeIf(Video::isShow);
        } else {
            videos.removeIf(video -> !video.isShow());
        }
    }

    private void videoToString(final ArrayList<Video> videos, final ArrayList<String> videosTitle) {
        for (Video video : videos) {
            videosTitle.add(video.getTitle());
        }
    }

    public LinkedHashMap<String, Video> getVideoDatabase() {
        return videoDatabase;
    }
}
