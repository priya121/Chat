import interfaces.ConsoleIO;

import java.util.LinkedList;
import java.util.List;

public class FakeIO implements ConsoleIO {
    public String output;
    private LinkedList<String> input;

    public FakeIO(List<String> input) {
        this.input = new LinkedList<>(input);
        this.output = "";
    }

    @Override
    public String getInput() {
        return input.pop();
    }

    @Override
    public void showOutput(String message) {
        output += message;
    }

    @Override
    public void showInitialMessage(int port) {
        output += "You're connected on port " + String.valueOf(port) + "\n" +
                  "Enter your name to register:\n" +
                  "type quit to exit\n";
    }

    @Override
    public void showExitMessage() {
        output += "Bye!";
    }

    @Override
    public void welcomeMessage(String name) {
        output += "Welcome " + name + "\n";
    }

    @Override
    public void userJoinedMessage(String name) {
        output += "Welcome " + name + "\n";
    }
}
