package micronaut.tutorial.taskcreator;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.ServerWebSocket;

@ServerWebSocket("/info")
public class InfoWebSocket {
    @OnOpen public void onOpen(WebSocketSession session) { session.sendSync("connected"); }
    @OnMessage public void onMessage(String msg, WebSocketSession session) { session.sendSync("echo: " + msg); }
    @OnClose public void onClose(WebSocketSession session) {}
}
