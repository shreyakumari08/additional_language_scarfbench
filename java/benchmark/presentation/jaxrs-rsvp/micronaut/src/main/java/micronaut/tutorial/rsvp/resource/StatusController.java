package micronaut.tutorial.rsvp.resource;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import micronaut.tutorial.rsvp.entity.Event;

import java.util.List;

@Controller("/webapi/status")
@Singleton
public class StatusController {

    @Inject EntityManager em;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Event> getAllEvents() {
        return em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
    }

    @Get(uri = "/{eventId}", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public Event getEvent(@PathVariable Long eventId) { return em.find(Event.class, eventId); }
}
