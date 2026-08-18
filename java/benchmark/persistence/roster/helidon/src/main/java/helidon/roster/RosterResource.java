package helidon.roster;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import helidon.roster.entity.League;
import helidon.roster.entity.SummerLeague;
import helidon.roster.entity.Team;
import helidon.roster.entity.Player;
import helidon.roster.entity.IncorrectSportException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// DEGRADED: original was multi-module. Flattened.
@Path("/roster") @ApplicationScoped
public class RosterResource {
    @PersistenceContext(unitName = "roster") EntityManager em;

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Roster Service (5-entity JPA)</h1></body></html>"; }

    @POST @Path("/init") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public Map<String,Object> init() throws IncorrectSportException {
        SummerLeague sl = new SummerLeague("L1", "MLS", "soccer");
        em.persist(sl);
        Team t = new Team("T1", "Red Team", "SF");
        t.setLeague(sl); em.persist(t);
        Player p = new Player("P1", "Alice", "Forward", 100000);
        em.persist(p);
        t.getPlayers().add(p);
        em.flush();
        return Map.of("leagues", 1, "teams", 1, "players", 1);
    }

    @GET @Path("/leagues") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> leagues() {
        return em.createQuery("SELECT l FROM League l", League.class).getResultList().stream()
            .<Map<String,Object>>map(l -> Map.of("id", l.getId(), "name", l.getName(), "sport", l.getSport())).collect(Collectors.toList());
    }

    @GET @Path("/teams") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> teams() {
        return em.createQuery("SELECT t FROM Team t", Team.class).getResultList().stream()
            .<Map<String,Object>>map(t -> Map.of("id", t.getId(), "name", t.getName(), "city", t.getCity())).collect(Collectors.toList());
    }

    @GET @Path("/players") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> players() {
        return em.createQuery("SELECT p FROM Player p", Player.class).getResultList().stream()
            .<Map<String,Object>>map(p -> Map.of("id", p.getId(), "name", p.getName(), "position", p.getPosition())).collect(Collectors.toList());
    }
}
