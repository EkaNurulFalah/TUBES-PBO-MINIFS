import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);
    static String username;

    public static void main(String[] args) {
        clear();
        System.out.println("Welcome message");
        System.out.println("login menu");
        login();

        String userInput;
        do {
            System.out.printf(username + "@MiniFS✦ ");

            userInput = input.nextLine().toLowerCase();

            switch (userInput) {
                case "help":
                    help();
                    break;
                case "ls":
                    list();
                    break;
                case "clear":
                    clear();
                    break;
                case "logout":
                    clear();
                    System.out.println("Welcome message");
                    login();
                    break;
                case "poweroff":
                    break;
                default:
                    System.out.printf("invalid option: %s\n", userInput);
            }
        } while (!isExit(userInput));
    }

    public static void login() {
        System.out.print("username: ");
        username = input.nextLine();
        System.out.print("password: ");
        input.nextLine();
    }

    public static void help() {
        System.out.println("help menu");
    }

    public static void clear() {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();

        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }

    public static void list() {
        System.out.println("list of folder/file");
    }

    public static boolean isExit(String userInput) {
        return userInput.equalsIgnoreCase("poweroff");
    }
}
