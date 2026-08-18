package helidon.roster.entity;
import jakarta.persistence.Entity;
@Entity
public class SummerLeague extends League {
    public SummerLeague() {}
    public SummerLeague(String id, String name, String sport) throws IncorrectSportException {
        this.id = id; this.name = name;
        if (sport.equalsIgnoreCase("swimming") || sport.equalsIgnoreCase("soccer") || sport.equalsIgnoreCase("basketball") || sport.equalsIgnoreCase("baseball")) this.sport = sport;
        else throw new IncorrectSportException("Sport is not a summer sport.");
    }
}
