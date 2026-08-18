package micronaut.tutorial.taskcreator;
import io.micronaut.serde.annotation.Serdeable;
@Serdeable
public class Task {
    private long id; private String name; private String status; private long timestamp;
    public Task() {}
    public Task(long id, String name, String status) { this.id = id; this.name = name; this.status = status; this.timestamp = System.currentTimeMillis(); }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public String getName() { return name; } public void setName(String s) { this.name = s; }
    public String getStatus() { return status; } public void setStatus(String s) { this.status = s; }
    public long getTimestamp() { return timestamp; } public void setTimestamp(long t) { this.timestamp = t; }
}
