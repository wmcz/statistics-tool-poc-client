package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.Event;
import cz.cvut.fit.wikimetric.pocclient.ui.view.EventView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collection;

@Component
public class EventClient {
    private final WebClient eventWebClient;
    private final EventView eventView;

    public EventClient(@Value("${backend-url}") String backendUrl, EventView eventView) {
        this.eventWebClient = WebClient.create(backendUrl + "/events");
        this.eventView = eventView;
    }

    public Event create(Event event) {
        return eventWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Event.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<Event> readAll() {
        return eventWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .block();
    }

    public Event readOne(Long id) {
        return eventWebClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Event.class)
                .block();
    }

    public Event update(Event event) {
        return eventWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Event.class)
                .block(Duration.ofSeconds(5));
    }


    public Collection<Event> findByName(String name) {
        return eventWebClient.get()
                .uri("/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Event.class)
                .collectList()
                .block();
    }

    public void delete(Long id) {
        eventWebClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(5));
    }
}
