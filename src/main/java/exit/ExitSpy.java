package exit;

public class ExitSpy implements Exiter {
    private final String name;
    public boolean called = false;

    public ExitSpy(String name) {
        this.name = name;
    }

    @Override
    public void exit() {
        called = true;
    }
}
