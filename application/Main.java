import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/minifs";
        String user = "root";
        String password = "";

        try {
            // Create connection
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create statement
            Statement stmt = conn.createStatement();

            // Execute query
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Process results
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") +
                        " | " +
                        rs.getString("username") +
                        " | " +
                        rs.getString("password") +
                        " | " +
                        rs.getString("role")
                );
            }

            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
