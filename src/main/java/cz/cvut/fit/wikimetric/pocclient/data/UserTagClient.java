package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.UserTag;
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
        this.userTagWebClient = WebClient.create(backendUrl + "/tags/event-tags");
        this.tagView = tagView;
    }

    public UserTag create(UserTag userTag) {
        return userTagWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userTag)
                .retrieve()
                .bodyToMono(UserTag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<UserTag> readAll() {
        return userTagWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(UserTag.class)
                .collectList()
                .block();
    }

    public UserTag readOne(Long id) {
        return userTagWebClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserTag.class)
                .block();
    }

    public UserTag update(UserTag userTag) {
        return userTagWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userTag)
                .retrieve()
                .bodyToMono(UserTag.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<UserTag> findByName(String name) {
        return userTagWebClient.get()
                .uri("/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(UserTag.class)
                .collectList()
                .block();
    }
}
