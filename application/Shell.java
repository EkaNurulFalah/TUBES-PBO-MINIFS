import java.util.ArrayList;
import java.util.Scanner;

public class Shell {

    private static final String LOGGED_IN_MESSAGE =
        "Welcome to MiniFS. Type 'help' to get started.";
    private static final String PROMPT = "✦";
    private static Scanner input = new Scanner(System.in);

    private User user;
    private Directory directory;
    private Directory root;

    public Shell(User user, Directory directory) {
        this.user = user;
        this.directory = directory;
        root = directory;
    }

    public ShellExit run() {
        System.out.println(LOGGED_IN_MESSAGE + "\n");
        boolean running = true;

        do {
            System.out.printf(
                user.getUsername() +
                    "@MiniFS:" +
                    directory.getPath() +
                    PROMPT +
                    " "
            );

            String line = input.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            String name;
            String rest;

            int firstSpace = line.indexOf(' ');

            if (firstSpace == -1) {
                name = line;
                rest = "";
            } else {
                name = line.substring(0, firstSpace);
                rest = line.substring(firstSpace + 1).trim();
            }

            switch (name) {
                case "help":
                    help();
                    DB.createLog(line, user);
                    break;
                case "ls":
                    list();
                    DB.createLog(line, user);
                    break;
                case "clear":
                    Console.clear();
                    DB.createLog(line, user);
                    break;
                case "cd":
                    if (rest.isEmpty()) {
                        directory = root;
                    } else {
                        changeDirectory(rest);
                    }
                    DB.createLog(line, user);
                    break;
                case "cat":
                    if (rest.isEmpty()) {
                        System.out.println("cat: missing file operand");
                    } else {
                        readFile(rest);
                    }
                    DB.createLog(line, user);
                    break;
                case "mkdir":
                    if (rest.isEmpty()) {
                        System.out.println("mkdir: missing operand");
                    } else {
                        makeDirectory(rest);
                    }
                    DB.createLog(line, user);
                    break;
                case "touch":
                    if (rest.isEmpty()) {
                        System.out.println("touch: missing file operand");
                    } else {
                        makeFile(rest);
                    }
                    DB.createLog(line, user);
                    break;
                case "echo":
                    handleEcho(line);
                    DB.createLog(line, user);
                    break;
                case "rm":
                    remove(rest);
                    DB.createLog(line, user);
                    break;
                case "logout":
                    DB.createLog(line, user);
                    return ShellExit.LOGOUT;
                case "poweroff":
                    DB.createLog(line, user);
                    return ShellExit.POWEROFF;
                case "user":
                    handleUser(rest);
                    DB.createLog(line, user);
                    break;
                case "history":
                    history();
                    DB.createLog(line, user);
                    break;
                default:
                    System.out.printf("Command '%s' not found.\n", name);
            }
        } while (running);

        return ShellExit.POWEROFF;
    }

    public void help() {
        Directory root = DB.getRoot(user.getId());
        Node node = resolvePath(root, "~/system/manual.txt");

        if (node instanceof File) {
            ((File) node).read();
        } else {
            System.out.println("help: manual not found");
        }
    }

    public void history() {
        ArrayList<String> logs = DB.getLogs(user);

        for (String log : logs) {
            System.out.println(log);
        }
    }

    public Node resolvePath(Directory root, String path) {
        String[] parts = path.replace("~", "").split("/");
        Directory current = root;

        for (int i = 1; i < parts.length; i++) {
            if (current.getChildren().isEmpty()) {
                DB.loadChildren(current);
            }

            Node next = current.getChild(parts[i]);
            if (next == null) return null;

            if (i == parts.length - 1) return next;

            if (!next.isDirectory()) return null;
            current = (Directory) next;
        }

        return current;
    }

    public void list() {
        directory.listChildren();
    }

    private void changeDirectory(String target) {
        if (target.equals("..")) {
            if (directory.getParent() != null) {
                directory = directory.getParent();
            }
            return;
        }

        Node node = directory.getChild(target);

        if (node == null) {
            System.out.println("cd: no such directory: " + target);
            return;
        }

        if (!node.isDirectory()) {
            System.out.println("cd: not a directory: " + target);
            return;
        }

        Directory next = (Directory) node;

        if (next.getChildren().isEmpty()) {
            DB.loadChildren(next);
        }

        directory = next;
    }

    public void makeDirectory(String name) {
        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        if (directory.getChild(name) != null) {
            System.out.println("mkdir: directory already exists");
            return;
        }

        Directory dir = DB.createDirectory(name, directory);

        if (dir != null) {
            directory.addChild(dir);
        }
    }

    public void makeFile(String name) {
        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        if (directory.getChild(name) != null) {
            System.out.println("touch: file already exists");
            return;
        }

        File file = DB.createFile(name, directory);

        if (file != null) {
            directory.addChild(file);
        }
    }

    private void handleEcho(String line) {
        int redirect = line.indexOf('>');

        if (redirect == -1) {
            System.out.println("echo: missing redirection");
            return;
        }

        String content = line.substring(5, redirect).trim();
        String target = line.substring(redirect + 1).trim();

        if (content.startsWith("\"") && content.endsWith("\"")) {
            content = content.substring(1, content.length() - 1);
        }

        echo(content, target);
    }

    public void echo(String content, String target) {
        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        Node node = directory.getChild(target);

        if (node == null) {
            System.out.println("echo: no such file: " + target);
            return;
        }

        if (node.isDirectory()) {
            System.out.println("echo: " + target + ": is a directory");
            return;
        }

        File file = (File) node;
        DB.writeFile(file.getId(), content);
        file.write(content);
    }

    public void readFile(String target) {
        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        Node node = directory.getChild(target);

        if (node == null || node.isDirectory()) {
            System.out.println("cat: invalid file");
            return;
        }

        File file = (File) node;
        String content = DB.readFileContent(file.getId());
        System.out.println(content);
    }

    private void remove(String rest) {
        if (rest.isEmpty()) {
            System.out.println("rm: missing operand");
            return;
        }

        boolean recursive = false;
        String target;

        if (rest.startsWith("-r ")) {
            recursive = true;
            target = rest.substring(3).trim();
        } else {
            target = rest.trim();
        }

        if (target.equals(".") || target.equals("..")) {
            System.out.println("rm: refusing to remove " + target);
            return;
        }

        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        Node node = directory.getChild(target);

        if (node == null) {
            System.out.println(
                "rm: cannot remove '" + target + "': No such file or directory"
            );
            return;
        }

        if (node.isDirectory()) {
            Directory dir = (Directory) node;

            if (!recursive) {
                System.out.println(
                    "rm: cannot remove '" + target + "': Is a directory"
                );
                return;
            }

            DB.loadChildren(dir);
        }

        boolean deleted = DB.deleteNode(node.id, user.getId());

        if (!deleted) {
            System.out.println("rm: failed to remove '" + target + "'");
            return;
        }

        directory.removeChild(node);
    }

    private void handleUser(String rest) {
        if (!user.isAdmin()) {
            System.out.println("Permission denied: admin only");
            return;
        }

        String[] parts = rest.split(" ");

        if (parts.length == 0) {
            System.out.println("user: missing command");
            return;
        }

        switch (parts[0]) {
            case "add": {
                if (parts.length < 3) {
                    System.out.println(
                        "Usage: user add <username> <password> [role]"
                    );
                    return;
                }

                String username = parts[1];
                String password = parts[2];
                String role = (parts.length >= 4) ? parts[3] : "user";

                if (!role.equals("user") && !role.equals("admin")) {
                    System.out.println("Invalid role: " + role);
                    return;
                }

                if (DB.userExists(username)) {
                    System.out.println("User already exists");
                    return;
                }

                if (DB.createUser(username, password, role)) {
                    User newUser = DB.getUserByUsername(username);

                    if (newUser != null) {
                        DB.createInitialFileSystem(newUser.getId());
                    }

                    System.out.println("User created: " + username);
                }

                break;
            }
            case "setname": {
                if (parts.length < 3) {
                    System.out.println(
                        "Usage: user setname <oldUsername> <newUsername>"
                    );
                    return;
                }

                String oldName = parts[1];
                String newName = parts[2];

                User target = DB.getUserByUsername(oldName);
                if (target == null) {
                    System.out.println("User not found");
                    return;
                }

                if (DB.userExists(newName)) {
                    System.out.println("Username already exists");
                    return;
                }

                if (DB.updateUsername(target.getId(), newName)) {
                    if (target.getId() == user.getId()) {
                        user.setUsername(newName);
                    }

                    System.out.println(
                        "Username updated: " + oldName + " → " + newName
                    );
                } else {
                    System.out.println("Failed to update username");
                }
                break;
            }
            case "setpass": {
                if (parts.length < 3) {
                    System.out.println(
                        "Usage: user setpass <username> <newPassword>"
                    );
                    return;
                }

                String username = parts[1];
                String newPass = parts[2];

                User target = DB.getUserByUsername(username);
                if (target == null) {
                    System.out.println("User not found");
                    return;
                }

                if (DB.updatePassword(target.getId(), newPass)) {
                    System.out.println(
                        "Password updated for user: " + username
                    );

                    if (target.getId() == user.getId()) {
                        System.out.println("Please log in again.");
                    }
                } else {
                    System.out.println("Failed to update password");
                }
                break;
            }
            case "list": {
                ArrayList<User> users = DB.getAllUsers();

                System.out.println("ID   USERNAME   ROLE");
                for (User u : users) {
                    System.out.printf(
                        "%-4d %-10s %s%n",
                        u.getId(),
                        u.getUsername(),
                        u.getRole()
                    );
                }
                break;
            }
            case "del": {
                if (parts.length < 2) {
                    System.out.println("Usage: user del <username>");
                    return;
                }

                String username = parts[1];

                User target = DB.getUserByUsername(username);
                if (target == null) {
                    System.out.println("User not found");
                    return;
                }

                if (target.getId() == user.getId()) {
                    System.out.println("Cannot delete yourself");
                    return;
                }

                if (DB.deleteUser(target.getId())) {
                    System.out.println("User deleted: " + username);
                } else {
                    System.out.println("Failed to delete user");
                }
                break;
            }
            default:
                System.out.println("Unknown user command");
        }
    }
}
