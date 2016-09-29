import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatServer {
    private final ServerSocketConnection serverSocket;
    private SocketConnection client;
    public List<User> users;
    private final UserIO console;
    private final boolean AUTOFLUSH = true;
    private List messageHistory;

    public ChatServer(UserIO io, ServerSocketConnection serverSocket) {
        this.serverSocket = serverSocket;
        this.console = io;
        this.users = new ArrayList<>();
        this.messageHistory = new ArrayList();
    }

    public void start() {
        while (true) {
            client = serverSocket.accept();
            createServerThread();
            sendAllMessagesThread(client);
        }
    }

    private void sendAllMessagesThread(SocketConnection client) {
        PrintWriter writer = createPrintWriter(client);
        Runnable runnable = () -> {
            sendAllMessages(writer);
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    private void createServerThread() {
        Runnable runnable = () -> {
            readInFromAndWriteOutToClient(client);
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }

    public void readInFromAndWriteOutToClient(SocketConnection client) {
        try {
            readInputUntilOver(client);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void readInputUntilOver(SocketConnection client) throws IOException, ParseException {
        BufferedReader reader = createBufferedReader(client);
        PrintWriter writer = createPrintWriter(client);
        String fromClient = reader.readLine();
        startChat(reader, writer, fromClient);
        console.showExitMessage(fromClient);
    }

    private void startChat(BufferedReader reader, PrintWriter writer, String fromClient) throws IOException, ParseException {
        if (fromClient != null) {
            console.userJoinedMessage(fromClient);
            existingUsersWelcomeMessage(fromClient);
            createNewUsersList(fromClient);
            sendWelcomeMessage(writer, fromClient);
            fromClient = mainChat(reader, writer);
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

    private String mainChat(BufferedReader reader, PrintWriter writer) throws IOException, ParseException {
        String messageFromUser = reader.readLine();
        while (messageFromUser != null) {
            String message = (messageFromUser);
            addMessage(message);
            console.showOutput(message);
            messageFromUser = reader.readLine();
            //writer.println(messageHistory);
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

    public List getMessageHistory() {
        return messageHistory;
    }

    private void addMessage(String message) {
        messageHistory.add(message);
    }

    public void sendAllMessages(PrintWriter writer) {
        WritingTask task1 = new WritingTask(writer, messageHistory);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> result = executorService.scheduleAtFixedRate(task1, 2, 5, TimeUnit.SECONDS);

        try {
            TimeUnit.MINUTES.sleep(20000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}

class WritingTask implements Runnable {
    private final PrintWriter writer;
    private final List messages;

    public WritingTask(PrintWriter writer, List messages) {
        this.writer = writer;
        this.messages = messages;
    }

    @Override
    public void run()
    {
        try {
            writer.println(messages);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
