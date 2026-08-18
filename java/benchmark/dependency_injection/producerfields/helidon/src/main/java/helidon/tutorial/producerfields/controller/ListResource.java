package helidon.tutorial.producerfields.controller;

import helidon.tutorial.producerfields.entity.ToDo;
import helidon.tutorial.producerfields.service.RequestService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.stream.Collectors;

@Path("/")
@RequestScoped
public class ListResource {

    @Inject RequestService requestService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderForm(); }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String createToDo(@FormParam("inputString") String inputString) {
        if (inputString != null && !inputString.isBlank()) requestService.createToDo(inputString);
        return renderForm();
    }

    @GET
    @Path("/todolist")
    @Produces(MediaType.TEXT_HTML)
    public String showToDoList() {
        var todos = requestService.getToDos();
        String rows = todos.stream()
            .map(t -> "<li>" + t.getId() + ": " + t.getTaskText() + " (@ " + t.getTimeCreated() + ")</li>")
            .collect(Collectors.joining());
        return """
                <!doctype html><html lang="en"><head><title>ToDo List</title></head>
                <body><h1>ToDo List</h1><ul>%s</ul>
                <p><a href="/producerfields/">Back</a></p></body></html>
                """.formatted(rows);
    }

    private String renderForm() {
        return """
                <!doctype html><html lang="en"><head><title>ToDo</title></head>
                <body><h1>ToDo</h1>
                <form method="post" action="/producerfields/create">
                <input type="text" name="inputString"><input type="submit" value="Add">
                </form>
                <p><a href="/producerfields/todolist">List</a></p>
                </body></html>
                """;
    }
}
