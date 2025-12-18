import java.util.Arrays;
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

    public void run() {
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

            String[] command = input.nextLine().split(" ");
            String name = command[0];
            String[] arguments = Arrays.copyOfRange(command, 1, command.length);
            switch (name) {
                case "help":
                    help();
                    break;
                case "ls":
                    list();
                    break;
                case "clear":
                    Console.clear();
                    break;
                case "cd":
                    if (arguments.length == 0) {
                        directory = root;
                    } else {
                        changeDirectory(arguments[0]);
                    }
                    break;
                case "cat":
                    readFile(arguments[0]);
                    break;
                case "poweroff":
                    running = false;
                    break;
                default:
                    System.out.printf("Command '%s' not found.\n", name);
            }
            //     switch (name) {
            //         case "mkdir":
            //             makeDirectory(arguments[0]);
            //             break;
            //         case "touch":
            //             makeFile(arguments[0]);
            //             break;
            //         case "echo":
            //             echo(arguments[0], arguments[2]);
            //             break;
            //         case "cat":
            //             readFile(arguments[0]);
            //             break;
            //         case "rm":
            //             remove(arguments[0]);
            //             break;
            //     }
        } while (running);
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

        // 🔥 THIS is the missing piece
        if (next.getChildren().isEmpty()) {
            DB.loadChildren(next);
        }

        directory = next;
    }

    // public void makeDirectory(String name) {
    //     System.out.println("lets make directory");
    //     directory.addChild(new Directory(name, directory));
    // }

    // public void makeFile(String name) {
    //     System.out.println("lets make file");
    //     directory.addChild(new File(name, directory));
    // }

    // public void echo(String content, String target) {
    //     System.out.println(target);
    //     System.out.println("lets write to a file :)");

    //     ((File) directory.getChild(target)).write(content);
    // }

    public void readFile(String target) {
        // Ensure directory contents are loaded
        if (directory.getChildren().isEmpty()) {
            DB.loadChildren(directory);
        }

        Node node = directory.getChild(target);

        if (node == null) {
            System.out.println("cat: no such file: " + target);
            return;
        }

        if (node.isDirectory()) {
            System.out.println("cat: " + target + ": is a directory");
            return;
        }

        File file = (File) node;
        file.read();
    }

    // public void remove(String name) {
    //     directory.deleteChild(name);
    // }
}
