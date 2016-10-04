import java.io.*;

public class UserIO {
    private final BufferedReader input;
    private final PrintStream output;

    public UserIO(InputStream input, PrintStream output) {
        this.input = new BufferedReader(new InputStreamReader(input));
        this.output = output;
    }

    public String getInput() {
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void showInitialMessage(int port) {
        showOutput("You're connected on port " + String.valueOf(port) + "\n" +
                   "Enter your name to register:\n\n" +
                   "type . to exit:\n");
    }

    public void chatStartedMessage() {
        showOutput("Chat started, type . to quit Application");
    }

    public void showInvalidInputMessage() {
        showOutput("Invalid start message");
    }

    public void showConnectionMessage() {
        showOutput("Type y to start chat or n to exit:");
    }

    public void showOutput(String message) {
        output.println(message);
    }

}


