import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

public class VulnerableApp {

    // Secrets from environment variables (no hardcoded password)
    private static final String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://mydatabase.com/mydb");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "admin");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    public static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        return scanner.nextLine().trim();
    }

    // Avoid Runtime.exec (command injection). Safe placeholder.
    public static void sendEmail(String to, String subject, String body) {
        System.out.println("Email queued to=" + to + " subject=" + subject + " bodyLength=" + body.length());
    }

    // Use HTTPS + timeouts
    public static String getData() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://example.com/get-data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error fetching data.");
        }
        return result.toString();
    }

    // PreparedStatement prevents SQL injection
    public static void saveToDb(String data) {
        String query = "INSERT INTO mytable (column1, column2) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, data);
            stmt.setString(2, "Another Value");

            stmt.executeUpdate();
            System.out.println("Data saved to database.");

        } catch (SQLException e) {
            System.out.println("Database error occurred.");
        }
    }

    public static void main(String[] args) {
        String userInput = getUserInput();
        String data = getData();
        saveToDb(data);
        sendEmail("admin@example.com", "User Input", userInput);
    }
}
