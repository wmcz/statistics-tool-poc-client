package cz.cvut.fit.wikimetric.pocclient.model;

import java.util.Collection;

public class User {
    public Long id;
    public String username;
    public String email;
    public Collection<UserTag> tags;
    public Collection<Event> events;
    public UserImpact impact;

    User(Long id, String username, String email, Collection<UserTag> tags, Collection<Event> events, UserImpact impact) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.tags = tags;
        this.events = events;
        this.impact = impact;
    }
}
