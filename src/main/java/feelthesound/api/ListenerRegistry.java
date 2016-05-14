package feelthesound.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public final Map<String, List<WebSocketSession>> REGISTRY = new ConcurrentHashMap<>();

    public void register(WebSocketSession session) {
        String identifier = ListenerRegistry.getIdentifier(session);
        if (session.isOpen()) {
            REGISTRY.compute(identifier, (key, oldValue) -> {
                if (oldValue == null) {
                    return listOf(session);
                } else {
                    return concat(of(session), oldValue.stream()).collect(toList());
                }
            });
        }
    }

    public void removeClosed() {
        REGISTRY.keySet().forEach(identifier -> {
            REGISTRY.computeIfPresent(identifier, (key, oldValue) ->
                    oldValue.stream().filter(s -> s.isOpen()).collect(Collectors.toList()));
        });
    }

    public List<WebSocketSession> removeByIdentifer(String identifier) {
        List<WebSocketSession> removed = REGISTRY.remove(identifier);
        return removed == null ? Collections.emptyList() : removed;
    }

    public List<WebSocketSession> getSessions(String identifier) {
        return REGISTRY.getOrDefault(identifier, Collections.emptyList());
    }

    public void sendMessage(String identifier, WebSocketMessage<?> message) throws IOException {
        getSessions(identifier).forEach(s -> {
            try {
                if (s.isOpen()) {
                    s.sendMessage(message);
                }
            } catch (IOException e) {
                log.warn("Could not dispage audio message to listeners.", e);
            }
        });
    }

    public static String getIdentifier(WebSocketSession session) {
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
