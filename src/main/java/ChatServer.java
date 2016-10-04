import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    private SocketConnection client;
    public List<User> users;
    private final UserIO console;
    private final boolean AUTOFLUSH = true;

    public ChatServer(UserIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.console = io;
        this.users = new ArrayList<>();
    }

    public void start() {
        while (true) {
            client = serverSocket.accept();
            createServerThread(client);
        }
    }

    private void createServerThread(SocketConnection client) {
        Runnable runnable = () -> readInFromAndWriteOutToClient(client);
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    public void readInFromAndWriteOutToClient(SocketConnection client) {
        try {
            readInputUntilOver(client);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void readInputUntilOver(SocketConnection client) throws IOException {
        BufferedReader reader = createBufferedReader(client);
        PrintWriter writer = createPrintWriter(client);
        String fromClient = reader.readLine();
        String originalName = fromClient;
        startChat(reader, writer, fromClient);
        console.showExitMessage(originalName);
    }

    private void startChat(BufferedReader reader, PrintWriter writer, String fromClient) throws IOException {
        if (fromClient != null) {
            console.userJoinedMessage(fromClient);
            existingUsersWelcomeMessage(fromClient);
            createNewUsersList(fromClient);
            sendWelcomeMessage(writer, fromClient);
            fromClient = mainChat(reader);
            startChat(reader, writer, fromClient);
        }
    }

    private BufferedReader createBufferedReader(SocketConnection client) {
        InputStream inputStream = client.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    private PrintWriter createPrintWriter(SocketConnection client) {
        OutputStream outToServer = client.getOutputStream();
        return new PrintWriter(outToServer, AUTOFLUSH);
    }

    private String mainChat(BufferedReader reader) throws IOException {
        String messageFromUser = reader.readLine();
        while (messageFromUser != null) {
            console.showOutput(messageFromUser);
            messageFromUser = reader.readLine();
        }
        return messageFromUser;
    }

    private void sendWelcomeMessage(PrintWriter writer, String message) {
        writer.println(message);
        writer.flush();
    }

    private void createNewUsersList(String name) {
        User user = users.stream()
                         .filter(person -> person.getName().equals(name))
                         .findAny().orElse(new User(name));
        users.add(user);
        users = getDistinctUsers();
    }

    private List<User> getDistinctUsers() {
        return users.stream()
                    .map(User::getName)
                    .distinct()
                    .map(User::new)
                    .collect(Collectors.toList());
    }

    private void existingUsersWelcomeMessage(String nameToFind) {
        users.stream()
             .filter(person -> person.getName().equals(nameToFind))
             .findAny()
             .ifPresent(person -> console.showWelcomeBackMessage(person.getName()));
    }

    public void exit() {
        client.close();
    }
}
