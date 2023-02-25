package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;
import java.util.Iterator;

@Component
public class EventView {

    private final UserClient userClient;
    private final UserTagClient userTagClient;
    private final EventTagClient eventTagClient;
    private final UserView userView;
    private final TagView tagView;

    public EventView(UserClient userClient, UserTagClient userTagClient, EventTagClient eventTagClient, UserView userView, TagView tagView) {
        this.userClient = userClient;
        this.userTagClient = userTagClient;
        this.eventTagClient = eventTagClient;
        this.userView = userView;
        this.tagView = tagView;
    }

    private String printTags(Event event) {
        if (event.tagIds.size() == 0)
            return "";
        else if (event.tagIds.size() > 4) {
            return " [" + event.tagIds.size() + " tagů]";
        }
        else {
            StringBuilder res = new StringBuilder();
            res.append(" [Tagy: ");

            Iterator<Tag> tagIterator = event.tagIds.stream().map(eventTagClient::readOne).iterator();
            for (int i = 0; i < event.tagIds.size(); i++) {
                if (i != 0) res.append(", ");
                res.append("\"")
                   .append(tagIterator.next().name)
                   .append("\"");
            }
            res.append("]");
            return res.toString();
        }
    }

    public void printAll(Collection<Event> events) {
        events.forEach(this::printEvent);
    }

    public String getEventString(Event event) {
        return event.name + " (" + event.userIds.size() + " účastníků)" + printTags(event);
    }

    public void printEvent(Event event) {
        System.out.println(getEventString(event));
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
