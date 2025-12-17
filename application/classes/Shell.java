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
        "● login\n" +
        "● exit";
    static Scanner input = new Scanner(System.in);
    private User current;
    private Directory currentDirectory;

    public Shell(User current, Directory currentDirectory) {
        this.current = current;
        this.currentDirectory = currentDirectory;
    }

    public Shell() {
        this.current = null;
        this.currentDirectory = null;
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
            System.out.print("✦ ");
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
        System.out.println("hello, file system.");
    }

    public static void help() {
        System.out.println("help menu");
    }

    public static void list() {
        System.out.println("list of folder/file");
    }
}
