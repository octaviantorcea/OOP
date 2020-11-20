package action;

import database.ActorDatabase;
import database.UserDatabase;
import database.VideoDatabase;
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
import static common.Constants.BEST_UNSEEN;
import static common.Constants.BEST_UNSEEN_REC;
import static common.Constants.BY;
import static common.Constants.CANT_APPLY;
import static common.Constants.COMMAND;
import static common.Constants.DESCENDING;
import static common.Constants.ERROR;
import static common.Constants.FAVORITE;
import static common.Constants.FAV_REC;
import static common.Constants.NOT_SEEN;
import static common.Constants.POPULAR;
import static common.Constants.POPULAR_REC;
import static common.Constants.QUERY;
import static common.Constants.QUERY_REZZ;
import static common.Constants.RATING;
import static common.Constants.RECOMMENDATION;
import static common.Constants.REZZ;
import static common.Constants.SEARCH;
import static common.Constants.SEARCH_REC;
import static common.Constants.STANDARD;
import static common.Constants.STANDARD_REC;
import static common.Constants.SUCCESS;
import static common.Constants.USERS;
import static common.Constants.VIEW;
import static common.Constants.WAS_RATED;
import static common.Constants.WAS_VIEWED;

public class Action {
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
                                final VideoDatabase videoDatabase) {
        switch (this.actionType) {
            case (COMMAND):
                User userCom = userDatabase.getUserDatabase().get(this.username);
                Video video = videoDatabase.getVideoDatabase().get(this.title);

                switch (this.type) {
                    case (FAVORITE):
                        if (!userCom.getFavVideos().contains(video)
                                && userCom.getViewedList().containsKey(video)) {
                            userCom.getFavVideos().add(video);
                            video.setNrOfFav(video.getNrOfFav() + 1);
                            return SUCCESS + video.getTitle() + ADDED_FAV;
                        } else if (userCom.getFavVideos().contains(video)) {
                            return ERROR + video.getTitle() + ALREADY_FAV;
                        } else if (!userCom.getViewedList().containsKey(video)) {
                            return ERROR + video.getTitle() + NOT_SEEN;
                        }

                    case (VIEW):
                        video.setViews(video.getViews() + 1);

                        if (!userCom.getViewedList().containsKey(video)) {
                            userCom.getViewedList().put(video, 1);
                        } else {
                            userCom.getViewedList().put(video, userCom.getViewedList().get(video) + 1);
                        }

                        return SUCCESS + video.getTitle() + WAS_VIEWED
                                + userCom.getViewedList().get(video);

                    case (RATING):
                        if (!userCom.getViewedList().containsKey(video)) {
                            return ERROR + video.getTitle() + NOT_SEEN;
                        } else {
                            if (this.seasonNumber == 0) {
                                if (userCom.getRatedMovies().contains(this.title)) {
                                    return ERROR + video.getTitle() + ALREADY_RATED;
                                } else {
                                    userCom.rateMovie(video, this.grade);
                                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                                }
                            } else {
                                if (userCom.getRatedShows().contains(this.title + this.seasonNumber)) {
                                    return ERROR + video.getTitle() + ALREADY_RATED;
                                } else {
                                    userCom.rateShow(this.seasonNumber, video, this.grade);
                                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                                }
                            }
                        }
                }
                break;

            case (QUERY):
                switch (this.objectType) {
                    case (USERS):
                        ArrayList<User> users = new ArrayList<>();

                        for (User user : userDatabase.getUserDatabase().values()) {
                            if (user.getNrOfRatings() > 0) {
                                users.add(user);
                            }
                        }

                        users.sort(Comparator.comparingInt(User::getNrOfRatings));

                        while (this.number < users.size()) {
                            users.remove(this.number);
                        }

                        if (this.criteria.equals(DESCENDING)) {
                            Collections.reverse(users);
                        }

                        return QUERY_REZZ + Utils.usernamesToString(users);
                }
                break;

            case (RECOMMENDATION):
                User userRec = userDatabase.getUserDatabase().get(this.username);

                switch (this.type) {
                    case (STANDARD):
                        String standardRec = "";

                        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
                            if (!userRec.getViewedList().containsKey(videoEntry)) {
                                standardRec = videoEntry.getTitle();
                                break;
                            }
                        }

                        if (standardRec.isBlank()) {
                            return STANDARD_REC + CANT_APPLY;
                        } else {
                            return STANDARD_REC + REZZ + standardRec;
                        }

                    case (BEST_UNSEEN):
                        Video bestUnseenRec = null;

                        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
                            if (!userRec.getViewedList().containsKey(videoEntry)) {
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

                    case (POPULAR):
                        if (!userRec.getSubscription()) {
                            return POPULAR_REC + CANT_APPLY;
                        }

                        //TODO

                    case (FAVORITE):
                        if (!userRec.getSubscription()) {
                            return FAV_REC + CANT_APPLY;
                        }

                        Video bestFav = null;

                        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
                            if (!userRec.getViewedList().containsKey(videoEntry)) {
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

                    case (SEARCH):
                        if (!userRec.getSubscription()) {
                            return SEARCH_REC + CANT_APPLY;
                        }

                        ArrayList<String> searchRec = new ArrayList<>();

                        for (Video videoEntry : videoDatabase.getVideoDatabase().values()) {
                            if (!userRec.getViewedList().containsKey(videoEntry)
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
                break;
        }

        return "";
    }

    public int getActionId() {
        return actionId;
    }

    // for debugging
    @Override
    public String toString() {
        return "Action{"
                + "actionId=" + actionId
                + ", actionType='" + actionType + '\''
                + ", type='" + type + '\''
                + ", username='" + username + '\''
                + ", objectType='" + objectType + '\''
                + ", sortType='" + sortType + '\''
                + ", criteria='" + criteria + '\''
                + ", title='" + title + '\''
                + ", genre='" + genre + '\''
                + ", number=" + number
                + ", grade=" + grade
                + ", seasonNumber=" + seasonNumber
                + ", filters=" + filters
                + '}';
    }
}
