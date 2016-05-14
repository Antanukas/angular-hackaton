package feelthesound.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Slf4j
@Component
public class ListenerRegistry {

    public final Map<String, WebSocketSession> streamers = new ConcurrentHashMap<>();
    public final Map<String, List<WebSocketSession>> listenerRegistry = new ConcurrentHashMap<>();


    public Optional<WebSocketSession> getStreamer(String subscriberId) {
        return Optional.ofNullable(streamers.get(subscriberId));
    }

    public void registerStreamer(WebSocketSession session) {
        streamers.compute(ListenerRegistry.getSubscriberId(session), (key, oldValue) -> {
            if (oldValue != null) {
                try {
                    oldValue.close();
                } catch (IOException e) {
                    log.warn("Failed to close old websocket", e);
                }
            }
            return session;
        });
    }

    public void removeStreamer(WebSocketSession session) {
        streamers.remove(getSubscriberId(session));
    }

    public void registerListener(WebSocketSession session) {
        String subscriberId = ListenerRegistry.getSubscriberId(session);
        if (session.isOpen()) {
            listenerRegistry.compute(subscriberId, (key, oldValue) -> {
                if (oldValue == null) {
                    return listOf(session);
                } else {
                    return concat(of(session), oldValue.stream()).collect(toList());
                }
            });
        }
    }

    public void removeClosed() {
        listenerRegistry.keySet().forEach(subscriberId -> {
            listenerRegistry.computeIfPresent(subscriberId, (key, oldValue) ->
                    oldValue.stream().filter(s -> s.isOpen()).collect(Collectors.toList()));
        });
    }

    public List<WebSocketSession> getListeners(String subscriberId) {
        return listenerRegistry.getOrDefault(subscriberId, Collections.emptyList());
    }

    public void sendMessage(String subscriberId, WebSocketMessage<?> message) throws IOException {
        getListeners(subscriberId).forEach(s -> {
            try {
                if (s.isOpen()) {
                    s.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn("Could not dispatch audio message to listeners.", e);
            }
        });
    }

    public static String getSubscriberId(WebSocketSession session) {
        String[] pathParts = session.getUri().toString().split("/");
        String id = pathParts[pathParts.length - 1];
        return id;
    }

    static List<WebSocketSession> listOf(WebSocketSession item) {
        ArrayList<WebSocketSession> list = new ArrayList<>();
        list.add(item);
        return Collections.unmodifiableList(list);
    }
}
