package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.Tag;
import cz.cvut.fit.wikimetric.pocclient.ui.view.TagView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collection;

@Component
public class UserTagClient {
    private final WebClient userTagWebClient;
    private final TagView tagView;

    public UserTagClient(@Value("${backend-url}") String backendUrl, TagView tagView) {
        this.userTagWebClient = WebClient.create(backendUrl + "/tags/user-tags");
        this.tagView = tagView;
    }

    public Tag create(Tag tag) {
        return userTagWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tag)
                .retrieve()
                .bodyToMono(Tag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<Tag> readAll() {
        return userTagWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Tag.class)
                .collectList()
                .block();
    }

    public Tag readOne(Long id) {
        return userTagWebClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Tag.class)
                .block();
    }

    public Tag update(Tag tag) {
        return userTagWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tag)
                .retrieve()
                .bodyToMono(Tag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<Tag> findByName(String name) {
        return userTagWebClient.get()
                .uri("/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Tag.class)
                .collectList()
                .block();
    }

    public void delete(Long id) {
        userTagWebClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(5));
    }
}
