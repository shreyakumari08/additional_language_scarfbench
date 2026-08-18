package micronaut.tutorial.mood.web;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SimpleServletListener implements ApplicationEventListener<ServerStartupEvent> {
    private static final Logger log = LoggerFactory.getLogger(SimpleServletListener.class);

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        log.info("Context initialized");
    }
}
