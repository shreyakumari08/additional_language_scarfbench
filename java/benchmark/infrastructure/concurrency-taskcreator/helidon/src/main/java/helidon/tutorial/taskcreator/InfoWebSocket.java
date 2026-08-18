package helidon.tutorial.taskcreator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/info")
@ApplicationScoped
public class InfoWebSocket {
    @OnOpen public void onOpen(Session s) throws IOException { s.getBasicRemote().sendText("connected"); }
    @OnMessage public void onMessage(String msg, Session s) throws IOException { s.getBasicRemote().sendText("echo: " + msg); }
}
