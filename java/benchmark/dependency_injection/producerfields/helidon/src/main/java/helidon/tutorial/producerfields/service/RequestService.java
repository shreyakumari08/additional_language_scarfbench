package helidon.tutorial.producerfields.service;

import helidon.tutorial.producerfields.entity.ToDo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class RequestService {

    @PersistenceContext(unitName = "producerfields")
    private EntityManager em;

    @Transactional
    public ToDo createToDo(String inputString) {
        ToDo t = new ToDo();
        Date now = Calendar.getInstance().getTime();
        t.setTaskText(inputString);
        t.setTimeCreated(now);
        em.persist(t);
        return t;
    }

    @Transactional
    public List<ToDo> getToDos() {
        return em.createQuery("SELECT t FROM ToDo t ORDER BY t.timeCreated", ToDo.class).getResultList();
    }
}
