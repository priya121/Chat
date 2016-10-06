import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class ReadInTask implements Runnable {
    private final List messages;
    private final BufferedReader reader;

    public ReadInTask(BufferedReader reader, List messages) {
        this.reader = reader;
        this.messages = messages;
    }

    @Override
    public void run() {
        try {
            String message = reader.readLine();
            messages.add(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
