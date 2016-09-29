import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChatClient {
    private final SocketConnection socket;
    private final UserIO io;
    private Time clock;

    public ChatClient(UserIO io, SocketConnection socket) {
        this.io = io;
        this.socket = socket;
    }

    public ChatClient(UserIO io, SocketConnection socketConnection, Time clock) {
        this.io = io;
        this.socket = socketConnection;
        this.clock = clock;
    }

    public void writeOutToAndReadInFromClient() {
        io.showInitialMessage(socket.getPort());
        String name = io.getInput();
        StreamWriter printWriter = createPrintWriter();
        writeMessageToServerUntilQuit(name, printWriter);
        closeSocket();
    }

    public void writeMessageToServerUntilQuit(String name, StreamWriter printWriter) {
        createThreadForTimer();
        try {
            if (!name.equals(".")) {
                printWriter.println(name);
                printWriter.flush();
                readInMessageFromServer();
                io.chatStartedMessage();
                startChat(name, printWriter);
                io.showExitMessage(name);
                name = io.getInput();
                writeMessageToServerUntilQuit(name, printWriter);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void showAllMessages(BufferedReader reader) {
        ReadingAllMessagesTask task = new ReadingAllMessagesTask(reader, io);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> result = executorService.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);

        try {
            TimeUnit.MINUTES.sleep(20000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    private void startChat(String name, StreamWriter printWriter) throws IOException {
        String messages = io.getInput();
        if (!messages.equals(".")) {
            printWriter.println(clock.getTimeStamp() + " - " + name + ": " + messages);
            printWriter.flush();
            startChat(name, printWriter);
        }
    }

    private void readInMessageFromServer() throws IOException {
        BufferedReader reader = createBufferedReader();
        io.welcomeMessage(reader.readLine());
    }

    private StreamWriter createPrintWriter() {
        OutputStream outToServer = socket.getOutputStream();
        return new RealPrintWriter(outToServer);
    }

    private BufferedReader createBufferedReader() {
        InputStream inputFromClient = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputFromClient);
        return new BufferedReader(inputStreamReader);
    }

    private void closeSocket() {
        socket.close();
    }

    private void createThreadForTimer() {
        BufferedReader reader = createBufferedReader();
        Runnable runnable = () -> {
            showAllMessages(reader);
        };
        Executors.newSingleThreadExecutor().submit(runnable);
    }
}

class ReadingAllMessagesTask implements Runnable {
    private final BufferedReader reader;
    private final UserIO io;

    public ReadingAllMessagesTask(BufferedReader reader, UserIO io) {
        this.reader = reader;
        this.io = io;
    }

        @Override
        public void run()
        {
            try {
                String display = "";
                String[] messages = reader.readLine().split(",");
                for (String message :messages) {
                    display += message + "\n";
                }
                io.showOutput(display);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
}
