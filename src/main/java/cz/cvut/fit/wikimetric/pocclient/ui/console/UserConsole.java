package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.UserView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;

@ShellComponent
public class UserConsole {

    private final UserClient userClient;
    private final UserView userView;
    private Long currentUserId;
    private String currentUsername;

    public UserConsole(UserClient userClient, UserView userView) {
        this.userClient = userClient;
        this.userView = userView;
        this.currentUserId = null;
        this.currentUsername = null;
    }

    public Availability userDetails() {
        return currentUserId == null ?
                Availability.unavailable("uživatel není zvolen") :
                Availability.available();
    }

    @ShellMethod("Přejít na detaily uživatele")
    public void setUser(String username) {
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

    private void setUser(User user) {
        currentUserId = user.id;
        currentUsername = user.username;

    }

    @ShellMethod("Zavřít detaily uživatele")
    @ShellMethodAvailability("userDetails")
    public void unsetUser() {
        currentUserId = null;
    }

    @ShellMethod("Přidat nového uživatele")
    public void addUser(String username) {
        User user = userClient.create(new User(
                null,
                username,
                null,
                null,
                null,
                null
        ));
        setUser(user);
    }

    @ShellMethod("Smazat aktuálního uživatele")
    @ShellMethodAvailability("userDetails")
    public void deleteUser() {
        userClient.delete(currentUserId);
        unsetUser();
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
    public Long getCurrentUserId() {
        return currentUserId;
    }
}
