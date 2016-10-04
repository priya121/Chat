import interfaces.ConsoleIO;
import interfaces.ServerSocketConnection;
import interfaces.SocketConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    public List<User> users;
    private ConsoleIO io;

    public ChatServer(ConsoleIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.io = io;
        this.users = new ArrayList<>();
    }

    public void readInFromAndWriteOutToClient() {
        try {
            SocketConnection server = serverSocket.accept();
            BufferedReader reader = createBufferedReader(server);
            readInputTillOver(reader, server);
            io.showExitMessage();
            closeSocket();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BufferedReader createBufferedReader(SocketConnection server) {
            InputStream inputStream = server.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new BufferedReader(inputStreamReader);
    }

    private void readInputTillOver(BufferedReader reader, SocketConnection server) throws IOException {
            String name = reader.readLine();
            while (name != null) {
                String nameToFind = name;
                io.userJoinedMessage(name);
                existingUsersWelcomeMessage(nameToFind);
                createNewUsersList(name, nameToFind);
                writeOutToClient(name , server);
                name = reader.readLine();
            }
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
                    .map(person -> person.getName())
                    .distinct()
                    .map(person -> new User(person))
                    .collect(Collectors.toList());
    }

    private void existingUsersWelcomeMessage(String nameToFind) {
        users.stream()
             .filter(person -> person.getName().equals(nameToFind))
             .findAny()
             .ifPresent(person -> io.showOutput("Welcome back " + person.getName()));
    }

    private void writeOutToClient(String message, SocketConnection socket) {
            OutputStream outToServer = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outToServer, true);
            printWriter.println(message);
            printWriter.flush();
    }

    private void closeSocket() {
        serverSocket.close();
    }
}
