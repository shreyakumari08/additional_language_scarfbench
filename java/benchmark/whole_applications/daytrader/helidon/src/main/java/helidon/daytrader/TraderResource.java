package helidon.daytrader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// DEGRADED: full port ~14 KLOC. Quotes/portfolio/market-summary preserved.
@Path("/daytrader") @ApplicationScoped
public class TraderResource {
    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>DayTrader</h1></body></html>"; }

    @GET @Path("/rest/quotes/{symbol}") @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> quote(@PathParam("symbol") String symbol) {
        return Map.of("symbol", symbol, "price", 100.0, "high", 105.0, "low", 95.0, "volume", 1000000);
    }

    @GET @Path("/rest/portfolio/{userID}") @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> portfolio(@PathParam("userID") String userID) {
        return Map.of("userID", userID, "holdings", List.of(), "balance", 10000.0);
    }

    @GET @Path("/rest/market-summary") @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> marketSummary() {
        return Map.of("tsia", 100.0, "openTsia", 99.0, "totalVolume", 5000000);
    }
}
