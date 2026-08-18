package micronaut.tutorial.producerfields.service;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import micronaut.tutorial.producerfields.entity.ToDo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Singleton
public class RequestService {

    @Inject EntityManager em;

    @Transactional
    public ToDo createToDo(String inputString) {
        ToDo toDo = new ToDo();
        Date currentTime = Calendar.getInstance().getTime();
        toDo.setTaskText(inputString);
        toDo.setTimeCreated(currentTime);
        em.persist(toDo);
        return toDo;
    }

    @Transactional
    public List<ToDo> getToDos() {
        return em.createQuery("SELECT t FROM ToDo t ORDER BY t.timeCreated", ToDo.class).getResultList();
    }
}
