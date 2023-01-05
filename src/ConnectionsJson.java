import java.net.HttpURLConnection;

public class ConnectionsJson {
    public HttpURLConnection connection;
    public String json;

    public ConnectionsJson(HttpURLConnection connection, String json) {
        this.connection = connection;
        this.json = json;
    }

    public ConnectionsJson(HttpURLConnection connection) {
        this.connection = connection;
    }
}
