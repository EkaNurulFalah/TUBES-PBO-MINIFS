package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/minifs";
        String user = "root";
        String password = "";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            String sql =
                "INSERT INTO nodes (name, type, content, owner, parent) " +
                "VALUES ('manual.txt', 'file', 'MiniFS User Manual', 1, 2)";

            int result = stmt.executeUpdate(sql);

            if (result > 0) {
                System.out.println("INSERT berhasil!");
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
