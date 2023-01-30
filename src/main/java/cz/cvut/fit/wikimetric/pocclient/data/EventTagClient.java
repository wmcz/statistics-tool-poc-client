package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.EventTag;
import cz.cvut.fit.wikimetric.pocclient.ui.TagView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collection;

@Component
public class EventTagClient {
    private final WebClient eventTagWebClient;
    private final TagView tagView;

    public EventTagClient(@Value("${backend-url}") String backendUrl, TagView tagView) {
        this.eventTagWebClient = WebClient.create(backendUrl + "/tags/event-tags");
        this.tagView = tagView;
    }

    public EventTag create(EventTag eventTag) {
        return eventTagWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventTag)
                .retrieve()
                .bodyToMono(EventTag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<EventTag> readAll() {
        return eventTagWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(EventTag.class)
                .collectList()
                .block();
    }

    public EventTag readOne(Long id) {
        return eventTagWebClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(EventTag.class)
                .block();
    }

    public EventTag update(EventTag eventTag) {
        return eventTagWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eventTag)
                .retrieve()
                .bodyToMono(EventTag.class)
                .block(Duration.ofSeconds(5));
    }




}
