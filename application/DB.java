import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DB {

    private static final String URL = "jdbc:mysql://localhost:3306/minifs";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static User getUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                if ("admin".equals(role)) {
                    return new Admin(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }

                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    role
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public static Directory createDirectory(String name, Directory parent) {
        String sql = """
            INSERT INTO nodes (name, type, owner, parent)
            VALUES (?, 'directory', ?, ?)
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            ps.setString(1, name);
            ps.setInt(2, parent.ownerId);
            ps.setInt(3, parent.id);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return new Directory(
                    keys.getInt(1),
                    name,
                    parent,
                    parent.ownerId
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File createFile(String name, Directory parent) {
        String sql = """
            INSERT INTO nodes (name, type, content, owner, parent)
            VALUES (?, 'file', '', ?, ?)
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            ps.setString(1, name);
            ps.setInt(2, parent.ownerId);
            ps.setInt(3, parent.id);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return new File(
                    keys.getInt(1),
                    name,
                    parent,
                    parent.ownerId,
                    ""
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void createLog(String command, User owner) {
        String sql = """
            INSERT INTO commands_log (command, user_id)
            VALUES (?, ?)
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            ps.setString(1, command);
            ps.setInt(2, owner.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getLogs(User owner) {
        ArrayList<String> logs = new ArrayList<>();

        String sql = "SELECT * FROM commands_log WHERE user_id = ? ORDER BY id";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, owner.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                logs.add(
                    String.format(
                        "%-30s %s",
                        rs.getString("command"),
                        rs.getString("timestamp")
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }

    public static void writeFile(int fileId, String content) {
        String sql =
            "UPDATE nodes SET content = ? WHERE id = ? AND type = 'file'";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, content);
            ps.setInt(2, fileId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFileContent(int fileId) {
        String sql = "SELECT content FROM nodes WHERE id = ? AND type = 'file'";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, fileId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean deleteNode(int nodeId, int ownerId) {
        String sql = "DELETE FROM nodes WHERE id = ? AND owner = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, nodeId);
            ps.setInt(2, ownerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateUsername(int userId, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, newUsername);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
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

        return null;
    }

    public static boolean createUser(
        String username,
        String password,
        String role
    ) {
        String sql = """
            INSERT INTO users (username, password, role)
            VALUES (?, ?, ?)
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            // duplicate username, etc.
            e.printStackTrace();
        }

        return false;
    }

    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");

                if ("admin".equals(role)) {
                    users.add(
                        new Admin(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password")
                        )
                    );
                } else {
                    users.add(
                        new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            role
                        )
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int createNode(
        String name,
        String type,
        String content,
        int ownerId,
        Integer parentId
    ) {
        String sql = """
            INSERT INTO nodes (name, type, content, owner, parent)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, content);
            ps.setInt(4, ownerId);

            if (parentId == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, parentId);
            }

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void createInitialFileSystem(int userId) {
        // root (~)
        int rootId = createNode("/", "directory", null, userId, null);

        // system
        int systemId = createNode("system", "directory", null, userId, rootId);

        final String MANUAL = """
            MiniFS Help
            -----------

            cat <file>                     display file contents

            cd <dir>                       change directory (cd .. = parent, cd ~ = root)

            clear                          clear the screen

            echo <text> > <file>           write text to a file (overwrite)

            help                           show command list

            logout                         log out

            ls                             list directory contents

            mkdir <dir>                    create a directory

            poweroff                       exit MiniFS

            rm <file>                      delete a file

            rm -r <dir>                    delete a directory recursively

            touch <file>                   create an empty file

            user list                      list all users (admin only)

            user add <name> <pass> [role]  create a user (admin only)

            user setname <old> <new>       change username (admin only)

            user setpass <name> <pass>     change password (admin only)

            user del <name>                delete a user (admin only)
            """;

        createNode("manual.txt", "file", MANUAL, userId, systemId);

        // workspace
        int workspaceId = createNode(
            "workspace",
            "directory",
            null,
            userId,
            rootId
        );

        createNode("personal", "directory", null, userId, workspaceId);
        createNode("work", "directory", null, userId, workspaceId);
        createNode("notes", "directory", null, userId, workspaceId);

        // logs
        int logsId = createNode("logs", "directory", null, userId, rootId);

        createNode("logs.txt", "file", "", userId, logsId);
    }
}
