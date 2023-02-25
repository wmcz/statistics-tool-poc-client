package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;
import java.util.Iterator;

@Component
public class UserView {
    private final UserTagClient userTagClient;
    private final EventClient eventClient;

    public UserView(UserTagClient userTagClient, EventClient eventClient) {
        this.userTagClient = userTagClient;
        this.eventClient = eventClient;
    }

    private String printTags(User user) {
        if (user.tagIds.size() == 0)
            return "";
        else if (user.tagIds.size() > 4) {
            return " [" + user.tagIds.size() + " tagů]";
        }
        else {
            StringBuilder res = new StringBuilder();
            res.append(" [Tagy: ");

            Iterator<Tag> tagIterator = user.tagIds.stream().map(userTagClient::readOne).iterator();
            for (int i = 0; i < user.tagIds.size(); i++) {
                if (i != 0) res.append(", ");
                res.append("\"")
                        .append(tagIterator.next().name)
                        .append("\"");
            }
            res.append("]");
            return res.toString();
        }
    }

    public void printAll(Collection<User> users) {
        users.forEach(this::printUser);
    }


    public String getUserString(User user) {
        return user.username + " (" + user.eventIds.size() + " událostí)" + printTags(user);
    }

    public void printUser(User user) {
        System.out.println(getUserString(user));
    }

    public void printError(Throwable e) {
        if (e instanceof WebClientRequestException) {
            System.err.println("Chyba připojení k serveru");
        } else if (e instanceof WebClientResponseException) {
            if (e instanceof WebClientResponseException.NotFound) {
                System.err.println("Uživatel nebyl nalezen");
            } else if (e instanceof WebClientResponseException.BadRequest) {
                System.err.println("Uživatel již existuje");
            }
        }
        System.err.println(e.getMessage());
    }

    public void listTags(User user) {
        System.out.println("\n\tTagy uživatele " + user.username + " (" + user.tagIds.size() + "):");
        user.tagIds.forEach(t -> System.out.println(userTagClient.readOne(t).name));
    }

    public void listEvents(User user) {
        System.out.println("\n\tUdálosti uživatele " + user.username + " (" + user.eventIds.size() + "):");
        user.eventIds.forEach(e -> System.out.println(eventClient.readOne(e).name));

    }
}
