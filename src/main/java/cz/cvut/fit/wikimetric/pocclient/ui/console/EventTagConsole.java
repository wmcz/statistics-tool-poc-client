package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.EventTag;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;

@ShellComponent
public class EventTagConsole {
    private final EventTagClient tagClient;
    private final TagView tagView;
    private Long currentTagId;
    private String currentTagName;

    public EventTagConsole(EventTagClient tagClient, TagView tagView) {
        this.tagClient = tagClient;
        this.tagView = tagView;
        this.currentTagId = null;
        this.currentTagName = null;
    }

    public Availability eventTagDetails() {
        return this.currentTagId == null ?
                Availability.unavailable("není zvolen tag") :
                Availability.available();
    }

    @ShellMethod("Přejít na detaily tagu")
    public void setEventTag(String name) {
        Collection<EventTag> tags = tagClient.findByName(name);
        switch (tags.size()) {
            case 0 -> {
                System.err.println("Tag " + name + " neexistuje.");
                currentTagId = null;
            }
            default -> {
                EventTag tag = tags.iterator().next();
                currentTagId = tag.id;
                currentTagName = tag.name;
            }
        }
    }

    @ShellMethod("Přidat tag události")
    public void addEventTag(String name) {
        EventTag eventTag = tagClient.create(
                new EventTag(
                        name,
                        null,
                        true,
                        null,
                        null,
                        null
                )
        );
        setEventTag(eventTag);
    }

    @ShellMethod("Smazat aktuální tag")
    @ShellMethodAvailability("eventTagDetails")

    private void setEventTag(EventTag tag) {
        currentTagId = tag.id;
        currentTagName = tag.name;

    }

    public Long getCurrentTagId() {
        return currentTagId;
    }

    public String getCurrentTagName() {
        return currentTagName;
    }
}
