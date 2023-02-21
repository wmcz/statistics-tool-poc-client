package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@ShellComponent
@ShellCommandGroup("Správa tagů události")
public class EventTagConsole {
    private final EventTagClient tagClient;
    private final TagView tagView;
    private final EventClient eventClient;
    private Long currentTagId;
    private String currentTagName;

    public EventTagConsole(EventTagClient tagClient, TagView tagView, EventClient eventClient) {
        this.tagClient = tagClient;
        this.tagView = tagView;
        this.currentTagId = null;
        this.currentTagName = null;
        this.eventClient = eventClient;
    }

    public Availability eventTagDetails() {
        return this.currentTagId == null ?
                Availability.unavailable("není zvolen tag") :
                Availability.available();
    }

    @ShellMethod(value = "Vypsat tagy událostí", group = "Tagy událostí")
    public void tagEventList() {
        tagClient.readAll().forEach(tagView::printEventTag);
    }

    @ShellMethod(value = "Přejít na detaily tagu", group = "Tagy událostí")
    public void tagEventSet(String name) {
        Collection<Tag> tags = tagClient.findByName(name);
        switch (tags.size()) {
            case 0 -> {
                System.err.println("Tag " + name + " neexistuje.");
                currentTagId = null;
            }
            default -> {
                Tag tag = tags.iterator().next();
                currentTagId = tag.id;
                currentTagName = tag.name;
            }
        }
    }

    private void tagEventSet(Tag tag) {
        currentTagId = tag.id;
        currentTagName = tag.name;
    }

    @ShellMethod("Zavřít detaily tagu")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventUnset() {
        currentTagId = null;
    }

    @ShellMethod(value = "Přidat tag události", group = "Tagy událostí")
    public void tagEventAdd(String[] names) {
        for (String name : names) {
            Tag tag = tagClient.create(new Tag(name));
            if (names.length == 1) tagEventSet(tag);
        }
    }

    @ShellMethod("Smazat aktuální tag")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventDelete() {
        tagClient.delete(currentTagId);
        tagEventUnset();
    }

    @ShellMethod("Vypsat události s aktuálním tagem")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventEventList() {
        Tag tag = tagClient.readOne(currentTagId);
        tagView.listEvents(tag);
    }

    @ShellMethod("Přidat jednu či více událostí k aktuálnímu tagu")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventEventAdd(String[] names) {
        Tag tag = tagClient.readOne(currentTagId);
        for (String name : names) {
            Collection<Event> events = eventClient.findByName(name);

            if (events.isEmpty()) {
                System.out.println("Událost " + name + " neexistuje, chcete přidat? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    tag.elementIds.add(eventClient.create(
                            new Event(name, List.of(tag.id))).id);

            } else tag.elementIds.addAll(events.stream().map(e -> e.id).toList());
        }
        tag = tagClient.update(tag);
        tagView.listEvents(tag);
    }

    @ShellMethod("Odebrat jednu či více událostí z aktuálního tagu")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventEventRemove(String[] names) {
        Tag tag = tagClient.readOne(currentTagId);
        for (String name : names) {
            tag.elementIds.removeIf(e -> eventClient.readOne(e).name.equals(name));
        }
        tag = tagClient.update(tag);
        tagView.listEvents(tag);
    }

    @ShellMethod("Přejmenovat tag události")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventRename(String name) {
        Tag tag = tagClient.readOne(currentTagId);
        tag.name = name;
        tagClient.update(tag);
        tagView.printEventTag(tag);
    }

    @ShellMethod("Odebrat nadtag")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventParentUnset() {
        Tag tag = tagClient.readOne(currentTagId);
        tag.parentId = null;
        tag = tagClient.update(tag);
        tagView.printEventTag(tag);
    }

    @ShellMethod("Nastavit nadtag")
    @ShellMethodAvailability("eventTagDetails")
    public void tagEventParentSet(String name) {
        Tag tag = tagClient.readOne(currentTagId);
        Tag parent = tagClient.findByName(name).iterator().next();
        tag.parentId = parent.id;
        parent.childrenIds.add(tag.id);
        tag = tagClient.update(tag);
        tagClient.update(parent);

        tagView.printEventTag(tag);
    }

    public Long getCurrentTagId() {
        return currentTagId;
    }

    public String getCurrentTagName() {
        return currentTagName;
    }
}
