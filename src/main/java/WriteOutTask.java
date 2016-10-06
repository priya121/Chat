import streamwriter.StreamWriter;

import java.util.List;

public class WriteOutTask implements Runnable {
    private final List messages;
    private final StreamWriter writer;


    public WriteOutTask(List messages, StreamWriter writer) {
        this.messages = messages;
        this.writer = writer;
    }

    @Override
    public void run() {
        String toSend = "";
        for (Object message : messages) {
            toSend += (String) message;
        }
        writer.println("2");
        writer.println(toSend);
    }
}
