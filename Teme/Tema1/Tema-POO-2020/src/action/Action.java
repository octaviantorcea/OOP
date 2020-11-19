package action;

import database.ActorDatabase;
import database.UserDatabase;
import database.VideoDatabase;
import entertainment.Video;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import user.User;

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
                User user = userDatabase.getUserDatabase().get(this.username);
                Video video = videoDatabase.getVideoDatabase().get(this.title);

                switch (this.type) {
                    case (FAVORITE):
                        if (!user.getFavVideos().contains(video) &&
                                user.getViewedList().containsKey(video)) {
                            user.getFavVideos().add(video);
                            video.setNrOfFav(video.getNrOfFav() + 1);
                            return SUCCESS + video.getTitle() + ADDED_FAV;
                        } else if (user.getFavVideos().contains(video)) {
                            return ERROR + video.getTitle() + ALREADY_FAV;
                        } else if (!user.getViewedList().containsKey(video)) {
                            return ERROR + video.getTitle() + NOT_SEEN;
                        }
                        break;

                    case (VIEW):
                        video.setViews(video.getViews() + 1);

                        if (!user.getViewedList().containsKey(video)) {
                            user.getViewedList().put(video, 1);
                        } else {
                            user.getViewedList().put(video, user.getViewedList().get(video) + 1);
                        }

                        return SUCCESS + video.getTitle() + WAS_VIEWED +
                                user.getViewedList().get(video);
                }
                break;

            case (QUERY):
                //smtsmth
                break;

            case (RECOMMENDATION):
                //jdsfghdfj
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
