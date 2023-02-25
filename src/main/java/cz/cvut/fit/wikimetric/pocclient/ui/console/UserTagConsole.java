package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import cz.cvut.fit.wikimetric.pocclient.ui.view.UserView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

@ShellComponent
@ShellCommandGroup("Správa tagů uživatele")
public class UserTagConsole {
    private final UserTagClient tagClient;
    private final UserClient userClient;
    private final TagView tagView;
    private final UserView userView;
    private Long currentTagId;
    private String currentTagName;

    public UserTagConsole(UserTagClient tagClient, UserClient userClient, TagView tagView, UserView userView) {
        this.tagClient = tagClient;
        this.userClient = userClient;
        this.tagView = tagView;
        this.currentTagId = null;
        this.currentTagName = null;
        this.userView = userView;
    }

    public Availability userTagDetails() {
        return this.currentTagId == null ?
                Availability.unavailable("není zvolen tag") :
                Availability.available();
    }

    @ShellMethod(value = "Vypsat tagy uživatel", group = "Tagy uživatel")
    public void tagUserList() {
        Collection<Tag> tags = tagClient.readAll();
        System.out.println("Nalezeno " + tags.size() + " tagů uživatel:");
        tags.forEach(tagView::printUserTag);
    }

    @ShellMethod(value = "Přejít na detaily tagu", group = "Tagy uživatel")
    public void tagUserSet(String name) {
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

    private void tagUserSet(Tag tag) {
        currentTagId = tag.id;
        currentTagName = tag.name;

    }

    @ShellMethod("Zavřít detaily tagu")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserUnset() {
        currentTagId = null;
    }

    @ShellMethod(value = "Přidat tag uživatele", group = "Tagy uživatel")
    public void tagUserAdd(String[] names) {
        for (String name : names) {
            try {
                Tag tag = tagClient.create(new Tag(name));
                tagView.printUserTag(tag);
                if (names.length == 1) tagUserSet(tag);
            } catch (Exception e) {
                tagView.printError(e);
            }
        }
    }

    @ShellMethod("Smazat aktuální tag")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserDelete() {
        tagClient.delete(currentTagId);
        tagUserUnset();
    }

    @ShellMethod("Vypsat uživatele s aktuálním tagem či jeho podtagem")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserUserList() {
        Collection<User> users = tagClient.getUsers(currentTagId);
        System.out.println("Nalezeno " + users.size() + " uživatel:");
        users.forEach(userView::printUser);
    }

    @ShellMethod("Přidat jednoho či více uživatel k aktuálnímu tagu")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserUserAdd(String[] names) {
        Collection<Long> userIds = new HashSet<>(names.length);
        for (String username : names) {
            Collection<Long> newIds = userClient.findByUsername(username).stream().map(u -> u.id).toList();

            if (newIds.isEmpty()) {
                System.out.println("Uživatel " + username + " neexistuje, chcete vytvořit? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    userIds.add(userClient.create(new User(username)).id);
            }
            else userIds.addAll(newIds);
        }
        tagClient.addUsers(currentTagId, userIds);
        tagView.listUsers(tagClient.readOne(currentTagId));
    }

    @ShellMethod("Odebrat jednoho či více uživatel z aktuálního tagu")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserUserRemove(String[] names) {
        Collection<Long> userIds = new HashSet<>(names.length);
        for (String name : names) {
            userIds.addAll(userClient.findByUsername(name).stream().map(e -> e.id).toList());
        }
        tagClient.removeUsers(currentTagId, userIds);
        tagView.listUsers(tagClient.readOne(currentTagId));
    }

    @ShellMethod("Přejmenovat tag uživatele")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserRename(String name) {
        Tag tag = tagClient.readOne(currentTagId);
        tag.name = name;
        tagClient.update(tag);
        tagView.printUserTag(tag);
    }

    @ShellMethod("Odebrat nadtag")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserParentUnset() {
        Tag tag = tagClient.readOne(currentTagId);
        tag.parentId = null;
        tag = tagClient.update(tag);
        tagView.printEventTag(tag);
    }

    @ShellMethod("Nastavit nadtag")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserParentSet(String name) {
        Tag tag = tagClient.readOne(currentTagId);
        Tag parent = tagClient.findByName(name).iterator().next();
        tag.parentId = parent.id;
        parent.childrenIds.add(tag.id);
        tag = tagClient.update(tag);
        tagClient.update(parent);

        tagView.printUserTag(tag);
    }

    public Long getCurrentTagId() {
        return currentTagId;
    }

    public String getCurrentTagName() {
        return currentTagName;
    }
}
