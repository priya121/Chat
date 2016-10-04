import clock.Clock;
import clock.TestClock;
import clock.Time;

import java.io.IOException;

public class AppCreator {
    private final UserIO console;
    private Time clock;

    public AppCreator(UserIO console) {
        this.console = console;
        this.clock = new Clock();
    }


    public AppCreator(UserIO console, TestClock testClock) {
        this.console = console;
        this.clock = testClock;

    }
    public void start(String choice) throws IOException {
        create(choice).start();
    }

    private App create(String choice) throws IOException {
        if (choice.equals("in")) {
            return new ClientApp(console, clock);
        } else {
            return new ServerApp(console);
        }
    }
}
