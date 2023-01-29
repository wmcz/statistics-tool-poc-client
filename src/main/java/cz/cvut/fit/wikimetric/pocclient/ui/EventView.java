package cz.cvut.fit.wikimetric.pocclient.ui;

import cz.cvut.fit.wikimetric.pocclient.model.Event;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collection;

@Component
public class EventView {

    public void printAll(Collection<Event> events) {
        events.forEach(this::printEvent);
    }

    public void printEvent(Event event) {
        System.out.println(event.name);
    }

    public void printError(Throwable e) {
        if (e instanceof WebClientRequestException) {
            System.err.println("Chyba připojení k serveru");
            return;
        } else if (e instanceof WebClientResponseException) {
            if (e instanceof WebClientResponseException.NotFound) {
                System.err.println("Událost nenalezena");
                return;
            } else if (e instanceof WebClientResponseException.BadRequest) {
                System.err.println("Událost již existuje");
            }
        }
        System.err.println(e.getMessage());

    }
}
