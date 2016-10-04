import java.io.IOException;

public class AppCreator {
    private final UserIO console;

    public AppCreator(UserIO console) {
        this.console = console;
    }

    public void create(String choice) throws IOException {
        if (choice.equals("in")) {
            new ClientApp(console).create();
        } else if (choice.equals("out")) {
            new ServerApp(console).create();
        }
    }
}
