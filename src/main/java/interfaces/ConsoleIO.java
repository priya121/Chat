package interfaces;

public interface ConsoleIO {
    String getInput();
    void showOutput(String message);
    void showInitialMessage(int port);
    void showExitMessage();
    void welcomeMessage(String name);
    void userJoinedMessage(String name);
}
