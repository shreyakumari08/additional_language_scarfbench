package helidon.tutorial.timersession;

import io.helidon.microprofile.scheduling.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

@ApplicationScoped
public class TimerSessionService {
    private static final Logger logger = Logger.getLogger(TimerSessionService.class.getName());
    private final Timer timer = new Timer(true);
    private volatile Date lastProgrammaticTimeout;
    private volatile Date lastAutomaticTimeout;

    public void setTimer(long intervalMillis) {
        timer.schedule(new TimerTask() {
            @Override public void run() { lastProgrammaticTimeout = new Date(); logger.info("Programmatic timeout"); }
        }, intervalMillis);
    }

    // Every 60s (mirrors Spring @Scheduled cron "0 */1 * * * *")
    @Scheduled("0 */1 * * * ?")
    public void automaticTimeout() {
        lastAutomaticTimeout = new Date();
        logger.info("Automatic timeout");
    }

    public String getLastProgrammaticTimeout() { return lastProgrammaticTimeout != null ? lastProgrammaticTimeout.toString() : "never"; }
    public String getLastAutomaticTimeout() { return lastAutomaticTimeout != null ? lastAutomaticTimeout.toString() : "never"; }
}
