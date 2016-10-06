package protocols;

import java.util.List;
import java.util.Optional;

public class WelcomeProtocol implements Protocol {
    private final String name;
    private final List<User> users;

    public WelcomeProtocol(String name, List<User> users) {
        this.name = name;
        this.users = users;
    }

    @Override
    public String action() {
        if ((existingUsersWelcomeMessage(name).isPresent())) {
            return "Welcome back " + name + "!\n";
        } else {
            createNewUsersList(name);
            return "Welcome " + name + "!\n";
        }
    }

    private void createNewUsersList(String name) {
        users.add(new User(name));
    }

    private Optional<User> existingUsersWelcomeMessage(String nameToFind) {
        return users.stream()
                .filter(person -> person.getName().equals(nameToFind))
                .findAny();
    }
}
