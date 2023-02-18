package cz.cvut.fit.wikimetric.pocclient.ui.view;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagView {
    private final EventClient eventClient;
    private final UserClient userClient;

    public TagView(EventClient eventClient, UserClient userClient) {
        this.eventClient = eventClient;
        this.userClient = userClient;
    }

    public void listUsers(Tag tag) {
        System.out.println("\n\tÚčastníci s tagem " + tag.name + " (" + tag.elementIds.size() + "):");
        tag.elementIds.forEach(u -> System.out.println(userClient.readOne(u).username));
    }

    public void listEvents(Tag tag) {
        System.out.println("\n\tUdálosti s tagem " + tag.name + " (" + tag.elementIds.size() + "):");
        tag.elementIds.forEach(e -> System.out.println(eventClient.readOne(e).name));
    }

    public void printEventTag(Tag tag) {
        System.out.println("Tag události " + tag.name + " (" + tag.elementIds.size() + " událostí)");
    }

    public void printUserTag(Tag tag) {
        System.out.println("Tag uživatele " + tag.name + " (" + tag.elementIds.size() + " uživatel)");
    }
}
