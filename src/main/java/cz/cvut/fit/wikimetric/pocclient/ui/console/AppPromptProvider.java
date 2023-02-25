package cz.cvut.fit.wikimetric.pocclient.ui.console;

import cz.cvut.fit.wikimetric.pocclient.data.EventClient;
import cz.cvut.fit.wikimetric.pocclient.data.EventTagClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserClient;
import cz.cvut.fit.wikimetric.pocclient.data.UserTagClient;
import cz.cvut.fit.wikimetric.pocclient.ui.view.EventView;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import cz.cvut.fit.wikimetric.pocclient.ui.view.UserView;
import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class AppPromptProvider implements PromptProvider {
    private final UserConsole userConsole;
    private final EventConsole eventConsole;
    private final UserTagConsole userTagConsole;
    private final EventTagConsole eventTagConsole;
    private final UserView userView;
    private final UserClient userClient;
    private final EventView eventView;
    private final EventClient eventClient;
    private final TagView tagView;
    private final UserTagClient userTagClient;
    private final EventTagClient eventTagClient;

    public AppPromptProvider(UserConsole userConsole, EventConsole eventConsole, UserTagConsole userTagConsole, EventTagConsole eventTagConsole, UserView userView, UserClient userClient, EventView eventView, EventClient eventClient, TagView tagView, UserTagClient userTagClient, EventTagClient eventTagClient) {
        this.userConsole = userConsole;
        this.eventConsole = eventConsole;
        this.userTagConsole = userTagConsole;
        this.eventTagConsole = eventTagConsole;
        this.userView = userView;
        this.userClient = userClient;
        this.eventView = eventView;
        this.eventClient = eventClient;
        this.tagView = tagView;
        this.userTagClient = userTagClient;
        this.eventTagClient = eventTagClient;
    }

    @Override
    public AttributedString getPrompt() {
        StringBuilder res = new StringBuilder(
                " ________________________\n" +
                "[\n" +
                "[ Aktuálně zvolené:");

        Long userId = userConsole.getCurrentUserId();
        Long eventId = eventConsole.getCurrentEventId();
        Long userTagId = userTagConsole.getCurrentTagId();
        Long eventTagId = eventTagConsole.getCurrentTagId();

        if (userId == null && eventId == null && userTagId == null && eventTagId == null)
             res.append(" nic\n");
        else res.append('\n');

        if (userId != null) {
            res.append("[   Uživatel:      ")
               .append(userView.getUserString(userClient.readOne(userId)))
               .append('\n');
        }

        if (eventId != null) {
            res.append("[   Událost:       ")
               .append(eventView.getEventString(eventClient.readOne(eventId)))
               .append('\n');
        }

        if (userTagId != null) {
            res.append("[   Tag uživatele: ")
               .append(tagView.getUserTagString(userTagClient.readOne(userTagId)))
               .append('\n');
        }

        if (eventTagId != null) {
            res.append("[   Tag události:  ")
               .append(tagView.getEventTagString(eventTagClient.readOne(eventTagId)))
               .append('\n');
        }

        res.append(
                "[________________________\n" +
                "\n" +
                "--> ");

        return new AttributedString(res);
    }

}
