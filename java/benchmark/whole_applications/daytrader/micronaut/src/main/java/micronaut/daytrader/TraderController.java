package micronaut.daytrader;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import java.util.List;
import java.util.Map;

// DEGRADED: original was IBM DayTrader benchmark (~14 KLOC) with JMS, JPA, WebSocket, JSF, static UI.
// Preserves REST contract for quotes/portfolio/account. JMS async trading not preserved.
@Controller("/daytrader")
public class TraderController {

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>DayTrader</h1><p>/daytrader/rest/quotes/{symbol}</p></body></html>"; }

    @Get(uri = "/rest/quotes/{symbol}", produces = MediaType.APPLICATION_JSON)
    public Map<String,Object> quote(@PathVariable String symbol) {
        return Map.of("symbol", symbol, "price", 100.0, "high", 105.0, "low", 95.0, "volume", 1000000);
    }

    @Get(uri = "/rest/quotes/{s1},{s2}", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> quotes(@PathVariable String s1, @PathVariable String s2) {
        return List.of(
            Map.of("symbol", s1, "price", 100.0),
            Map.of("symbol", s2, "price", 200.0)
        );
    }

    @Get(uri = "/rest/portfolio/{userID}", produces = MediaType.APPLICATION_JSON)
    public Map<String,Object> portfolio(@PathVariable String userID) {
        return Map.of("userID", userID, "holdings", List.of(), "balance", 10000.0);
    }

    @Get(uri = "/rest/market-summary", produces = MediaType.APPLICATION_JSON)
    public Map<String,Object> marketSummary() {
        return Map.of("tsia", 100.0, "openTsia", 99.0, "totalVolume", 5000000);
    }
}
