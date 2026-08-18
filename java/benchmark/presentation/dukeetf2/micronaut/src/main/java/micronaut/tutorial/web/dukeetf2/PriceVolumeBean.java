package micronaut.tutorial.web.dukeetf2;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Random;

@Singleton
public class PriceVolumeBean {
    private final Random random = new Random();
    private volatile double price = 100.0;
    private volatile int volume = 300000;

    @Inject ETFEndpoint endpoint;

    @Scheduled(fixedDelay = "1s")
    public void tick() {
        price += 1.0 * (random.nextInt(100) - 50) / 100.0;
        volume += random.nextInt(5000) - 2500;
        endpoint.broadcast(String.format("%.2f / %d", price, volume));
    }

    public String snapshot() { return String.format("%.2f / %d", price, volume); }
}
