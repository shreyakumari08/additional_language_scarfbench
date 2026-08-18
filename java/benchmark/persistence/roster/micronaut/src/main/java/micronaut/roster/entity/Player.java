package micronaut.roster.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "PERSISTENCE_ROSTER_PLAYER")
public class Player implements Serializable {
    @Id private String id;
    private String name;
    private String position;
    private double salary;
    @ManyToMany(mappedBy = "players") private Collection<Team> teams = new ArrayList<>();
    public Player() {}
    public Player(String id, String name, String position, double salary) {
        this.id = id; this.name = name; this.position = position; this.salary = salary;
    }
    public String getId() { return id; } public void setId(String s) { this.id = s; }
    public String getName() { return name; } public void setName(String s) { this.name = s; }
    public String getPosition() { return position; } public void setPosition(String s) { this.position = s; }
    public double getSalary() { return salary; } public void setSalary(double s) { this.salary = s; }
    public Collection<Team> getTeams() { return teams; } public void setTeams(Collection<Team> t) { this.teams = t; }
}
