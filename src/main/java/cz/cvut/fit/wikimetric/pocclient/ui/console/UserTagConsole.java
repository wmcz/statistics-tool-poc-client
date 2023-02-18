package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;
import java.util.Scanner;

@ShellComponent
@ShellCommandGroup("Správa tagů uživatele")
public class UserTagConsole {
    private final UserTagClient tagClient;
    private final UserClient userClient;
    private final TagView tagView;
    private Long currentTagId;
    private String currentTagName;

    public UserTagConsole(UserTagClient tagClient, UserClient userClient, TagView tagView) {
        this.tagClient = tagClient;
        this.userClient = userClient;
        this.tagView = tagView;
        this.currentTagId = null;
        this.currentTagName = null;
    }

    public Availability userTagDetails() {
        return this.currentTagId == null ?
                Availability.unavailable("není zvolen tag") :
                Availability.available();
    }

    @ShellMethod(value = "Vypsat tagy uživatel", group = "Tagy uživatel")
    public void tagsUserList() {
        tagClient.readAll().forEach(tagView::printEventTag);
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
    public void tagUserAdd(String name) {
        tagUserSet(tagClient.create(new Tag(name)));
    }

    @ShellMethod("Smazat aktuální tag")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserDelete() {
        tagClient.delete(currentTagId);
        tagUserUnset();
    }

    @ShellMethod("Vypsat uživatele s aktuálním tagem")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserList() {
        Tag tag = tagClient.readOne(currentTagId);
        tagView.listUsers(tag);
    }

    @ShellMethod("Přidat jednoho či více uživatel k aktuálnímu tagu")
    @ShellMethodAvailability("userTagDetails")
    public void tagUserAssign(String[] usernames) {
        Tag tag = tagClient.readOne(currentTagId);
        for (String username : usernames) {
            Collection<Long> userIds = userClient.findByUsername(username).stream().map(u -> u.id).toList();

            if (userIds.isEmpty()) {
                System.out.println("Uživatel " + username + " neexistuje, chcete vytvořit? (ano/ne)");
                String response = new Scanner(System.in).next();

                if (response.contains("ano"))
                    tag.elementIds.add(
                            userClient.create(new User(username)).id);
            }
            else tag.elementIds.addAll(userIds);
        }
        tag = tagClient.update(tag);
        tagView.listUsers(tag);
    }

    @ShellMethod("Odebrat jednoho či více uživatel z aktuálního tagu")
    @ShellMethodAvailability("userTagDetails")
    public void removeUser(String[] usernames) {
        Tag tag = tagClient.readOne(currentTagId);
        for (String username : usernames) {
            tag.elementIds.removeIf(u -> userClient.readOne(u).username.equals(username));
        }
        tag = tagClient.update(tag);
        tagView.listUsers(tag);
    }

    @ShellMethod("Přejmenovat tag uživatele")
    @ShellMethodAvailability("eventTagDetails")
    public void tagUserRename(String name) {
        Tag tag = tagClient.readOne(currentTagId);
        tag.name = name;
        tagClient.update(tag);
        tagView.printUserTag(tag);
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
