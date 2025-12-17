package application.classes;

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

    public Shell(User user, Directory directory) {
        this.user = user;
        this.directory = directory;
    }

    public Shell() {
        this.user = new User("1337", "noah", "123", "user");
        this.directory = null;
    }

    private static void clearTerminal() {
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

    private static boolean login() {
        System.out.print("username: ");
        String username = input.nextLine();
        System.out.print("password: ");
        String password = input.nextLine();

        return username.equals("noah") && password.equals("123");
    }

    private void runFileSystem() {
        System.out.println(LOGGED_IN_MESSAGE + "\n");

        boolean running = true;
        String command;
        do {
            System.out.printf(user.getUsername() + "@MiniFS" + PROMPT + " ");

            command = input.nextLine().trim();
            switch (command) {
                case "help":
                    help();
                    break;
                case "ls":
                    list();
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
                    System.out.printf("Command '%s' not found.\n", command);
            }
        } while (running);
    }

    public static void help() {
        System.out.println("help menu");
    }

    public static void list() {
        System.out.println("list of folder/file");
    }
}
