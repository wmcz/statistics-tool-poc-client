package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.model.UserTag;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Collection;

@ShellComponent
public class UserTagConsole {
    private final UserTagClient tagClient;
    private final TagView tagView;
    private Long currentTagId;
    private String currentTagName;

    public UserTagConsole(UserTagClient tagClient, TagView tagView) {
        this.tagClient = tagClient;
        this.tagView = tagView;
        this.currentTagId = null;
        this.currentTagName = null;
    }

    public Availability userTagDetails() {
        return this.currentTagId == null ?
                Availability.unavailable("není zvolen tag") :
                Availability.available();
    }

    @ShellMethod("Přejít na detaily tagu")
    public void setUserTag(String name) {
        Collection<UserTag> tags = tagClient.findByName(name);
        switch (tags.size()) {
            case 0 -> {
                System.err.println("Tag " + name + " neexistuje.");
                currentTagId = null;
            }
            default -> {
                UserTag tag = tags.iterator().next();
                currentTagId = tag.id;
                currentTagName = tag.name;
            }
        }
    }

    @ShellMethod("Přidat tag uživatele")
    public void addUserTag(String name) {
        UserTag userTag = tagClient.create(
                new UserTag(
                        name,
                        null,
                        true,
                        null,
                        null,
                        null
                )
        );
        setUserTag(userTag);
    }

    @ShellMethod("Smazat aktuální tag")
    @ShellMethodAvailability("userTagDetails")

    private void setUserTag(UserTag tag) {
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
