package micronaut.roster;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import micronaut.roster.entity.League;
import micronaut.roster.entity.SummerLeague;
import micronaut.roster.entity.WinterLeague;
import micronaut.roster.entity.Team;
import micronaut.roster.entity.Player;
import micronaut.roster.entity.IncorrectSportException;

import java.util.List;
import java.util.Map;

// DEGRADED: original was multi-module (roster-common + roster-boot). Flattened to single module.
@Controller("/roster")
public class RosterController {
    @Inject EntityManager em;

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Roster Service (5-entity JPA)</h1></body></html>"; }

    @Post(uri = "/init", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public Map<String,Object> init() throws IncorrectSportException {
        SummerLeague sl = new SummerLeague("L1", "MLS", "soccer");
        em.persist(sl);
        Team t = new Team("T1", "Red Team", "SF");
        t.setLeague(sl);
        em.persist(t);
        Player p = new Player("P1", "Alice", "Forward", 100000);
        em.persist(p);
        t.getPlayers().add(p);
        em.flush();
        return Map.of("leagues", 1, "teams", 1, "players", 1);
    }

    @Get(uri = "/leagues", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> leagues() {
        return em.createQuery("SELECT l FROM League l", League.class).getResultList().stream()
            .<Map<String,Object>>map(l -> Map.of("id", l.getId(), "name", l.getName(), "sport", l.getSport())).toList();
    }

    @Get(uri = "/teams", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> teams() {
        return em.createQuery("SELECT t FROM Team t", Team.class).getResultList().stream()
            .<Map<String,Object>>map(t -> Map.of("id", t.getId(), "name", t.getName(), "city", t.getCity())).toList();
    }

    @Get(uri = "/players", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> players() {
        return em.createQuery("SELECT p FROM Player p", Player.class).getResultList().stream()
            .<Map<String,Object>>map(p -> Map.of("id", p.getId(), "name", p.getName(), "position", p.getPosition())).toList();
    }
}
