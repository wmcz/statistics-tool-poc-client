package cz.cvut.fit.wikimetric.pocclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

public class Event {
    public Long id;
    public Collection<Long> tagIds;
    public String name;
    public Instant startDate;
    public Instant endDate;
    public Collection<Long> userIds;

    public Event(@JsonProperty("id") Long id,
                 @JsonProperty("tagIds") Collection<Long> tagIds,
                 @JsonProperty("name") String name,
                 @JsonProperty("startDate") Instant startDate,
                 @JsonProperty("endDate") Instant endDate,
                 @JsonProperty("userIds") Collection<Long> userIds) {
        this.id = id;
        this.tagIds = tagIds;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userIds = userIds;
    }

    public Event(String name, Collection<Long> tagIds) {
        this(0L, tagIds, name, null, null, new ArrayList<>());
    }

    public Event(String name) {
        this(name, new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", tagIds=" + tagIds +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", userIds=" + userIds +
                '}';
    }
}
