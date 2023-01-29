package cz.cvut.fit.wikimetric.pocclient.model;

import java.util.Collection;

public class UserTag {
    public String name;
    public Long id;
    public boolean assignable;
    public Collection<Event> tagged;
    public UserTag parent;
    public Collection<UserTag> children;

    public UserTag(String name, Long id, boolean assignable, Collection<Event> tagged, UserTag parent, Collection<UserTag> children) {
        this.name = name;
        this.id = id;
        this.assignable = assignable;
        this.tagged = tagged;
        this.parent = parent;
        this.children = children;
    }
}
