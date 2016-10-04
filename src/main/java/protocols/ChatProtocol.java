package protocols;

public class ChatProtocol implements Protocol {
    private final String message;

    public ChatProtocol(String message) {
        this.message = message;
    }

    @Override
    public String action() {
        return message;
    }
}
