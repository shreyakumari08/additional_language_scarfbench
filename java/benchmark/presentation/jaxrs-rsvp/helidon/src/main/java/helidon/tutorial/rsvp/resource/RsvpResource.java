package helidon.tutorial.rsvp.resource;

import helidon.tutorial.rsvp.entity.Event;
import helidon.tutorial.rsvp.entity.Person;
import helidon.tutorial.rsvp.entity.Response;
import helidon.tutorial.rsvp.util.ResponseEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/webapi")
@ApplicationScoped
public class RsvpResource {

    @PersistenceContext(unitName = "rsvp") EntityManager em;

    @GET @Path("/status") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Event> getAllEvents() { return em.createQuery("SELECT e FROM Event e", Event.class).getResultList(); }

    @GET @Path("/status/{eventId}") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public jakarta.ws.rs.core.Response getEvent(@PathParam("eventId") Long eventId) {
        Event e = em.find(Event.class, eventId);
        return e == null ? jakarta.ws.rs.core.Response.status(404).build() : jakarta.ws.rs.core.Response.ok(e).build();
    }

    @POST @Path("/events") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public Event createEvent() { Event e = new Event("Sample Event", "Main Hall"); em.persist(e); return e; }

    @POST @Path("/persons") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public Person createPerson() { Person p = new Person("Alice"); em.persist(p); return p; }

    @POST @Path("/{eventId}/{inviteId}/{response}") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public jakarta.ws.rs.core.Response setResponse(@PathParam("eventId") Long eventId,
                                                    @PathParam("inviteId") Long inviteId,
                                                    @PathParam("response") String response) {
        Event e = em.find(Event.class, eventId);
        Person p = em.find(Person.class, inviteId);
        if (e == null || p == null) return jakarta.ws.rs.core.Response.status(404).build();
        Response r = new Response(e, p, ResponseEnum.valueOf(response.toUpperCase()));
        em.persist(r);
        return jakarta.ws.rs.core.Response.ok(r).build();
    }
}
