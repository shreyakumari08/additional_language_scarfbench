package helidon.tutorial.websocketbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocketbot")
@ApplicationScoped
public class BotEndpoint {
    @Inject BotService bot;
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen public void onOpen(Session s) throws IOException {
        sessions.add(s);
        broadcast("{\"type\":\"info\",\"message\":\"connection opened\"}");
    }

    @OnMessage public void onMessage(String msg, Session s) throws IOException {
        broadcast(msg);
        if (msg.contains("\"target\":\"Duke\"")) {
            String content = extract(msg, "\"message\":\"", "\"");
            String resp = bot.respond(content);
            broadcast("{\"type\":\"chat\",\"name\":\"Duke\",\"target\":\"user\",\"message\":\"" + resp.replace("\"","\\\"") + "\"}");
        }
    }

    @OnClose public void onClose(Session s) { sessions.remove(s); }

    private void broadcast(String msg) {
        for (Session s : sessions) {
            if (s.isOpen()) {
                try { s.getBasicRemote().sendText(msg); } catch (IOException e) { /* ignore */ }
            }
        }
    }
    private String extract(String s, String start, String end) {
        int a = s.indexOf(start); if (a < 0) return "";
        a += start.length(); int b = s.indexOf(end, a); if (b < 0) return "";
        return s.substring(a, b);
    }
}
