package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;

@Component
public class EventView {

    private final UserClient userClient;
    private final UserTagClient userTagClient;
    private final EventTagClient eventTagClient;

    public EventView(UserClient userClient, UserTagClient userTagClient, EventTagClient eventTagClient) {
        this.userClient = userClient;
        this.userTagClient = userTagClient;
        this.eventTagClient = eventTagClient;
    }

    public void printAll(Collection<Event> events) {
        events.forEach(this::printEvent);
    }

    public void printEvent(Event event) {
        System.out.println(event.name);
    }

    public void printError(Throwable e) {
        if (e instanceof WebClientRequestException) {
            System.err.println("Chyba připojení k serveru");
            return;
        } else if (e instanceof WebClientResponseException) {
            if (e instanceof WebClientResponseException.NotFound) {
                System.err.println("Událost nenalezena");
                return;
            } else if (e instanceof WebClientResponseException.BadRequest) {
                System.err.println("Událost již existuje");
            }
        }
        System.err.println(e.getMessage());

    }

    public void listUsers(Event event) {
        System.out.println("\n\tÚčastníci události " + event.name + " (" + event.userIds.size() + "):");
        event.userIds.forEach(u -> System.out.println(userClient.readOne(u).username));
    }

    public void listTags(Event event) {
        System.out.println("\n\tTagy události " + event.name + " (" + event.tagIds.size() + "):");
        event.tagIds.forEach(t -> System.out.println(eventTagClient.readOne(t).name));
    }
}
