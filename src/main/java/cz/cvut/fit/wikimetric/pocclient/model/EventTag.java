package cz.cvut.fit.wikimetric.pocclient.model;

import java.util.Collection;

public class EventTag {
    public String name;
    public Long id;
    public boolean assignable;
    public Collection<Event> tagged;
    public EventTag parent;
    public Collection<EventTag> children;

    public EventTag(String name, Long id, boolean assignable, Collection<Event> tagged, EventTag parent, Collection<EventTag> children) {
        this.name = name;
        this.id = id;
        this.assignable = assignable;
        this.tagged = tagged;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public String toString() {
        return "EventTag{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", assignable=" + assignable +
                ", tagged=" + tagged +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }
}
