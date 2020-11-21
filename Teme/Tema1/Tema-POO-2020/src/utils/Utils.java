package utils;

import action.Action;
import actor.Actor;
import actor.ActorsAwards;
import common.Constants;
import database.VideoDatabase;
import entertainment.Genre;
import entertainment.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The class contains static methods that helps with parsing.
 *
 * We suggest you add your static methods here or in a similar class.
 */
public final class Utils {
    /**
     * for coding style
     */
    private Utils() {
    }

    /**
     * Transforms a string into an enum
     * @param genre of video
     * @return an Genre Enum
     */
    public static Genre stringToGenre(final String genre) {
        return switch (genre.toLowerCase()) {
            case "action" -> Genre.ACTION;
            case "adventure" -> Genre.ADVENTURE;
            case "drama" -> Genre.DRAMA;
            case "comedy" -> Genre.COMEDY;
            case "crime" -> Genre.CRIME;
            case "romance" -> Genre.ROMANCE;
            case "war" -> Genre.WAR;
            case "history" -> Genre.HISTORY;
            case "thriller" -> Genre.THRILLER;
            case "mystery" -> Genre.MYSTERY;
            case "family" -> Genre.FAMILY;
            case "horror" -> Genre.HORROR;
            case "fantasy" -> Genre.FANTASY;
            case "science fiction" -> Genre.SCIENCE_FICTION;
            case "action & adventure" -> Genre.ACTION_ADVENTURE;
            case "sci-fi & fantasy" -> Genre.SCI_FI_FANTASY;
            case "animation" -> Genre.ANIMATION;
            case "kids" -> Genre.KIDS;
            case "western" -> Genre.WESTERN;
            case "tv movie" -> Genre.TV_MOVIE;
            default -> null;
        };
    }

    /**
     * Transforms a string into an enum
     * @param award for actors
     * @return an ActorsAwards Enum
     */
    public static ActorsAwards stringToAwards(final String award) {
        return switch (award) {
            case "BEST_SCREENPLAY" -> ActorsAwards.BEST_SCREENPLAY;
            case "BEST_SUPPORTING_ACTOR" -> ActorsAwards.BEST_SUPPORTING_ACTOR;
            case "BEST_DIRECTOR" -> ActorsAwards.BEST_DIRECTOR;
            case "BEST_PERFORMANCE" -> ActorsAwards.BEST_PERFORMANCE;
            case "PEOPLE_CHOICE_AWARD" -> ActorsAwards.PEOPLE_CHOICE_AWARD;
            default -> null;
        };
    }

    /**
     * Transforms an array of JSON's into an array of strings
     * @param array of JSONs
     * @return a list of strings
     */
    public static ArrayList<String> convertJSONArray(final JSONArray array) {
        if (array != null) {
            ArrayList<String> finalArray = new ArrayList<>();
            for (Object object : array) {
                finalArray.add((String) object);
            }
            return finalArray;
        } else {
            return null;
        }
    }

    /**
     * Transforms an array of JSON's into a map
     * @param jsonActors array of JSONs
     * @return a map with ActorsAwardsa as key and Integer as value
     */
    public static Map<ActorsAwards, Integer> convertAwards(final JSONArray jsonActors) {
        Map<ActorsAwards, Integer> awards = new LinkedHashMap<>();

        for (Object iterator : jsonActors) {
            awards.put(stringToAwards((String) ((JSONObject) iterator).get(Constants.AWARD_TYPE)),
                    Integer.parseInt(((JSONObject) iterator).get(Constants.NUMBER_OF_AWARDS)
                            .toString()));
        }

        return awards;
    }

    /**
     * Transforms an array of JSON's into a map
     * @param movies array of JSONs
     * @return a map with String as key and Integer as value
     */
    public static Map<String, Integer> watchedMovie(final JSONArray movies) {
        Map<String, Integer> mapVideos = new LinkedHashMap<>();

        if (movies != null) {
            for (Object movie : movies) {
                mapVideos.put((String) ((JSONObject) movie).get(Constants.NAME),
                        Integer.parseInt(((JSONObject) movie).get(Constants.NUMBER_VIEWS)
                                .toString()));
            }
        } else {
            System.out.println("NU ESTE VIZIONAT NICIUN FILM");
        }

        return mapVideos;
    }

    /**
     * Returns a string representation of only the usernames of users from a list.
     * @param users the list of users whose usernames will be converted to string
     * @return the string that contains only the usernames of users from a list
     */
    public static String usernamesToString(final ArrayList<User> users) {
        StringBuilder toArray = new StringBuilder("[");

        for (int i = 0; i < users.size() - 1; i++) {
            toArray.append(users.get(i).getUsername()).append(", ");
        }

        toArray.append(users.get(users.size() - 1).getUsername()).append("]");

        return toArray.toString();
    }

    /**
     * Verify if a video respects all the filters (if any) of an action.
     * @param video the video that needs to be verified
     * @param action contains the filters
     * @return true if video respects the filters, false otherwise
     */
    public static boolean isFiltered(final Video video, final Action action) {
        List<String> years = action.getFilters().get(0);
        List<String> genres = action.getFilters().get(1);

        if (years.get(0) != null) {
            if (!years.get(0).equals(((Integer) video.getYear()).toString())) {
                return false;
            }
        }

        if (genres.get(0) != null) {
            return !(Collections.disjoint(genres, video.getGenres()));
        }

        return true;
    }

    /**
     * Returns a string representation of only the titles of videos from a list.
     * @param videos the list of videos whose titles will be converted to string
     * @return the string that contains only the titles of videos from a list
     */
    public static String videosTitle(final ArrayList<Video> videos) {
        if (videos.isEmpty()) {
            return "[]";
        }

        StringBuilder toArray = new StringBuilder("[");

        for (int i = 0; i < videos.size() - 1; i++) {
            toArray.append(videos.get(i).getTitle()).append(", ");
        }

        toArray.append(videos.get(videos.size() - 1).getTitle()).append("]");

        return toArray.toString();
    }

    /**
     * Transforms a string to a string array that contains only the words from the string (all
     * lowercase).
     * @param string the string that will be transformed.
     * @return the array of strings
     */
    public static ArrayList<String> stringToArray(final String string) {
        String auxStr = string.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("\\.", "").toLowerCase();
        return new ArrayList<>(Arrays.asList(auxStr.split(" ")));
    }

    /**
     * Computes the average grade for an Actor.<br>
     * Only videos that are graded are considered.
     * @param actor actor whose grade will be calculated
     * @param videoDatabase the video database
     */
    public static void computeActorGrade(final Actor actor, final VideoDatabase videoDatabase) {
        double sumGrade = 0;
        int ratedVideos = 0;

        for (String title : actor.getFilmography()) {
            Video video = videoDatabase.getVideoDatabase().get(title);

            if (video != null && video.getAvgRating() > 0) {
                sumGrade += video.getAvgRating();
                ratedVideos++;
            }
        }

        if (ratedVideos == 0) {
            actor.setAverageRating(0d);
        } else {
            actor.setAverageRating(sumGrade / ratedVideos);
        }
    }

    /**
     * Transforms a Genre into a string.
     * @param genre of video
     * @return a string
     */
    public static String genreToString(final Genre genre) {
        return switch (genre) {
            case ACTION -> "Action";
            case ADVENTURE -> "Adventure";
            case DRAMA -> "Drama";
            case COMEDY -> "Comedy";
            case CRIME -> "Crime";
            case ROMANCE -> "Romance";
            case WAR -> "War";
            case HISTORY -> "History";
            case THRILLER -> "Thriller";
            case MYSTERY -> "Mystery";
            case FAMILY -> "Family";
            case HORROR -> "Horror";
            case FANTASY -> "Fantasy";
            case SCIENCE_FICTION -> "Science Fiction";
            case ACTION_ADVENTURE -> "Action & Adventure";
            case SCI_FI_FANTASY -> "Sci-Fi & Fantasy";
            case ANIMATION -> "Animation";
            case KIDS -> "Kids";
            case WESTERN -> "Western";
            case TV_MOVIE -> "TV Movie";
        };
    }
}
