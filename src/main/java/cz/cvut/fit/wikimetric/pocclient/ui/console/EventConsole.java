package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.ui.view.EventView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;

@ShellComponent
public class EventConsole {
    private final EventClient eventClient;
    private final EventView eventView;
    private Long currentEventId;
    private String currentEventName;

    public EventConsole(EventClient eventClient, EventView eventView) {
        this.eventClient = eventClient;
        this.eventView = eventView;
        this.currentEventId = null;
        this.currentEventName = null;
    }

    public Availability eventDetails() {
        return this.currentEventId == null ?
                Availability.unavailable("událost není zvolena") :
                Availability.available();
    }

    @ShellMethod("Otevřít detaily události")
    public void setEvent(String name) {
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

    private void setEvent(Event event) {
        currentEventId = event.id;
        currentEventName = event.name;
    }

    @ShellMethod("Zavřít detaily události")
    @ShellMethodAvailability("eventDetails")
    public void unsetEvent() {
        currentEventId = null;
    }

    @ShellMethod("Přidat nového uživatele")
    public void addUser(String name) {
        Event event = eventClient.create(new Event(
                null,
                null,
                name,
                null,
                null,
                null,
                null
        ));
        setEvent(event);
    }

    @ShellMethod("Smazat aktuální událost")
    @ShellMethodAvailability("eventDetails")
    public void deleteUser() {
        eventClient.delete(currentEventId);
        unsetEvent();
    }

    public String getCurrentEventName() {
        return currentEventName;
    }

    public Long getCurrentEventId() {
        return currentEventId;
    }
}
