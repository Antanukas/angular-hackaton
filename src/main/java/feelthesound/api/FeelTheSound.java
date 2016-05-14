package feelthesound.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class FeelTheSound {

    @Autowired
    private ListenerRegistry registry;

    @RequestMapping(value = "/rest/streams/{subscriberId}", method = RequestMethod.GET)
    @ResponseBody
    public Streamer getStreamer(@PathVariable String subscriberId) {
        if (!registry.getStreamer(subscriberId).isPresent()) {
            throw new RuntimeException("Streamer not found");
        }
        return Streamer
                .builder()
                .subscriberId(subscriberId)
                .listenerCount(registry.getListeners(subscriberId).size())
                .subscriberSessionId(registry.getStreamer(subscriberId).get().getId())
                .build();
    }

    @Data
    @Builder
    public static class Streamer {
        private final String subscriberId;
        private final int listenerCount;
        private final String subscriberSessionId;
    }

    public static void main(String[] args) {
        SpringApplication.run(FeelTheSound.class, args);
    }
}

