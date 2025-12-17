package application.classes;

import application.classes.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Shell {

    private static final String LOGIN_BANNER =
        "███╗   ███╗██╗███╗   ██╗██╗███████╗███████╗\n" +
        "████╗ ████║██║████╗  ██║██║██╔════╝██╔════╝\n" +
        "██╔████╔██║██║██╔██╗ ██║██║█████╗  ███████╗\n" +
        "██║╚██╔╝██║██║██║╚██╗██║██║██╔══╝  ╚════██║\n" +
        "██║ ╚═╝ ██║██║██║ ╚████║██║██║     ███████║\n" +
        "╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═╝╚═╝     ╚══════╝\n" +
        "• login\n" +
        "• exit";
    private static final String PROMPT = "✦";
    private static final String LOGGED_IN_MESSAGE =
        "Welcome to MiniFS. Type 'help' to get started.";

    static Scanner input = new Scanner(System.in);
    private User user;
    private Directory directory;
    private Directory root;

    public Shell(User user, Directory directory) {
        this.user = user;
        this.directory = directory;
        root = directory;
    }

    public Shell() {
        this.user = new User("1337", "noah", "123", "user");
        this.directory = new Directory();
        root = directory;
    }

    private void clearTerminal() {
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }

    public void start() {
        clearTerminal();
        if (!authenticate()) return;
        runFileSystem();
    }

    private boolean authenticate() {
        loginMenu();
        while (true) {
            System.out.print(PROMPT + " ");
            String command = input.nextLine().trim();

            if (command.equals("login")) {
                if (login()) {
                    clearTerminal();
                    return true;
                }

                clearTerminal();
                loginMenu("Login Failed!");
            } else if (command.equals("exit")) {
                return false;
            } else {
                clearTerminal();
                loginMenu("invalid option: " + command);
            }
        }
    }

    private void loginMenu(String errorMessage) {
        System.out.printf("%s\n\n%s\n\n", LOGIN_BANNER, errorMessage);
    }

    private void loginMenu() {
        System.out.printf("%s\n\n", LOGIN_BANNER);
    }

    private boolean login() {
        System.out.print("username: ");
        String username = input.nextLine();
        System.out.print("password: ");
        String password = input.nextLine();

        return username.equals("noah") && password.equals("123");
    }

    private void runFileSystem() {
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
                case "cd":
                    if (arguments.length == 0) {
                        directory = root;
                    } else {
                        changeDirectory(arguments[0]);
                    }
                    break;
                case "clear":
                    clearTerminal();
                    break;
                case "logout":
                    running = false;
                    start();
                    break;
                case "poweroff":
                    running = false;
                    break;
                default:
                    System.out.printf("Command '%s' not found.\n", name);
            }
        } while (running);
    }

    private void changeDirectory(String target) {
        if (target.equals("..")) {
            directory = directory.getParent();
        } else {
            directory = (Directory) directory.getChild(target);
        }
    }

    public void help() {
        System.out.println("help menu");
    }

    public void list() {
        directory.listChildren();
    }
}
