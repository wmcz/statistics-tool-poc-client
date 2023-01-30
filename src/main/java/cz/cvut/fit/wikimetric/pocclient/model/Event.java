package cz.cvut.fit.wikimetric.pocclient.model;

import java.time.Instant;
import java.util.Collection;

public class Event {
    public Long id;
    public Collection<EventTag> tags;
    public String name;
    public Instant startDate;
    public Instant endDate;
    public Collection<User> participants;
    public EventImpact impact;

    public Event(Long id, Collection<EventTag> tags, String name, Instant startDate, Instant endDate, Collection<User> participants, EventImpact impact) {
        this.id = id;
        this.tags = tags;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.impact = impact;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", tags=" + tags +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", participants=" + participants +
                ", impact=" + impact +
                '}';
    }
}
