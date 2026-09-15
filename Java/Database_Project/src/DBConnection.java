package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String url = "jdbc:mysql://localhost:3306/Schema_final";
    private static final String user = "Magnetar";
    private static final String password = "Ibbh56@hb,7-noiaU#0.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    public static void main(String[] args) {
        

        try (Connection connection = getConnection()) {
            System.out.println("Connection successful");

            Statement stmt = connection.createStatement();

            // Beispiel-Query (anpassen bei Bedarf!) --> sollte aber funktionieren | Query example (adapt if necessary) --> but it should work
            ResultSet rs = stmt.executeQuery(
                "SELECT Username FROM User WHERE Username = 'LuffyOfficial'"
            );

            if (rs.next()) {
                System.out.println("Checkpoint member found: " + rs.getString("Username"));
            } else {
                System.out.println("Checkpoint member NOT found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
