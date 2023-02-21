package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;

@Component
public class UserView {
    private final UserTagClient userTagClient;
    private final EventClient eventClient;

    public UserView(UserTagClient userTagClient, EventClient eventClient) {
        this.userTagClient = userTagClient;
        this.eventClient = eventClient;
    }

    public void printAll(Collection<User> users) {
        users.forEach(this::printUser);
    }

    public void printUser(User user) {
        System.out.println(user.username);
    }

    public void printError(Throwable e) {
        if (e instanceof WebClientRequestException) {
            System.err.println("Chyba připojení k serveru");
            return;
        } else if (e instanceof WebClientResponseException) {
            if (e instanceof WebClientResponseException.NotFound) {
                System.err.println("Uživatel nebyl nalezen");
                return;
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
