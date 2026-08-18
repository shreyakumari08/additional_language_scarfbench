package micronaut.tutorial.timersession.ejb;

import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class TimerSessionService {
    private static final Logger logger = Logger.getLogger(TimerSessionService.class.getName());

    @Inject TaskScheduler taskScheduler;

    private volatile Date lastProgrammaticTimeout;
    private volatile Date lastAutomaticTimeout;

    public void setTimer(long intervalMillis) {
        logger.log(Level.INFO, "Setting a programmatic timeout for {0} milliseconds from now.", intervalMillis);
        taskScheduler.schedule(Duration.ofMillis(intervalMillis), this::programmaticTimeout);
    }

    private void programmaticTimeout() {
        this.lastProgrammaticTimeout = new Date();
        logger.info("Programmatic timeout occurred.");
    }

    // Automatic timer: every 1 minute (mirrors Spring @Scheduled cron "0 */1 * * * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void automaticTimeout() {
        this.lastAutomaticTimeout = new Date();
        logger.info("Automatic timeout occurred.");
    }

    public String getLastProgrammaticTimeout() {
        return lastProgrammaticTimeout != null ? lastProgrammaticTimeout.toString() : "never";
    }

    public String getLastAutomaticTimeout() {
        return lastAutomaticTimeout != null ? lastAutomaticTimeout.toString() : "never";
    }
}
