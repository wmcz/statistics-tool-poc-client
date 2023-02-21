package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.EventView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;
import java.util.Scanner;

@ShellComponent
@ShellCommandGroup("Správa události")
public class EventConsole {
    private final EventClient eventClient;
    private final UserClient userClient;
    private final EventTagClient tagClient;
    private final EventView eventView;
    private Long currentEventId;
    private String currentEventName;

    public EventConsole(EventClient eventClient, UserClient userClient, EventTagClient tagClient, EventView eventView) {
        this.eventClient = eventClient;
        this.userClient = userClient;
        this.tagClient = tagClient;
        this.eventView = eventView;
        this.currentEventId = null;
        this.currentEventName = null;
    }

    public Availability eventDetails() {
        return this.currentEventId == null ?
                Availability.unavailable("událost není zvolena") :
                Availability.available();
    }

    @ShellMethod(value = "Vypsat všechny události", group = "Události")
    public void eventList() {
        Collection<Event> events = eventClient.readAll();
        System.out.println("Nalezeno " + events.size() + " událostí:");
        events.forEach(eventView::printEvent);
    }

    @ShellMethod(value = "Otevřít detaily události", group = "Události")
    public void eventSet(String name) {
        Collection<Event> events = eventClient.findByName(name);
        switch (events.size()) {
            case 0 -> {
                System.err.println("Událost " + name + " neexistuje.");
                currentEventId = null;
            }
            default -> {
                Event event = events.iterator().next();
                currentEventId = event.id;
                currentEventName = event.name;
            }
        }
    }

    private void eventSet(Event event) {
        currentEventId = event.id;
        currentEventName = event.name;
    }

    @ShellMethod("Zavřít detaily události")
    @ShellMethodAvailability("eventDetails")
    public void eventUnset() {
        currentEventId = null;
    }

    @ShellMethod(value = "Přidat novou událost", group = "Události")
    public void eventAdd(String[] names) {
        for (String name : names) {
            Event event = eventClient.create(new Event(name));
            eventView.printEvent(event);
            if (names.length == 1)
                eventSet(event);
        }
    }

    @ShellMethod("Smazat aktuální událost")
    @ShellMethodAvailability("eventDetails")
    public void eventDelete() {
        eventClient.delete(currentEventId);
        eventUnset();
    }

    @ShellMethod("Vypsat účastníky")
    @ShellMethodAvailability("eventDetails")
    public void eventUserList() {
        eventView.listUsers(eventClient.readOne(currentEventId));
    }

    @ShellMethod("Vypsat tagy")
    @ShellMethodAvailability("eventDetails")
    public void eventTagList() {
        eventView.listTags(eventClient.readOne(currentEventId));
    }

    @ShellMethod("Přidat jednoho či více uživatel k události")
    @ShellMethodAvailability("eventDetails")
    public void eventUserAdd(String[] names) {
        Event event = eventClient.readOne(currentEventId);
        for (String username : names) {
            Collection<User> users = userClient.findByUsername(username);

            if (users.isEmpty()) {
                System.out.println("Uživatel " + username + " neexistuje, chcete vytvořit? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    event.userIds.add(
                            userClient.create(new User(username)).id);
            }
            else event.userIds.addAll(users.stream().map(u -> u.id).toList());
        }
        event = eventClient.update(event);
        eventView.listUsers(event);
    }

    @ShellMethod("Odebrat jednoho či více uživatel z události")
    @ShellMethodAvailability("eventDetails")
    public void eventUserRemove(String[] names) {
        Event event = eventClient.readOne(currentEventId);
        for (String name : names) {
           event.userIds.removeIf(u -> userClient.readOne(u).username.equals(name));
        }
        event = eventClient.update(event);
        eventView.listUsers(event);
    }

    @ShellMethod("Přidat jeden nebo více tagů k události")
    @ShellMethodAvailability("eventDetails")
    public void eventTagAdd(String[] names) {
        Event event = eventClient.readOne(currentEventId);
        for (String name : names) {
            Collection<Tag> tags = tagClient.findByName(name);
            if (tags.isEmpty()) {
                System.out.println("Tag " + name + " neexistuje, chcete přidat? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    event.tagIds.add(tagClient.create(new Tag(name)).id);
            } else event.tagIds.addAll(tags.stream().map(t -> t.id).toList());
        }
        event = eventClient.update(event);
        eventView.listTags(event);
    }

    @ShellMethod("Odebrat jeden či více tagů z události")
    @ShellMethodAvailability("eventDetails")
    public void eventTagRemove(String[] names) {
        Event event = eventClient.readOne(currentEventId);
        for (String name : names) {
            event.tagIds.removeIf(t -> tagClient.readOne(t).name.equals(name));
        }
        event = eventClient.update(event);
        eventView.listTags(event);
    }

    @ShellMethod("Přejmenovat aktuální událost")
    @ShellMethodAvailability("eventDetails")
    public void eventRename(String name) {
        Event event = eventClient.readOne(currentEventId);
        event.name = name;
        event = eventClient.update(event);
        eventView.printEvent(event);
        currentEventName = event.name;
    }

    public String getCurrentEventName() {
        return currentEventName;
    }

    public Long getCurrentEventId() {
        return currentEventId;
    }
}
