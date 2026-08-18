package micronaut.roster.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "PERSISTENCE_ROSTER_LEAGUE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class League implements Serializable {
    @Id protected String id;
    protected String name;
    protected String sport;
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL) protected Collection<Team> teams = new ArrayList<>();
    public String getId() { return id; } public void setId(String s) { this.id = s; }
    public String getName() { return name; } public void setName(String s) { this.name = s; }
    public String getSport() { return sport; } public void setSport(String s) { this.sport = s; }
    public Collection<Team> getTeams() { return teams; } public void setTeams(Collection<Team> t) { this.teams = t; }
}
