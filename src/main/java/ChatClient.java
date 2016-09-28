import java.io.*;

public class ChatClient {
    private final SocketConnection socket;
    private final UserIO io;

    public ChatClient(UserIO io, SocketConnection socket) {
        this.io = io;
        this.socket = socket;
    }

    public void writeOutToAndReadInFromClient() {
        io.showInitialMessage(socket.getPort());
        String name = io.getInput();
        StreamWriter printWriter = createPrintWriter();
        writeMessageToServerUntilQuit(name, printWriter);
        closeSocket();
    }

    public void writeMessageToServerUntilQuit(String name, StreamWriter printWriter) {
        try {
            if (!name.contains(".")) {
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

    private void startChat(String name, StreamWriter printWriter) throws IOException {
        String messages = io.getInput();
        if (!messages.contains(".")) {
            printWriter.println(name + ": " + messages);
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
}
