package feelthesound.api;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class FeelTheSound {

    @RequestMapping(value = "/streams", method = RequestMethod.POST)
    @ResponseBody
    public StreamId initiateStream() {
        StreamId streamId = generateStreamId();
        return streamId;
    }

    private StreamId generateStreamId() {
        String token = UUID.randomUUID().toString();
        return StreamId.builder().token(token).build();
    }

    @Data
    @Builder
    public static class StreamId {
        private final String token;
    }

    public static void main(String[] args) {
        SpringApplication.run(FeelTheSound.class, args);
    }
}

