import java.io.IOException;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        UserIO console = new UserIO(System.in, System.out);
        new AppCreator(console).start(args[0]);
    }
}
