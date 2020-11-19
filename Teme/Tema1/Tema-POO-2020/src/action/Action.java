package action;

import database.ActorDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import entertainment.Video;
import fileio.ActionInputData;
import user.User;

import java.util.ArrayList;
import java.util.List;

import static common.Constants.*;

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

    public Action(ActionInputData actionData) {
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

    public String executeAction(ActorDatabase actorDatabase,
                              UserDatabase userDatabase,
                              VideoDatabase videoDatabase) {
        switch (this.actionType) {
            case (COMMAND):
                User userCom = userDatabase.getUserDatabase().get(this.username);
                Video video = videoDatabase.getVideoDatabase().get(this.title);

                switch (this.type) {
                    case (FAVORITE):
                        if (!userCom.getFavVideos().contains(video) &&
                                userCom.getViewedList().containsKey(video)) {
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

                        return SUCCESS + video.getTitle() + WAS_VIEWED +
                                userCom.getViewedList().get(video);

                    case (RATING):
                        if (!userCom.getViewedList().containsKey(video)) {
                            return ERROR + video.getTitle() + NOT_SEEN;
                        } else {
                            if (this.seasonNumber == 0) {
                                if (userCom.getRatedMovies().contains(this.title)) {
                                    return ERROR + video.getTitle() + ALREADY_RATED;
                                } else {
                                    userCom.rateMovie(this.title, video, this.grade);
                                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                                }
                            } else {
                                if (userCom.getRatedShows().contains(this.title + this.seasonNumber)) {
                                    return ERROR + video.getTitle() + ALREADY_RATED;
                                } else {
                                    userCom.rateShow(this.title, this.seasonNumber, video, this.grade);
                                    return SUCCESS + video.getTitle() + WAS_RATED + this.grade + BY + this.username;
                                }
                            }
                        }
                }
                break;

            case (QUERY):
                //smtsmth
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
                                    if(videoEntry.getAvgRating() > bestUnseenRec.getAvgRating()) {
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
                            return FAV_REC + CANT_APPLY;
                        }

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
                            return FAV_REC + CANT_APPLY;
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
        return "Action{" +
                "actionId=" + actionId +
                ", actionType='" + actionType + '\'' +
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", objectType='" + objectType + '\'' +
                ", sortType='" + sortType + '\'' +
                ", criteria='" + criteria + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", number=" + number +
                ", grade=" + grade +
                ", seasonNumber=" + seasonNumber +
                ", filters=" + filters +
                '}';
    }
}
