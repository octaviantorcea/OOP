package action;

import database.ActorDatabase;
import database.GenreDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import entertainment.Genre;
import entertainment.Video;
import fileio.ActionInputData;
import user.User;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static common.Constants.ADDED_FAV;
import static common.Constants.ALREADY_FAV;
import static common.Constants.ALREADY_RATED;
import static common.Constants.AVERAGE;
import static common.Constants.AWARDS;
import static common.Constants.BEST_UNSEEN;
import static common.Constants.BEST_UNSEEN_REC;
import static common.Constants.BY;
import static common.Constants.CANT_APPLY;
import static common.Constants.COMMAND;
import static common.Constants.DESCENDING;
import static common.Constants.ERROR;
import static common.Constants.FAVORITE;
import static common.Constants.FAV_REC;
import static common.Constants.FILTER_DESCRIPTIONS;
import static common.Constants.LONGEST;
import static common.Constants.MOST_VIEWED;
import static common.Constants.MOVIES;
import static common.Constants.NOT_SEEN;
import static common.Constants.NUM_RATINGS;
import static common.Constants.POPULAR;
import static common.Constants.POPULAR_REC;
import static common.Constants.QUERY;
import static common.Constants.QUERY_REZZ;
import static common.Constants.RATING;
import static common.Constants.RATINGS;
import static common.Constants.RECOMMENDATION;
import static common.Constants.REZZ;
import static common.Constants.SEARCH;
import static common.Constants.SEARCH_REC;
import static common.Constants.STANDARD;
import static common.Constants.STANDARD_REC;
import static common.Constants.SUCCESS;
import static common.Constants.VIEW;
import static common.Constants.WAS_RATED;
import static common.Constants.WAS_VIEWED;

/**
 * Contains info about an action.<br>
 * Has a method that executes every type of action.
 */
public final class Action {
    private final int actionId;
    private final String actionType;
    private final String type;
    private final String username;
    private final String objectType;
    private final String sortType;
    private final String criteria;
    private final String title;
    private final String genre;
    private final int number;
    private final double grade;
    private final int seasonNumber;
    private final List<List<String>> filters;

    public Action(final ActionInputData actionData) {
        this.actionId = actionData.getActionId();
        this.actionType = actionData.getActionType();
        this.type = actionData.getType();
        this.username = actionData.getUsername();
        this.objectType = actionData.getObjectType();
        this.sortType = actionData.getSortType();
        this.criteria = actionData.getCriteria();
        this.title = actionData.getTitle();
        this.genre = actionData.getGenre();
        this.number = actionData.getNumber();
        this.grade = actionData.getGrade();
        this.seasonNumber = actionData.getSeasonNumber();
        this.filters = actionData.getFilters();
    }

    /**
     * Calls the necessary method to execute the action depending on the
     * action's type.
     * @param actorDatabase all actors
     * @param userDatabase all users
     * @param videoDatabase all videos
     * @param genreDatabase all genres
     * @return the message as a string that will be put in the JSONArray
     */
    public String executeAction(final ActorDatabase actorDatabase,
                                final UserDatabase userDatabase,
                                final VideoDatabase videoDatabase,
                                final GenreDatabase genreDatabase) {

        User user = userDatabase.getUserDatabase().get(this.username);
        Video video = videoDatabase.getVideoDatabase().get(this.title);

        return switch (this.actionType) {
            case (COMMAND) -> command(user, video, genreDatabase);
            case (QUERY) -> query(actorDatabase, videoDatabase, userDatabase);
            case (RECOMMENDATION) -> recommendation(user, videoDatabase, genreDatabase);
            default -> null;
        };
    }

    /**
     * Executes one of the 3 commands.
     * @param user the user that "does" the command
     * @param video the video that the user does the command on
     * @param genreDatabase all genres
     * @return the message as a string that will be put in the JSONArray
     */
    private String command(final User user, final Video video, final GenreDatabase genreDatabase) {
        return switch (this.type) {
            case (FAVORITE) -> favCom(user, video);
            case (VIEW) -> viewCom(user, video, genreDatabase);
            case (RATING) -> rateCom(user, video);
            default -> null;
        };
    }

    /**
     * Tries to add the video in the user's favorite list if that video was
     * seen by the user and is not already in the favorite list.
     * @see user.User#addVideoInFavList
     * @param user the user that favors a video
     * @param video the video that has been favored
     * @return the message as a string that will be put in the JSONArray
     */
    private String favCom(final User user, final Video video) {
        if (!user.getFavVideos().contains(video)
                && user.getViewedList().containsKey(video)) {
            user.addVideoInFavList(video);
            return SUCCESS + video.getTitle() + ADDED_FAV;
        } else if (user.getFavVideos().contains(video)) {
            return ERROR + video.getTitle() + ALREADY_FAV;
        } else {
            return ERROR + video.getTitle() + NOT_SEEN;
        }
    }

    /**
     * Calls the method from the user class to watch a video.
     * @see user.User#watchVideo
     * @param user the user that views a video
     * @param video the video that has been viewed
     * @param genreDatabase all the genres
     * @return the message as a string that will be put in the JSONArray
     */
    private String viewCom(final User user, final Video video, final GenreDatabase genreDatabase) {
        user.watchVideo(video, genreDatabase);
        return SUCCESS + video.getTitle() + WAS_VIEWED + user.getViewedList().get(video);
    }

    /**
     * Uses the methods from the user class to rate a video.<br>
     * If the video is not seen by the user it will return the
     * specific error message.
     * @see user.User#rateMovie
     * @see user.User#rateShow
     * @param user the user that rates a video
     * @param video the video that has been rated
     * @return the message as a string that will be put in the JSONArray
     */
    private String rateCom(final User user, final Video video) {
        if (!user.getViewedList().containsKey(video)) {
            return ERROR + video.getTitle() + NOT_SEEN;
        } else {
            if (!video.isShow()) {
                if (user.getRatedMovies().contains(this.title)) {
                    return ERROR + video.getTitle() + ALREADY_RATED;
                } else {
                    user.rateMovie(video, this.grade);
                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                }
            } else {
                if (user.getRatedShows().contains(this.title + this.seasonNumber)) {
                    return ERROR + video.getTitle() + ALREADY_RATED;
                } else {
                    user.rateShow(this.seasonNumber, video, this.grade);
                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                }
            }
        }
    }

    /**
     * Applies the query depending on it's criteria.
     * @param actorDatabase actor database
     * @param videoDatabase video database
     * @param userDatabase user database
     * @return the message as a string that will be put in the JSONArray
     */
    private String query(final ActorDatabase actorDatabase, final VideoDatabase videoDatabase,
                         final UserDatabase userDatabase) {
        return switch (this.criteria) {
            case (AVERAGE) -> averageQuery(actorDatabase, videoDatabase);
            case (AWARDS) -> awardsQuery(actorDatabase);
            case (FILTER_DESCRIPTIONS) -> descriptionQuery(actorDatabase);
            case (RATINGS) -> ratingsQuery(videoDatabase);
            case (FAVORITE) -> favQuery(videoDatabase);
            case (LONGEST) -> longestQuery(videoDatabase);
            case (MOST_VIEWED) -> mostViewedQuery(videoDatabase);
            case (NUM_RATINGS) -> userQuery(userDatabase);
            default -> null;
        };
    }

    /**
     * @see database.ActorDatabase#getAvgQuery
     * @param actorDatabase the database on which the query is made
     * @param videoDatabase video database needed to compute average grade
     * @return the message as a string that will be put in the JSONArray
     */
    private String averageQuery(final ActorDatabase actorDatabase,
                                final VideoDatabase videoDatabase) {
        ArrayList<String> actorsNames = actorDatabase.getAvgQuery(videoDatabase);
        reverseAndTrimIfNecessary(actorsNames);

        return QUERY_REZZ + actorsNames;
    }


    /**
     * @see database.ActorDatabase#getAwardsQuery
     * @param actorDatabase the database on which the query is made
     * @return the message as a string that will be put in the JSONArray
     */
    private String awardsQuery(final ActorDatabase actorDatabase) {
        ArrayList<String> actorsNames = actorDatabase.getAwardsQuery(this);
        reverseAndTrimIfNecessary(actorsNames);

        return QUERY_REZZ + actorsNames;
    }

    /**
     * @see database.ActorDatabase#getDescriptionQuery
     * @param actorDatabase the database on which the query is made
     * @return the message as a string that will be put in the JSONArray
     */
    private String descriptionQuery(final ActorDatabase actorDatabase) {
        ArrayList<String> actors = actorDatabase.getDescriptionQuery(this);
        reverseAndTrimIfNecessary(actors);

        return QUERY_REZZ + actors;
    }

    private String ratingsQuery(final VideoDatabase videoDatabase) {
        ArrayList<Video> ratedVideos = new ArrayList<>();

        if (MOVIES.equals(this.objectType)) {
            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.getAvgRating() > 0
                        && video.isFiltered(this)) {
                    ratedVideos.add(video);
                }
            }
        } else {
            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getAvgRating() > 0
                        && video.isFiltered(this)) {
                    ratedVideos.add(video);
                }
            }
        }

        return getRatedVideos(ratedVideos);
    }

    private String favQuery(final VideoDatabase videoDatabase) {
        if (MOVIES.equals(this.objectType)) {
            ArrayList<Video> favMovies = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.getNrOfFav() > 0
                        && video.isFiltered(this)) {
                    favMovies.add(video);
                }
            }

            return getFavVideos(favMovies);
        } else {
            ArrayList<Video> favShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getNrOfFav() > 0
                        && video.isFiltered(this)) {
                    favShows.add(video);
                }
            }

            return getFavVideos(favShows);
        }
    }

    private String longestQuery(final VideoDatabase videoDatabase) {
        if (MOVIES.equals(this.objectType)) {
            ArrayList<Video> longestMovies = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.isFiltered(this)) {
                    longestMovies.add(video);
                }
            }

            return getLongVideos(longestMovies);
        } else {
            ArrayList<Video> longestShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.isFiltered(this)) {
                    longestShows.add(video);
                }
            }

            return getLongVideos(longestShows);
        }
    }

    private String mostViewedQuery(final VideoDatabase videoDatabase) {
        if (MOVIES.equals(this.objectType)) {
            ArrayList<Video> mostViewedMovies = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.getViews() > 0
                        && video.isFiltered(this)) {
                    mostViewedMovies.add(video);
                }
            }

            return getMostViewedVideos(mostViewedMovies);
        } else {
            ArrayList<Video> mostViewedShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getViews() > 0
                        && video.isFiltered(this)) {
                    mostViewedShows.add(video);
                }
            }

            return getMostViewedVideos(mostViewedShows);
        }
    }

    /**
     * @see UserDatabase#getPopularUsers
     * @param userDatabase the database on which the query is made
     * @return the message as a string that will be put in the JSONArray
     */
    private String userQuery(final UserDatabase userDatabase) {
        ArrayList<String> users = userDatabase.getPopularUsers();
        reverseAndTrimIfNecessary(users);

        return QUERY_REZZ + users;
    }

    private String recommendation(final User user, final VideoDatabase videoDatabase,
                                  final GenreDatabase genreDatabase) {
        return switch (this.type) {
            case (STANDARD) -> standardRec(user, videoDatabase);
            case (BEST_UNSEEN) -> bestUnseenRec(user, videoDatabase);
            case (POPULAR) -> popularRec(user, videoDatabase, genreDatabase);
            case (FAVORITE) -> favRec(user, videoDatabase);
            case (SEARCH) -> searchRec(user, videoDatabase);
            default -> null;
        };
    }

    private String standardRec(final User user, final VideoDatabase videoDatabase) {
        String standardRec = "";

        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
            if (!user.getViewedList().containsKey(videoEntry)) {
                standardRec = videoEntry.getTitle();
                break;
            }
        }

        if (standardRec.isBlank()) {
            return STANDARD_REC + CANT_APPLY;
        } else {
            return STANDARD_REC + REZZ + standardRec;
        }
    }

    private String bestUnseenRec(final User user, final VideoDatabase videoDatabase) {
        Video bestUnseenRec = null;

        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
            if (!user.getViewedList().containsKey(videoEntry)) {
                if (bestUnseenRec == null) {
                    bestUnseenRec = videoEntry;
                } else {
                    if (videoEntry.getAvgRating() > bestUnseenRec.getAvgRating()) {
                        bestUnseenRec = videoEntry;
                    }
                }
            }
        }

        if (bestUnseenRec == null) {
            return BEST_UNSEEN_REC + CANT_APPLY;
        } else {
            return BEST_UNSEEN_REC + REZZ + bestUnseenRec.getTitle();
        }
    }

    private String popularRec(final User user, final VideoDatabase videoDatabase,
                              final GenreDatabase genreDatabase) {
        if (!user.getSubscription()) {
            return POPULAR_REC + CANT_APPLY;
        }

        ArrayList<Genre> allGenres = new ArrayList<>(genreDatabase.getGenreDatabase().keySet());

        allGenres.sort(Comparator.comparingInt(key -> genreDatabase.getGenreDatabase().get(key)));

        ArrayList<String> stringGenres = new ArrayList<>();

        for (Genre genres : allGenres) {
            stringGenres.add(Utils.genreToString(genres));
        }
        Collections.reverse(stringGenres);

        for (String genreName : stringGenres) {
            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.getGenres().contains(genreName)
                        && !user.getViewedList().containsKey(video)) {
                    return POPULAR_REC + REZZ + video.getTitle();
                }
            }
        }

        return POPULAR_REC + CANT_APPLY;
    }

    private String favRec(final User user, final VideoDatabase videoDatabase) {
        if (!user.getSubscription()) {
            return FAV_REC + CANT_APPLY;
        }

        Video bestFav = null;

        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
            if (!user.getViewedList().containsKey(videoEntry)) {
                if (bestFav == null) {
                    if (videoEntry.getNrOfFav() > 0) {
                        bestFav = videoEntry;
                    }
                } else {
                    if (videoEntry.getNrOfFav() > bestFav.getNrOfFav()) {
                        bestFav = videoEntry;
                    }
                }
            }
        }

        if (bestFav == null) {
            return FAV_REC + CANT_APPLY;
        } else {
            return FAV_REC + REZZ + bestFav.getTitle();
        }
    }

    private String searchRec(final User user, final VideoDatabase videoDatabase) {
        if (!user.getSubscription()) {
            return SEARCH_REC + CANT_APPLY;
        }

        ArrayList<String> searchRec = new ArrayList<>();

        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
            if (!user.getViewedList().containsKey(videoEntry)
                    && videoEntry.getGenres().contains(this.genre)) {
                searchRec.add(videoEntry.getTitle());
            }
        }

        if (searchRec.isEmpty()) {
            return SEARCH_REC + CANT_APPLY;
        } else {
            Collections.sort(searchRec);
            return SEARCH_REC + REZZ + searchRec;
        }
    }

    private String getMostViewedVideos(final ArrayList<Video> mostViewedVideos) {
        mostViewedVideos.sort((video1, video2) -> {
            int compare = video1.getViews() - video2.getViews();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(mostViewedVideos);
        }

        while (this.number < mostViewedVideos.size()) {
            mostViewedVideos.remove(this.number);
        }

        return QUERY_REZZ + Utils.videosTitle(mostViewedVideos);
    }

    private String getLongVideos(final ArrayList<Video> longestVideos) {
        longestVideos.sort((video1, video2) -> {
            int compare = video1.getDuration() - video2.getDuration();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(longestVideos);
        }

        while (this.number < longestVideos.size()) {
            longestVideos.remove(this.number);
        }

        return QUERY_REZZ + Utils.videosTitle(longestVideos);
    }

    private String getFavVideos(final ArrayList<Video> favVideos) {
        favVideos.sort((video1, video2) -> {
            int compare = video1.getNrOfFav() - video2.getNrOfFav();

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(favVideos);
        }

        while (this.number < favVideos.size()) {
            favVideos.remove(this.number);
        }

        return QUERY_REZZ + Utils.videosTitle(favVideos);
    }

    private String getRatedVideos(final ArrayList<Video> ratedVideos) {
        ratedVideos.sort((video1, video2) -> {
            int compare = video1.getAvgRating().compareTo(video2.getAvgRating());

            if (compare != 0) {
                return compare;
            } else {
                return video1.getTitle().compareTo(video2.getTitle());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(ratedVideos);
        }

        while (this.number < ratedVideos.size()) {
            ratedVideos.remove(this.number);
        }

        return QUERY_REZZ + Utils.videosTitle(ratedVideos);
    }

    private void reverseAndTrimIfNecessary(final ArrayList<String> list) {
        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(list);
        }

        if (number != 0) {
            while (this.number < list.size()) {
                list.remove(this.number);
            }
        }
    }

    public int getActionId() {
        return actionId;
    }

    public List<List<String>> getFilters() {
        return filters;
    }
}
