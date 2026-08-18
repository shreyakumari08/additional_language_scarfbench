package helidon.tutorial.mood.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class TimeOfDayFilter implements ContainerRequestFilter {
    private static final String MOOD = "awake";
    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty("mood", MOOD);
    }
}
