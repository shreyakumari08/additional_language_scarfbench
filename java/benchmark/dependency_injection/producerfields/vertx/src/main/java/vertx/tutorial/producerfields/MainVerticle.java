package vertx.tutorial.producerfields;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

    // Vert.x has no JPA. Preserves external contract with in-memory store (documented simplification).
    private final ConcurrentSkipListMap<Long, ToDo> store = new ConcurrentSkipListMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/producerfields/").handler(this::showForm);
        router.get("/producerfields").handler(this::showForm);
        router.post("/producerfields/create").handler(this::create);
        router.get("/producerfields/todolist").handler(this::todolist);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(renderForm());
    }

    private void create(RoutingContext ctx) {
        String inputString = ctx.request().getFormAttribute("inputString");
        if (inputString != null && !inputString.isBlank()) {
            long id = seq.getAndIncrement();
            store.put(id, new ToDo(id, inputString, new Date()));
        }
        ctx.response().putHeader("content-type", "text/html").end(renderForm());
    }

    private void todolist(RoutingContext ctx) {
        List<ToDo> todos = store.values().stream()
            .sorted((a, b) -> a.getTimeCreated().compareTo(b.getTimeCreated()))
            .collect(Collectors.toList());
        String rows = todos.stream()
            .map(t -> "<li>" + t.getId() + ": " + t.getTaskText() + " (@ " + t.getTimeCreated() + ")</li>")
            .collect(Collectors.joining());
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>ToDo List</title></head>
                <body><h1>ToDo List</h1><ul>%s</ul>
                <p><a href="/producerfields/">Back</a></p></body></html>
                """.formatted(rows));
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
