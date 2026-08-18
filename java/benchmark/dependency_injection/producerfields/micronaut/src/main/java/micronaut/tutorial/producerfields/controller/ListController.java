package micronaut.tutorial.producerfields.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import micronaut.tutorial.producerfields.entity.ToDo;
import micronaut.tutorial.producerfields.service.RequestService;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ListController {

    @Inject RequestService requestService;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String showForm() { return renderForm(); }

    @Post(uri = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> createToDo(@Body Map<String, String> form) {
        String inputString = form != null ? form.getOrDefault("inputString", "") : "";
        if (!inputString.isBlank()) requestService.createToDo(inputString);
        return HttpResponse.ok(renderForm());
    }

    @Get(uri = "/todolist", produces = MediaType.TEXT_HTML)
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
