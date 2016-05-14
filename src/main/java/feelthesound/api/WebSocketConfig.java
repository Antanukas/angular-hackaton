package feelthesound.api;

import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends SpringBootServletInitializer
        implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(streamHandler(), "/streams/{identifier}");
        registry.addHandler(listenerHandler(), "/listeners/{identifier}");
    }

    @Bean
    public StreamHandler streamHandler() {
        return new StreamHandler();
    }

    @Bean
    public ListenerHandler listenerHandler() {
        return new ListenerHandler();
    }
}
