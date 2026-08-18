package micronaut.tutorial.rsvp.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import micronaut.tutorial.rsvp.util.ResponseEnum;

@Entity
@Table(name = "RSVP_RESPONSE")
@Serdeable
public class Response {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
    @ManyToOne private Event event;
    @ManyToOne private Person person;
    @Enumerated(EnumType.STRING) private ResponseEnum response;
    public Response() { this.response = ResponseEnum.NOT_RESPONDED; }
    public Response(Event e, Person p, ResponseEnum r) { this.event = e; this.person = p; this.response = r; }
    public Long getId() { return id; }
    public Event getEvent() { return event; } public void setEvent(Event e) { this.event = e; }
    public Person getPerson() { return person; } public void setPerson(Person p) { this.person = p; }
    public ResponseEnum getResponse() { return response; } public void setResponse(ResponseEnum r) { this.response = r; }
}
