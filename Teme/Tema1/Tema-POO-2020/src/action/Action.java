package action;

import actor.Actor;
import actor.ActorsAwards;
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

import static common.Constants.*;

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

    public int getActionId() {
        return actionId;
    }

    public List<List<String>> getFilters() {
        return filters;
    }

    private String command(final User user, final Video video, final GenreDatabase genreDatabase) {
        return switch (this.type) {
            case (FAVORITE) -> favCom(user, video);
            case (VIEW) -> viewCom(user, video, genreDatabase);
            case (RATING) -> rateCom(user, video);
            default -> null;
        };
    }

    private String favCom(final User user, final Video video) {
        if (!user.getFavVideos().contains(video)
                && user.getViewedList().containsKey(video)) {
            user.getFavVideos().add(video);
            video.setNrOfFav(video.getNrOfFav() + 1);
            return SUCCESS + video.getTitle() + ADDED_FAV;
        } else if (user.getFavVideos().contains(video)) {
            return ERROR + video.getTitle() + ALREADY_FAV;
        } else {
            return ERROR + video.getTitle() + NOT_SEEN;
        }
    }

    private String viewCom(final User user, final Video video, final GenreDatabase genreDatabase) {
        video.setViews(video.getViews() + 1);

        video.getGenres().forEach(genreName ->
                genreDatabase.getGenreDatabase().put(Utils.stringToGenre(genreName),
                genreDatabase.getGenreDatabase().get(Utils.stringToGenre(genreName)) + 1));

        if (!user.getViewedList().containsKey(video)) {
            user.getViewedList().put(video, 1);
        } else {
            user.getViewedList().put(video, user.getViewedList().get(video) + 1);
        }

        return SUCCESS + video.getTitle() + WAS_VIEWED + user.getViewedList().get(video);
    }

    private String rateCom(final User user, final Video video) {
        if (!user.getViewedList().containsKey(video)) {
            return ERROR + video.getTitle() + NOT_SEEN;
        } else {
            if (this.seasonNumber == 0) {
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

    private String averageQuery(final ActorDatabase actorDatabase,
                                final VideoDatabase videoDatabase) {
        actorDatabase.getActorDatabase().values().forEach(actor ->
                actor.computeActorGrade(videoDatabase));

        ArrayList<Actor> actors = new ArrayList<>();

        actorDatabase.getActorDatabase().values().forEach(actor -> {
            if (actor.getAverageRating() > 0) {
                actors.add(actor);
            }
        });

        actors.sort((actor1, actor2) -> {
            int compute = actor1.getAverageRating().compareTo(actor2.getAverageRating());

            if (compute != 0) {
                return compute;
            } else {
                return actor1.getName().compareTo(actor2.getName());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(actors);
        }

        while (this.number < actors.size()) {
            actors.remove(this.number);
        }

        ArrayList<String> actorsNames = new ArrayList<>();

        actors.forEach(actor -> actorsNames.add(actor.getName()));

        return QUERY_REZZ + actorsNames;
    }

    private String awardsQuery(final ActorDatabase actorDatabase) {
        ArrayList<String> actorsNames = new ArrayList<>();
        ArrayList<Actor> actors = new ArrayList<>();
        ArrayList<ActorsAwards> awards = new ArrayList<>();

        this.filters.get(AWARD_LIST).forEach(string -> awards.add(Utils.stringToAwards(string)));

        for (Actor actor : actorDatabase.getActorDatabase().values()) {
            if (actor.getAwards().keySet().containsAll(awards)) {
                actors.add(actor);
            }
        }

        actors.sort((actor1, actor2) -> {
            int compare = actor1.getTotalAwards() - actor2.getTotalAwards();

            if (compare != 0) {
                return compare;
            } else {
                return actor1.getName().compareTo(actor2.getName());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(actors);
        }

        actors.forEach(actor -> actorsNames.add(actor.getName()));

        return QUERY_REZZ + actorsNames;
    }

    private String descriptionQuery(final ActorDatabase actorDatabase) {
        ArrayList<String> actors = new ArrayList<>();
        ArrayList<String> filterWords = new ArrayList<>(this.filters.get(2));

        for (Actor actor : actorDatabase.getActorDatabase().values()) {

            ArrayList<String> careerDescription;
            careerDescription = Utils.stringToArray(actor.getCareerDescription());

            if (careerDescription.containsAll(filterWords)) {
                actors.add(actor.getName());
            }
        }

        actors.sort(String::compareTo);

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(actors);
        }

        return QUERY_REZZ + actors;
    }

    private String ratingsQuery(final VideoDatabase videoDatabase) {
        if (MOVIES.equals(this.objectType)) {
            ArrayList<Video> ratedMovies = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.getAvgRating() > 0
                        && Utils.isFiltered(video, this)) {
                    ratedMovies.add(video);
                }
            }

            return getRatedVideos(ratedMovies);
        } else {
            ArrayList<Video> ratedShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getAvgRating() > 0
                        && Utils.isFiltered(video, this)) {
                    ratedShows.add(video);
                }
            }

            return getRatedVideos(ratedShows);
        }
    }

    private String favQuery(final VideoDatabase videoDatabase) {
        if (MOVIES.equals(this.objectType)) {
            ArrayList<Video> favMovies = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (!video.isShow() && video.getNrOfFav() > 0
                        && Utils.isFiltered(video, this)) {
                    favMovies.add(video);
                }
            }

            return getFavVideos(favMovies);
        } else {
            ArrayList<Video> favShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getNrOfFav() > 0
                        && Utils.isFiltered(video, this)) {
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
                if (!video.isShow() && Utils.isFiltered(video, this)) {
                    longestMovies.add(video);
                }
            }

            return getLongVideos(longestMovies);
        } else {
            ArrayList<Video> longestShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && Utils.isFiltered(video, this)) {
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
                        && Utils.isFiltered(video, this)) {
                    mostViewedMovies.add(video);
                }
            }

            return getMostViewedVideos(mostViewedMovies);
        } else {
            ArrayList<Video> mostViewedShows = new ArrayList<>();

            for (Video video : videoDatabase.getVideoDatabase().values()) {
                if (video.isShow() && video.getViews() > 0
                        && Utils.isFiltered(video, this)) {
                    mostViewedShows.add(video);
                }
            }

            return getMostViewedVideos(mostViewedShows);
        }
    }

    private String userQuery(final UserDatabase userDatabase) {
        ArrayList<User> users = new ArrayList<>();

        for (User user : userDatabase.getUserDatabase().values()) {
            if (user.getNrOfRatings() > 0) {
                users.add(user);
            }
        }

        users.sort((user1, user2) -> {
            int compare = user1.getNrOfRatings() - user2.getNrOfRatings();

            if (compare != 0) {
                return compare;
            } else {
                return user1.getUsername().compareTo(user2.getUsername());
            }
        });

        if (this.sortType.equals(DESCENDING)) {
            Collections.reverse(users);
        }

        while (this.number < users.size()) {
            users.remove(this.number);
        }

        return QUERY_REZZ + Utils.usernamesToString(users);
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

        for (Genre genre : allGenres) {
            stringGenres.add(Utils.genreToString(genre));
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
}
