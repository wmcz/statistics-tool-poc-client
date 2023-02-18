package cz.cvut.fit.wikimetric.pocclient.data;

import cz.cvut.fit.wikimetric.pocclient.model.User;
import cz.cvut.fit.wikimetric.pocclient.ui.view.UserView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collection;

@Component
public class UserClient {
    private final WebClient userWebClient;
    private final UserView userView;

    public UserClient(@Value("${backend-url}") String backendUrl, UserView userView) {
        this.userWebClient = WebClient.create(backendUrl + "/users");
        this.userView = userView;
    }

    public User create(User user) {
        return userWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .block(Duration.ofSeconds(5));
    }

    public Collection<User> readAll() {
        return userWebClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(User.class)
                .collectList()
                .block();
    }

    public User readOne(Long id) {
        return userWebClient.get()
                .uri( "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    public Collection<User> findByUsername(String username) {
        return userWebClient.get()
                .uri("/username/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(User.class)
                .collectList()
                .block();
    }

    public User update(User user) {
        return userWebClient.put()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .block(Duration.ofSeconds(5));
    }


    public void delete(Long id) {
        userWebClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(5));
    }
}
