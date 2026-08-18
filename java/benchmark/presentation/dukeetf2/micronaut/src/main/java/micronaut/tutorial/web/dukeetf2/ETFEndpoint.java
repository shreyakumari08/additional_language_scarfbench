package micronaut.tutorial.web.dukeetf2;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import jakarta.inject.Singleton;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ServerWebSocket("/dukeetf")
public class ETFEndpoint {
    private static final Logger logger = Logger.getLogger("ETFEndpoint");
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void open(WebSocketSession session) { sessions.add(session); logger.info("Connection opened."); }

    @OnClose
    public void close(WebSocketSession session) { sessions.remove(session); logger.info("Connection closed."); }

    @OnMessage
    public void message(String msg, WebSocketSession session) {}

    public void broadcast(String msg) {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try { s.sendSync(msg); logger.log(Level.FINE, "Sent: {0}", msg); }
                catch (Exception ignored) {}
            }
        }
    }
}
