import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        UserIO console = new UserIO(System.in, System.out);
        new AppCreator(console).create(args[0]);
    }
}
