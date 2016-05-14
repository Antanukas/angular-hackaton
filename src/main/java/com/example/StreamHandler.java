package com.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Slf4j
public class StreamHandler extends BinaryWebSocketHandler {

    private static final PipedOutputStream out = new PipedOutputStream();
    public static final PipedInputStream in = new PipedInputStream();
    //public volatile static WritableByteChannel writeChannel = Channels.newChannel(out);
    public volatile static WritableByteChannel writeChannel;

    {
        try {
            out.connect(in);
        } catch (IOException e) {
            log.error("Error", e);
        }
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws FileNotFoundException {
        session.setBinaryMessageSizeLimit(1000000);
        log.info("Opened new session in instance " + this);
        writeChannel = Channels.newChannel(new FileOutputStream("/home/antanas/test.ogg"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Sending");
        ListenerHandler.sendMessage(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error", exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        writeChannel.close();
        writeChannel = null;
        log.info("Closed");
    }
}
