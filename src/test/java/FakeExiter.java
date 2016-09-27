public class FakeExiter implements Exiter {
    private final UserIO console;
    public boolean called = false;

    public FakeExiter(UserIO console) {
        this.console = console;
    }

    @Override
    public void exit() {
        called = true;
        console.showExitMessage();
    }
}
