package helidon.tutorial.web.dukeetf2;

import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class PriceVolumeBean {
    private final Random random = new Random();
    private volatile double price = 100.0;
    private volatile int volume = 300000;

    @FixedRate(value = 1, timeUnit = TimeUnit.SECONDS)
    public void tick() {
        price += 1.0 * (random.nextInt(100) - 50) / 100.0;
        volume += random.nextInt(5000) - 2500;
        ETFEndpoint.broadcast(String.format("%.2f / %d", price, volume));
    }

    public String snapshot() { return String.format("%.2f / %d", price, volume); }
}
