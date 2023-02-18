package cz.cvut.fit.wikimetric.pocclient.ui.console;

import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class AppPromptProvider implements PromptProvider {
    private final UserConsole userConsole;
    private final EventConsole eventConsole;
    private final UserTagConsole userTagConsole;
    private final EventTagConsole eventTagConsole;

    public AppPromptProvider(UserConsole userConsole, EventConsole eventConsole, UserTagConsole userTagConsole, EventTagConsole eventTagConsole) {
        this.userConsole = userConsole;
        this.eventConsole = eventConsole;
        this.userTagConsole = userTagConsole;
        this.eventTagConsole = eventTagConsole;
    }

    @Override
    public AttributedString getPrompt() {
        int offset = 0;

        StringBuilder res = new StringBuilder();
        if (eventConsole.getCurrentEventId() != null) {
            res.append("[")
               .append(eventConsole.getCurrentEventName())
               .append("]");
            offset++;
        }

        addOffset(res, offset);

        if (eventTagConsole.getCurrentTagId() != null) {
            res.append("<")
               .append(eventTagConsole.getCurrentTagName())
               .append(">");
            offset++;
            addOffset(res, offset);
        }


        if (userConsole.getCurrentUserId() != null) {
            res.append("(")
               .append(userConsole.getCurrentUsername())
               .append(")");
            offset++;
            addOffset(res, offset);
        }


        if (userTagConsole.getCurrentTagId() != null) {
            res.append("<")
                    .append(userTagConsole.getCurrentTagName())
                    .append(">");
            offset++;
            addOffset(res, offset);
        }

        if (offset == 0) res.append("--> ");

        return new AttributedString(res);
    }

    private void addOffset(StringBuilder res, int offset) {
        if (offset > 0) {
            res.append("\n")
               .append("  ".repeat(offset - 1))
               .append("\\--> ");
        }
    }
}
