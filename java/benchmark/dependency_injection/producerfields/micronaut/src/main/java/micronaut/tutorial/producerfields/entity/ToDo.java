package micronaut.tutorial.producerfields.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Serdeable
public class ToDo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected String taskText;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timeCreated;

    public ToDo() {}
    public ToDo(Long id, String taskText, Date timeCreated) {
        this.id = id; this.taskText = taskText; this.timeCreated = timeCreated;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskText() { return taskText; }
    public void setTaskText(String s) { this.taskText = s; }
    public Date getTimeCreated() { return timeCreated; }
    public void setTimeCreated(Date d) { this.timeCreated = d; }
}
