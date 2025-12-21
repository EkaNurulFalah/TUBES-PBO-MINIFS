import java.util.Scanner;

public class Main {

    private static Scanner input = new Scanner(System.in);
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

    public static void main(String[] args) {
        Console.clear();

        while (true) {
            User user = login();
            if (user == null) {
                break;
            }

            Directory root = DB.getRoot(user.getId());
            DB.loadChildren(root);
            Shell shell = new Shell(user, root);
            ShellExit exit = shell.run();

            if (exit == ShellExit.POWEROFF) {
                break;
            }

            Console.clear();
        }
    }

    private static User login() {
        loginMenu();
        while (true) {
            System.out.print(PROMPT + " ");
            String command = input.nextLine().trim();

            if (command.equals("login")) {
                User user = authenticate();

                if (user != null) {
                    Console.clear();
                    return user;
                }

                Console.clear();
                loginMenu("Login Failed!");
            } else if (command.equals("exit")) {
                return null;
            } else {
                Console.clear();
                loginMenu("invalid option: " + command);
            }
        }
    }

    private static User authenticate() {
        System.out.print("username: ");
        String username = input.nextLine();
        System.out.print("password: ");
        String password = input.nextLine();

        return DB.getUser(username, password);
    }

    private static void loginMenu(String errorMessage) {
        System.out.printf("%s\n\n%s\n\n", LOGIN_BANNER, errorMessage);
    }

    private static void loginMenu() {
        System.out.printf("%s\n\n", LOGIN_BANNER);
    }
}
