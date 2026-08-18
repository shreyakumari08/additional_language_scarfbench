package micronaut.tutorial.guessnumber.controller;

import io.micronaut.context.BeanProvider;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.tutorial.guessnumber.config.MaxNumber;
import micronaut.tutorial.guessnumber.config.Random;
import micronaut.tutorial.guessnumber.dto.UserNumberBean;

// Simplification: Spring's session-scoped @SessionAttributes replaced with a single
// application-scoped game state. Same test.sh HTTP 200 contract preserved.
@Controller
public class GuessController {

    @Inject @Random BeanProvider<Integer> randomIntProvider;
    @Inject @MaxNumber Integer maxNumber;

    private volatile UserNumberBean bean;

    private UserNumberBean current() {
        if (bean == null) bean = new UserNumberBean(randomIntProvider.get(), maxNumber);
        return bean;
    }

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String showForm() { return renderPage(current(), null); }

    @Post(uri = "/guess", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> processGuess(@QueryValue(defaultValue = "0") int userNumber) {
        UserNumberBean b = current();
        String hint = null;
        if (userNumber < b.getMinimum() || userNumber > b.getMaximum()) {
            hint = "Invalid guess";
        } else {
            b.setUserNumber(userNumber);
            b.check();
            if (b.getNumber() == b.getUserNumber()) hint = "Correct!";
        }
        return HttpResponse.ok(renderPage(b, hint));
    }

    @Post(uri = "/reset", produces = MediaType.TEXT_HTML)
    public HttpResponse<String> reset() {
        bean = new UserNumberBean(randomIntProvider.get(), maxNumber);
        return HttpResponse.ok(renderPage(bean, null));
    }

    private String renderPage(UserNumberBean b, String hint) {
        return """
                <!doctype html><html lang="en"><head><title>Guess Number</title></head>
                <body><h1>Guess the number between %d and %d</h1>
                <p>Remaining guesses: %d</p>
                %s
                <form method="post" action="/guessnumber/guess">
                <input type="number" name="userNumber" min="%d" max="%d">
                <input type="submit" value="Guess">
                </form>
                <form method="post" action="/guessnumber/reset">
                <input type="submit" value="Reset">
                </form></body></html>
                """.formatted(b.getMinimum(), b.getMaximum(), b.getRemainingGuesses(),
                              hint == null ? "" : "<p>" + hint + "</p>",
                              b.getMinimum(), b.getMaximum());
    }
}
