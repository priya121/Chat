public class TestClock implements Time {

    private final String time;

    public TestClock(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return time;
    }
}
