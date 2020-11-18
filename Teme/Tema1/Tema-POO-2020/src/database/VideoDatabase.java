package database;

import entertainment.Movie;
import entertainment.Show;
import entertainment.Video;
import fileio.Input;
import fileio.MovieInputData;
import fileio.SerialInputData;

import java.util.LinkedHashSet;
import java.util.List;

public class VideoDatabase {
    private LinkedHashSet<Video> videoDatabase = new LinkedHashSet<>();

    public VideoDatabase(List<MovieInputData> movieData,
                         List<SerialInputData> serialData) {
        for (MovieInputData movieEntry : movieData) {
            Movie newMovie = new Movie();
            newMovie.readVideo(movieEntry);
            videoDatabase.add(newMovie);
        }

        for (SerialInputData serialEntry : serialData) {
            Show newShow = new Show();
            newShow.readVideo(serialEntry);
            videoDatabase.add(newShow);
        }
    }

    // for debugging
    public void printDatabase() {
        for (Video video : videoDatabase) {
            System.out.println(video);
        }
    }
}
