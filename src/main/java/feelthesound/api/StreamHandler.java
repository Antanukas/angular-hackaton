package feelthesound.api;

import java.io.FileNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Slf4j
public class StreamHandler extends BinaryWebSocketHandler {

    @Autowired
    private ListenerRegistry registry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws FileNotFoundException {
        session.setBinaryMessageSizeLimit(1000000);
        registry.registerStreamer(session);
        log.debug("Connected streamer '{}'", ListenerRegistry.getSubscriberId(session));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String subscriberId = ListenerRegistry.getSubscriberId(session);
        registry.sendMessage(subscriberId, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error", exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        registry.removeStreamer(session);
        log.debug("Disconnected streamer '{}'", ListenerRegistry.getSubscriberId(session));
    }
}
