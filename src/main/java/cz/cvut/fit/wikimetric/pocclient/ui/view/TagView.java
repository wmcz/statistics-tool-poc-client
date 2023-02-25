package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class TagView {
    private final EventClient eventClient;
    private final UserClient userClient;
    private final UserTagClient userTagClient;
    private final EventTagClient eventTagClient;

    public TagView(EventClient eventClient, UserClient userClient, UserTagClient userTagClient, EventTagClient eventTagClient) {
        this.eventClient = eventClient;
        this.userClient = userClient;
        this.userTagClient = userTagClient;
        this.eventTagClient = eventTagClient;
    }

    private String getEnd(Tag tag) {
        if (tag.childrenIds.size() > 0)
            return ", " + tag.childrenIds.size() + " podtagy)";
        else return ")";
    }

    public void listUsers(Tag tag) {
        System.out.println("\n\tÚčastníci s tagem " + tag.name + " (" + tag.elementIds.size() + "):");
        tag.elementIds.forEach(u -> System.out.println(userClient.readOne(u).username));
    }

    public void listEvents(Tag tag) {
        System.out.println("\n\tUdálosti s tagem " + tag.name + " (" + tag.elementIds.size() + "):");
        tag.elementIds.forEach(e -> System.out.println(eventClient.readOne(e).name));
    }

    public String getEventTagString(Tag tag) {
        String parentBit = "";
        if (tag.parentId != null) {
            parentBit = " [nadtag: " + eventTagClient.readOne(tag.parentId).name + "]";
        }
        return tag.name + parentBit + " (" + tag.elementIds.size() + " událostí" + getEnd(tag);
    }

    public String getUserTagString(Tag tag) {
        String parentBit = "";
        if (tag.parentId != null) {
            parentBit = " [nadtag: " + userTagClient.readOne(tag.parentId).name + "]";
        }
        return tag.name + parentBit + " (" + tag.elementIds.size() + " uživatel" + getEnd(tag);
    }

    public void printEventTag(Tag tag) {
        System.out.println(getEventTagString(tag));
    }

    public void printUserTag(Tag tag) {
        System.out.println(getUserTagString(tag));
    }

    public void printError(Exception e) {
        if (e instanceof WebClientRequestException) {
            System.err.println("Chyba připojení k serveru");
        } else if (e instanceof WebClientResponseException) {
            if (e instanceof WebClientResponseException.NotFound) {
                System.err.println("Tag nebyl nalezen");
            } else if (e instanceof WebClientResponseException.BadRequest) {
                System.err.println("Tag již existuje");
            }
        }
        System.err.println(e.getMessage());
    }
}
