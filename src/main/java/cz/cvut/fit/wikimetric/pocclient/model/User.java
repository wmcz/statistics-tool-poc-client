package cz.cvut.fit.wikimetric.pocclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;

public class User {
    public Long id;
    public String username;
    public String email;
    public Collection<Long> tagIds;
    public Collection<Long> eventIds;

    public User(@JsonProperty("id") Long id,
                @JsonProperty("username") String username,
                @JsonProperty("email") String email,
                @JsonProperty("tagIds") Collection<Long> tagIds,
                @JsonProperty("eventIds") Collection<Long> eventIds) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.tagIds = tagIds;
        this.eventIds = eventIds;
    }

    public User(String username) {
        this(null, username, null, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", tagIds=" + tagIds +
                ", eventIds=" + eventIds +
                '}';
    }
}
