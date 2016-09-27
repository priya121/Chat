import interfaces.ServerSocketConnection;
import interfaces.SocketConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    public List<User> users;
    private final UserIO io;

    public ChatServer(UserIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.io = io;
        this.users = new ArrayList<>();
    }

    public void start() {
        while (true) {
            readInFromAndWriteOutToClient();
        }
    }

    public void readInFromAndWriteOutToClient() {
        try {
            SocketConnection client = serverSocket.accept();
            BufferedReader reader = createBufferedReader(client);
            readInputUntilOver(reader, client);
            io.showExitMessage();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BufferedReader createBufferedReader(SocketConnection client) {
        InputStream inputStream = client.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

    private void readInputUntilOver(BufferedReader reader, SocketConnection server) throws IOException {
        String name = reader.readLine();
        while (name != null) {
            String nameToFind = name;
            io.userJoinedMessage(name);
            existingUsersWelcomeMessage(nameToFind);
            createNewUsersList(name, nameToFind);
            sendWelcomeMessage(name , server);
            name = chat(reader);
        }
    }

    private String chat(BufferedReader reader) throws IOException {
        String name = reader.readLine();
        while (name != null) {
            io.showName(name);
            name = reader.readLine();
        }
        return name;
    }

    private void sendWelcomeMessage(String message, SocketConnection socket) {
        OutputStream outToServer = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outToServer, true);
        printWriter.println(message);
        printWriter.flush();
    }

    private void createNewUsersList(String name, String nameToFind) {
        User user = users.stream()
                         .filter(person -> person.getName().equals(nameToFind))
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
             .ifPresent(person -> io.showWelcomeBackMessage(person.getName()));
    }
}
