import org.junit.Test;
import protocols.User;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void canGetAName() {
        User priya = new User("Priya");
        assertEquals(priya.getName(), "Priya");
    }
}
