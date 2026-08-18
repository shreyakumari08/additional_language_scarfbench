package micronaut.tutorial.websocketbot;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import jakarta.inject.Inject;

@ServerWebSocket("/websocketbot")
public class BotWebSocket {
    @Inject BotService bot;
    @Inject WebSocketBroadcaster broadcaster;

    @OnOpen public void onOpen(WebSocketSession session) {
        broadcaster.broadcastSync("{\"type\":\"info\",\"message\":\"connection opened\"}");
    }

    @OnMessage public void onMessage(String msg, WebSocketSession session) {
        broadcaster.broadcastSync(msg);
        // JSON contract: {"type":"chat","target":"Duke","name":"user","message":"..."}
        if (msg.contains("\"target\":\"Duke\"")) {
            String content = extract(msg, "\"message\":\"", "\"");
            String resp = bot.respond(content);
            broadcaster.broadcastSync("{\"type\":\"chat\",\"name\":\"Duke\",\"target\":\"user\",\"message\":\"" + resp.replace("\"","\\\"") + "\"}");
        }
    }

    @OnClose public void onClose(WebSocketSession session) {}

    private String extract(String s, String start, String end) {
        int a = s.indexOf(start); if (a < 0) return "";
        a += start.length();
        int b = s.indexOf(end, a); if (b < 0) return "";
        return s.substring(a, b);
    }
}
