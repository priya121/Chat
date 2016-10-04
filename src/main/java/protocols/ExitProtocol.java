package protocols;

public class ExitProtocol implements Protocol {
    private final String name;

    public ExitProtocol(String name) {
        this.name = name;
    }

    @Override
    public String action() {
        return "Bye " + name + "! Exiting...";
    }
}
