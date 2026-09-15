import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

/// Wrapper class that communicates with the Arduino board (Feather M0) via HTTP requests
public class ArduinoClient {

    // Logger for all commands sent and their execution time
    private MovementLogger movementLogger = new MovementLogger();
    private static final String BASE_URL = "http://192.168.1.1"; // Feather M0 AP or LAN IP
    // Reusable HTTP client with short connection timeout
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    // Retrieving the last execution time of a command
    public int getLastDuration(String route) {
        String url = BASE_URL + "/" + route;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                String body = res.body();
                int idx = body.indexOf("time=");
                if (idx != -1) {
                    String timeStr = body.substring(idx + 5).trim();
                    movementLogger.addRecord(route, Integer.parseInt(timeStr));
                    return Integer.parseInt(timeStr);
                }
            } else {
                System.out.println("HTTP error " + res.statusCode() + " for " + route);
            }
        } catch (Exception ex) {
            System.out.println("Error retrieving lastDuration: " + ex.getMessage());
        }
        return -1;
    }

    // Access to the internal link of MovementLogger
    public MovementLogger getMovementLogger() {
        return movementLogger;
    }

    // sending command to the board
    public static void sendCommand(String route) {
        String url = BASE_URL + "/" + route;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        System.out.println("Sending command: " + req.uri());
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response (" + res.statusCode() + "): " + res.body());
        }
        catch (Exception ex) {
            System.out.println("Error sending command: " + ex.getMessage());
        }
    }

    // Retrieving the distance to a obstacle from ultrasound sensor
    public static double getDistanceUSS() {
        String url = BASE_URL + "/distance";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                String body = res.body().trim();
                int start = body.indexOf(":") + 1;
                int end = body.indexOf("}");
                return Double.parseDouble(body.substring(start, end).trim());
            } else {
                System.out.println("Error fetching distance: " + res.statusCode());
            }
        } catch (Exception ex) {
            System.out.println("Error fetching distance: " + ex.getMessage());
        }
        return -1; // Fehlerwert
    }

    private static void checkDistanceAndStop(double distance) {
        if (distance > 0 && distance <= 20) {
            System.out.println("Distance below 20 cm! Sending 'reverse' to stop motors.");
            sendCommand("reverse");
        }
    }

    // Parsing the sensor JSON response
    private static Sensors parseSensorsJson(String json) {
        Sensors s = new Sensors();
        try {
            s.leftOnLine = json.contains("\"left\":true");
            s.rightOnLine = json.contains("\"right\":true");
            return s;
        } catch (Exception e) {
            System.out.println("Error parsing sensors: " + e.getMessage());
            return null;
        }
    }

    // overall sensor status (IR sensors)
    public Sensors getSensors() {
        String url = BASE_URL + "/sensors";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .GET()
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) return null;
            return parseSensorsJson(res.body());
        } catch (Exception ex) {
            System.out.println("Error fetching sensors: " + ex.getMessage());
            return null;
        }
    }
}
