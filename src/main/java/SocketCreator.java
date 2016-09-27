import interfaces.Exiter;
import interfaces.SocketConnection;

public class SocketCreator {
    private final UserIO console;
    private final SocketConnection socket;

    public SocketCreator(UserIO console, SocketConnection socket) {
        this.console = console;
        this.socket = socket;
    }

    public SocketConnection create(Exiter exit) {
        console.showConnectionMessage();
        String userInput = console.getInput();
        if (invalidInput(userInput)) {
            askAgain(exit);
        } else if (choiceIsNo(userInput)) {
            exit(exit);
        }
        return createSocket();
    }

    private void exit(Exiter exit) {
        console.showExitMessage();
        exit.exit();
    }

    private void askAgain(Exiter exit) {
        console.showInvalidInputMessage();
        create(exit);
    }

    private boolean choiceIsNo(String userInput) {
        return userInput.equals("n");
    }

    private boolean invalidInput(String userInput) {
        return !userInput.equals("y") && !userInput.equals("n");
    }

    private SocketConnection createSocket() {
        return socket;
    }
}
