package vertx.tutorial.producerfields;
import java.util.Date;
public class ToDo {
    private final long id;
    private final String taskText;
    private final Date timeCreated;
    public ToDo(long id, String taskText, Date timeCreated) { this.id = id; this.taskText = taskText; this.timeCreated = timeCreated; }
    public long getId() { return id; }
    public String getTaskText() { return taskText; }
    public Date getTimeCreated() { return timeCreated; }
}
