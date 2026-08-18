package micronaut.roster.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "PERSISTENCE_ROSTER_TEAM")
public class Team implements Serializable {
    @Id private String id;
    private String name;
    private String city;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "PERSISTENCE_ROSTER_TEAM_PLAYER",
        joinColumns = @JoinColumn(name = "TEAM_ID"),
        inverseJoinColumns = @JoinColumn(name = "PLAYER_ID"))
    private Collection<Player> players = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "LEAGUE_ID")
    private League league;

    public Team() {}
    public Team(String id, String name, String city) { this.id = id; this.name = name; this.city = city; }
    public String getId() { return id; } public void setId(String s) { this.id = s; }
    public String getName() { return name; } public void setName(String s) { this.name = s; }
    public String getCity() { return city; } public void setCity(String s) { this.city = s; }
    public Collection<Player> getPlayers() { return players; }
    public void setPlayers(Collection<Player> p) { this.players = p; }
    public League getLeague() { return league; } public void setLeague(League l) { this.league = l; }
}
