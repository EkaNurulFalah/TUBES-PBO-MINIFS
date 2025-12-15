import java.sql.Connection;
import java.sql.DriverManager;

public class App {
    public static void main(String[] args) throws Exception {

    String url = "jdbc:mysql://localhost:3306/minifs";
    String user = "root";
    String password = ""; 

    try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Koneksi berhasil");
            conn.close();
        } catch (Exception e) {
            System.out.println("koneksi gagal");
            e.printStackTrace();
        }
    }
}
