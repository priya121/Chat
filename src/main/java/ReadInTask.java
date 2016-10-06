import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class ReadInTask implements Runnable {
    private final List messages;
    private final BufferedReader reader;
    private final UserIO console;

    public ReadInTask(BufferedReader reader, List messages, UserIO console) {
        this.reader = reader;
        this.messages = messages;
        this.console = console;
    }

    @Override
    public void run() {
        try {
            String message = reader.readLine();
            messages.add(message);
            String display = "";
            for (Object item :messages) {
                display += (String) item;
            }
            console.showOutput(display);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
