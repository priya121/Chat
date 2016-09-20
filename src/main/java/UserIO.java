import java.io.*;

public class UserIO {

    private final BufferedReader input;
    private PrintStream output;

    public UserIO(InputStream input, PrintStream output) {
        this.input = new BufferedReader(new InputStreamReader(input));
        this.output = output;
    }

    public String getInput() {
        String message = "";
        try {
            message = input.readLine();
            return message;
        } catch (IOException e) {
            showOutput("No input to read");
            e.printStackTrace();
        }
        return message;
    }

    public void showOutput(String message) {
        output.println(message);
    }

    public void showConnectionMessage(int port) {
        output.print("You're connected on port " + String.valueOf(port) + "\n");

    }

    public void showInitialMessage() {
        showOutput("Enter your name to register:\n" +
                   "type quit to exit");
    }

    public void showExitMessage() {
        showOutput("Bye!");
    }


}


