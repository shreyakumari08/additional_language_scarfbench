package helidon.tutorial.web.dukeetf2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ApplicationScoped
@ServerEndpoint("/dukeetf")
public class ETFEndpoint {
    private static final Logger logger = Logger.getLogger("ETFEndpoint");
    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen public void open(Session s) { sessions.add(s); logger.info("Connection opened."); }
    @OnClose public void close(Session s) { sessions.remove(s); }
    @OnError public void error(Session s, Throwable t) { sessions.remove(s); }

    public static void broadcast(String msg) {
        for (Session s : sessions) {
            if (s.isOpen()) { try { s.getBasicRemote().sendText(msg); } catch (IOException ignored) {} }
        }
    }
}
