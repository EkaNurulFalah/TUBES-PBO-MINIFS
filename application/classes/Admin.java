
public class Admin extends User {

    public Admin(int id, String username, String password) {
        super(id, username, password, "admin");
    }

    public void createUser(String username, String password, String role) {

    }

    public void deleteUser(String username) {

    }

    public void resetPassword(String username, String newPassword) {

    }
}
