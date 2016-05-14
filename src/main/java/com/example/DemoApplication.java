package com.example;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Controller
@SpringBootApplication
public class DemoApplication {

    private final Map<StreamId, Object> streams = new ConcurrentHashMap<>();

    @RequestMapping("/")
    @ResponseBody
    String home() {
      return "Hello World! 55";
    }


/*
    @RequestMapping(value = "/listen", method = RequestMethod.GET)
    public void listen(HttpServletResponse r) throws IOException, InterruptedException {
        dosmth(r);
        System.out.println("Stopped");
    }
*/

    private void dosmth(HttpServletResponse r) throws IOException, InterruptedException {
        ServletOutputStream os = r.getOutputStream();
        r.setHeader("Content-Type", "audio/ogg");
        byte[] buffer = new byte[10000];
        System.out.println("Listening");
        while (true) {
            int bytesRead = StreamHandler.in.read(buffer, 0, buffer.length - 1);
            if (bytesRead > 0) {
                os.write(buffer,0,bytesRead - 1);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                throw e;
            }
        }
    }

    @RequestMapping(value = "/stream", method = RequestMethod.POST)
    @ResponseBody
    public StreamId initiateStream() {
        String token = UUID.randomUUID().toString();
        StreamId streamId = StreamId.builder().token(token).build();
        streams.put(streamId, new Object());
        return streamId;
    }

/*
    @RequestMapping("/")
    @ResponseBody
    public byte[] listen() {

    }
*/

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Data
    @Builder
    public static class StreamId {
        private final String token;
    }
}

