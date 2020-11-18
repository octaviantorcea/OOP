package database;

import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.LinkedHashMap;
import java.util.List;

public class VideoDatabase {
    private LinkedHashMap<String, Video> videoDatabase = new LinkedHashMap<>();

    public VideoDatabase(List<MovieInputData> movieData,
                         List<SerialInputData> serialData) {
        for (MovieInputData movieEntry : movieData) {
            Movie newMovie = new Movie();
            newMovie.readVideo(movieEntry);
            videoDatabase.put(newMovie.getTitle(), newMovie);
        }

        for (SerialInputData serialEntry : serialData) {
            Show newShow = new Show();
            newShow.readVideo(serialEntry);
            videoDatabase.put(newShow.getTitle(), newShow);
        }
    }

    public LinkedHashMap<String, Video> getVideoDatabase() {
        return videoDatabase;
    }

    // for debugging
    public void printDatabase() {
        videoDatabase.forEach((s, video) -> System.out.println(video));
    }
}
