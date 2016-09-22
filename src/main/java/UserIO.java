import interfaces.ConsoleIO;

import java.io.*;

public class UserIO implements ConsoleIO {
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

    public void showOutput(String message) {
        output.println(message);
    }

    public void showInitialMessage(int port) {
        showOutput("You're connected on port " + String.valueOf(port) + "\n" +
                   "Enter your name to register:\n" +
                   "type quit to exit");
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

}


