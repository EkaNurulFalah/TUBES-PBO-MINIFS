import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// import java.sql.Statement;
// import java.util.ArrayList;

public class DB {

    private static final String URL = "jdbc:mysql://localhost:3306/minifs";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static User getUser(String name, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, name);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // System.out.println("db found something");
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println("db not found shits");
        return null;
    }

    public static Directory getRoot(int ownerId) {
        String sql = """
            SELECT id, name
            FROM nodes
            WHERE owner = ?
              AND parent IS NULL
              AND type = 'directory'
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Directory(
                    rs.getInt("id"),
                    rs.getString("name"),
                    null, // parent
                    ownerId
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // no filesystem for this user
    }

    public static void loadChildren(Directory dir) {
        String sql = """
            SELECT id, name, type, content
            FROM nodes
            WHERE parent = ? AND owner = ?
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, dir.id);
            ps.setInt(2, dir.ownerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");

                if ("directory".equals(type)) {
                    Directory child = new Directory(
                        rs.getInt("id"),
                        rs.getString("name"),
                        dir,
                        dir.ownerId
                    );
                    dir.addChild(child);
                } else {
                    File file = new File(
                        rs.getInt("id"),
                        rs.getString("name"),
                        dir,
                        dir.ownerId,
                        rs.getString("content")
                    );
                    dir.addChild(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTree(Directory dir) {
        loadChildren(dir);

        for (Node n : dir.getChildren()) {
            if (n instanceof Directory) {
                loadTree((Directory) n);
            }
        }
    }
}
