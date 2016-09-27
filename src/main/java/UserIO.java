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
                   "Enter your name to register:\n" +
                   "type . to exit:\n");
    }

    public void showExitMessage() {
        showOutput("Bye!");
    }

    public void userJoinedMessage(String name) {
        showOutput(name + " has now joined the chat room");
    }

    public void welcomeMessage(String name) {
        showOutput("Welcome " + name);
    }

    public void chatStartedMessage() {
        showOutput("Chat started, type . to quit App");
    }

    public void showInvalidInputMessage() {
        showOutput("Invalid start message");
    }

    public void showName(String name) {
        showOutput(name);
    }

    public void showWelcomeBackMessage(String name) {
        showOutput("Welcome back " + name);
    }

    public void showConnectionMessage() {
        showOutput("Type y to start chat or n to exit:");
    }

    private void showOutput(String message) {
        output.println(message);
    }

}


