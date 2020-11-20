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

    public String executeAction(final ActorDatabase actorDatabase, final UserDatabase userDatabase,
                                final VideoDatabase videoDatabase) {
        if (COMMAND.equals(this.actionType)) {
            User user = userDatabase.getUserDatabase().get(this.username);
            Video video = videoDatabase.getVideoDatabase().get(this.title);

            if (FAVORITE.equals(this.type)) {
                if (!user.getFavVideos().contains(video)
                        && user.getViewedList().containsKey(video)) {
                    user.getFavVideos().add(video);
                    video.setNrOfFav(video.getNrOfFav() + 1);
                    return SUCCESS + video.getTitle() + ADDED_FAV;
                } else if (user.getFavVideos().contains(video)) {
                    return ERROR + video.getTitle() + ALREADY_FAV;
                } else if (!user.getViewedList().containsKey(video)) {
                    return ERROR + video.getTitle() + NOT_SEEN;
                }
            } else if (VIEW.equals(this.type)) {
                video.setViews(video.getViews() + 1);

                if (!user.getViewedList().containsKey(video)) {
                    user.getViewedList().put(video, 1);
                } else {
                    user.getViewedList().put(video, user.getViewedList().get(video) + 1);
                }

                return SUCCESS + video.getTitle() + WAS_VIEWED + user.getViewedList().get(video);
            } else if (RATING.equals(this.type)) {
                if (!user.getViewedList().containsKey(video)) {
                    return ERROR + video.getTitle() + NOT_SEEN;
                } else {
                    if (this.seasonNumber == 0) { // then it's a movie
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
        } else if (QUERY.equals(this.actionType)) {
            if (USERS.equals(this.objectType)) {
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

                while (this.number < users.size()) {
                    users.remove(this.number);
                }

                if (this.sortType.equals(DESCENDING)) {
                    Collections.reverse(users);
                }

                return QUERY_REZZ + Utils.usernamesToString(users);
            }
        } else if (RECOMMENDATION.equals(this.actionType)) {
            User user = userDatabase.getUserDatabase().get(this.username);

            if (STANDARD.equals(this.type)) {
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
            } else if (BEST_UNSEEN.equals(this.type)) {
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
            } else if (POPULAR.equals(this.type)) {
                if (!user.getSubscription()) {
                    return POPULAR_REC + CANT_APPLY;
                }

                //TODO
            } else if (FAVORITE.equals(this.type)) {
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
            } else if (SEARCH.equals(this.type)) {
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
