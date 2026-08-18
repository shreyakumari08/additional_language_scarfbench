package helidon.tutorial.producerfields.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
public class ToDo implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected String taskText;
    @Temporal(TemporalType.TIMESTAMP) protected Date timeCreated;
    public ToDo() {}
    public Long getId() { return id; }
    public String getTaskText() { return taskText; }
    public void setTaskText(String s) { this.taskText = s; }
    public Date getTimeCreated() { return timeCreated; }
    public void setTimeCreated(Date d) { this.timeCreated = d; }
}
