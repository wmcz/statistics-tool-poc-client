package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.EventView;
import cz.cvut.fit.wikimetric.pocclient.ui.view.UserView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

@ShellComponent
@ShellCommandGroup("Správa uživatele")
public class UserConsole {

    private final UserClient userClient;
    private final UserTagClient tagClient;
    private final UserView userView;
    private final EventClient eventClient;
    private Long currentUserId;
    private String currentUsername;

    public UserConsole(UserClient userClient, UserTagClient tagClient, UserView userView, EventClient eventClient, EventView eventView) {
        this.userClient = userClient;
        this.tagClient = tagClient;
        this.userView = userView;
        this.currentUserId = null;
        this.currentUsername = null;
        this.eventClient = eventClient;
    }

    public Availability userDetails() {
        return currentUserId == null ?
                Availability.unavailable("uživatel není zvolen") :
                Availability.available();
    }

    @ShellMethod(value = "Vypsat uživatele", group = "Uživatelé")
    public void userList() {
        Collection<User> users = userClient.readAll();
        System.out.println("Nalezeno " + users.size() + " uživatel:");
        users.forEach(userView::printUser);
    }

    @ShellMethod(value = "Přejít na detaily uživatele", group = "Uživatelé")
    public void userSet(String username) {
        Collection<User> users = userClient.findByUsername(username);
        switch (users.size()) {
            case 0 -> {
                System.err.println("Uživatel " + username + " neexistuje.");
                currentUserId = null;
            }
            default -> {
                User user = users.iterator().next();
                currentUserId = user.id;
                currentUsername = user.username;
            }
        }
    }

    private void userSet(User user) {
        currentUserId = user.id;
        currentUsername = user.username;

    }

    @ShellMethod("Zavřít detaily uživatele")
    @ShellMethodAvailability("userDetails")
    public void userUnset() {
        currentUserId = null;
    }

    @ShellMethod(value = "Přidat nového uživatele", group = "Uživatelé")
    public void userAdd(String[] names) {
        for (String username : names) {
            User user = userClient.create(new User(username));
            userView.printUser(user);
            if (names.length == 1)
                userSet(user);
        }
    }

    @ShellMethod("Smazat aktuálního uživatele")
    @ShellMethodAvailability("userDetails")
    public void userDelete() {
        userClient.delete(currentUserId);
        userUnset();
    }

    @ShellMethod("Vypsat události")
    @ShellMethodAvailability("userDetails")
    public void userEventList() {
        userView.listEvents(userClient.readOne(currentUserId));
    }

    @ShellMethod("Vypsat tagy uživatele")
    @ShellMethodAvailability("userDetails")
    public void userTagList() {
        userView.listTags(userClient.readOne(currentUserId));
    }

    @ShellMethod("Přidat jeden či více tagů k uživateli")
    @ShellMethodAvailability("userDetails")
    public void userTagAdd(String[] names) {
        User user = userClient.readOne(currentUserId);
        for (String name : names) {
            Collection<Long> tagIds = tagClient.findByName(name).stream().map(t -> t.id).toList();
            if (tagIds.isEmpty()) {
                System.out.println("Tag " + name + " neexistuje, chcete přidat? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    user.tagIds.add(tagClient.create(new Tag(name)).id);
            } else user.tagIds.addAll(tagIds);
        }
        user = userClient.update(user);
        userView.listTags(user);
    }

    @ShellMethod("Odebrat jeden či více tagů od uživatele")
    @ShellMethodAvailability("userDetails")
    public void userTagRemove(String[] names) {
        User user = userClient.readOne(currentUserId);
        for (String name : names) {
            user.tagIds.removeIf(t -> tagClient.readOne(t).name.equals(name));
        }
        user = userClient.update(user);
        userView.listTags(user);
    }

    @ShellMethod("Přiřadit uživatele k jedné či více událostí")
    @ShellMethodAvailability("userDetails")
    public void userEventAdd(String[] names) {
        Collection<Long> eventIds = new HashSet<>(names.length);
        for (String name : names) {
            Collection<Long> newIds = eventClient.findByName(name).stream().map(e -> e.id).toList();
            if (newIds.isEmpty()) {
                System.out.println("Událost " + name + " neexistuje, chcete přidat? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    eventIds.add(eventClient.create(new Event(name)).id);
            } else eventIds.addAll(newIds);
        }
        userClient.addEvents(currentUserId, eventIds);
        userView.listEvents(userClient.readOne(currentUserId));
    }

    @ShellMethod("Odebrat uživatele z událostí")
    @ShellMethodAvailability("userDetails")
    public void userEventRemove(String[] names) {
        Collection<Long> eventIds = new HashSet<>(names.length);
        for (String name : names) {
            eventIds.addAll(eventClient.findByName(name).stream().map(e -> e.id).toList());
        }
        userClient.removeEvents(currentUserId, eventIds);
        userView.listEvents(userClient.readOne(currentUserId));
    }

    @ShellMethod("Přejmenovat uživatele")
    @ShellMethodAvailability("userDetails")
    public void userRename(String name) {
        User user = userClient.readOne(currentUserId);
        user.username = name;
        user = userClient.update(user);
        userView.printUser(user);
        currentUsername = user.username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
    public Long getCurrentUserId() {
        return currentUserId;
    }
}
