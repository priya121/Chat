package protocols;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WelcomeProtocol implements Protocol {
    private final String name;
    private List<User> users;

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
        User user = users.stream()
                .filter(person -> person.getName().equals(name))
                .findAny().orElse(new User(name));
        users.add(user);
        users = getDistinctUsers();
    }

    private List<User> getDistinctUsers() {
        return users.stream()
                .map(User::getName)
                .distinct()
                .map(User::new)
                .collect(Collectors.toList());
    }

    private Optional<User> existingUsersWelcomeMessage(String nameToFind) {
        return users.stream()
                .filter(person -> person.getName().equals(nameToFind))
                .findAny();
    }
}
