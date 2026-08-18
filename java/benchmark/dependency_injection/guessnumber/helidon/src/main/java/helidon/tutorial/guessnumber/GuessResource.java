package helidon.tutorial.guessnumber;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class GuessResource {

    @Inject GameState state;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderPage(null); }

    @POST
    @Path("/guess")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String guess(@FormParam("userNumber") int userNumber) {
        String hint = state.tryGuess(userNumber);
        return renderPage(hint);
    }

    @POST
    @Path("/reset")
    @Produces(MediaType.TEXT_HTML)
    public String reset() { state.reset(); return renderPage(null); }

    private String renderPage(String hint) {
        return """
                <!doctype html><html lang="en"><head><title>Guess Number</title></head>
                <body><h1>Guess the number between %d and %d</h1>
                <p>Remaining guesses: %d</p>
                %s
                <form method="post" action="/guessnumber/guess">
                <input type="number" name="userNumber" min="%d" max="%d">
                <input type="submit" value="Guess">
                </form></body></html>
                """.formatted(state.getMinimum(), state.getMaximum(), state.getRemainingGuesses(),
                              hint == null ? "" : "<p>" + hint + "</p>",
                              state.getMinimum(), state.getMaximum());
    }
}
