public class User {

    private String name;

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    public void setName(String givenName) {
        name = givenName;
    }

    public String getName() {
        return name;
    }
}
