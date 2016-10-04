import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
    @Test
    public void canSetAName() {
        User priya = new User();
        priya.setName("Priya");
        assertEquals(priya.getName(), "Priya");
    }
}
