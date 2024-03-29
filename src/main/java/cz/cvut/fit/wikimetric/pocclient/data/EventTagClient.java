package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collection;

@Component
public class EventTagClient {
    private final WebClient eventTagWebClient;
    public EventTagClient(@Value("${backend-url}") String backendUrl) {
        this.eventTagWebClient = WebClient.create(backendUrl + "/tags/event-tags");
    }

    public Tag create(Tag tag) {
        return eventTagWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tag)
                .retrieve()
                .bodyToMono(Tag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<Tag> readAll() {
        return eventTagWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Tag.class)
                .collectList()
                .block();
    }

    public Tag readOne(Long id) {
        return eventTagWebClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Tag.class)
                .block();
    }

    public Tag update(Tag tag) {
        return eventTagWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tag)
                .retrieve()
                .bodyToMono(Tag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<Event> getEvents(Long id) {
        return eventTagWebClient.get()
                .uri("/{id}/events?withChildren=true", id)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .block();
    }

    public Collection<Event> addEvents(Long id, Collection<Long> eventIds) {
        return eventTagWebClient.post()
                .uri("/{id}/events", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventIds)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .block();
    }

    public Collection<Event> removeEvents(Long id, Collection<Long> eventIds) {
        return eventTagWebClient.method(HttpMethod.DELETE)
                .uri("/{id}/events", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventIds)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .block();
    }


    public Collection<Tag> findByName(String name) {
        return eventTagWebClient.get()
                .uri("/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Tag.class)
                .collectList()
                .block();
    }

    public void delete(Long id) {
        eventTagWebClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(5));
    }
}
