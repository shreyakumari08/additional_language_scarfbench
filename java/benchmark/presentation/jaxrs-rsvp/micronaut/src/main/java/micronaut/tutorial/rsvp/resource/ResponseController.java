package micronaut.tutorial.rsvp.resource;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import micronaut.tutorial.rsvp.entity.Event;
import micronaut.tutorial.rsvp.entity.Person;
import micronaut.tutorial.rsvp.entity.Response;
import micronaut.tutorial.rsvp.util.ResponseEnum;

import java.util.List;

@Controller("/webapi")
@Singleton
public class ResponseController {

    @Inject EntityManager em;

    @Post(uri = "/events", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public Event createEvent() {
        Event e = new Event("Sample Event", "Main Hall");
        em.persist(e);
        return e;
    }

    @Post(uri = "/persons", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public Person createPerson() {
        Person p = new Person("Alice");
        em.persist(p);
        return p;
    }

    @Get(uri = "/{eventId}/{inviteId}", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public HttpResponse<Response> getResponse(@PathVariable Long eventId, @PathVariable Long inviteId) {
        List<Response> found = em.createQuery(
            "SELECT r FROM Response r WHERE r.event.id = :eventId AND r.person.id = :personId", Response.class)
            .setParameter("eventId", eventId).setParameter("personId", inviteId).getResultList();
        if (found.isEmpty()) return HttpResponse.notFound();
        return HttpResponse.ok(found.get(0));
    }

    @Post(uri = "/{eventId}/{inviteId}/{response}", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public HttpResponse<Response> setResponse(@PathVariable Long eventId, @PathVariable Long inviteId, @PathVariable String response) {
        Event e = em.find(Event.class, eventId);
        Person p = em.find(Person.class, inviteId);
        if (e == null || p == null) return HttpResponse.notFound();
        Response r = new Response(e, p, ResponseEnum.valueOf(response.toUpperCase()));
        em.persist(r);
        return HttpResponse.ok(r);
    }
}
