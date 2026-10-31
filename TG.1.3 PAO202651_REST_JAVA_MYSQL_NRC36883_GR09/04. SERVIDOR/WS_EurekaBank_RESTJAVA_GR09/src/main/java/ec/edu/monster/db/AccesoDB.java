package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/** Creates JDBC connections using local defaults that can be overridden per environment. */
public final class AccesoDB {

    private AccesoDB() {
    }

    public static Connection getConnection() throws SQLException {
        ConnectionSettings settings = connectionSettings(System.getenv());
        try {
            // Payara may not discover JDBC drivers packaged inside WEB-INF/lib automatically.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL.", e);
        }
        return DriverManager.getConnection(settings.url(), settings.user(), settings.password());
    }

    static ConnectionSettings connectionSettings(Map<String, String> environment) {
        String host = valueOrDefault(environment, "DB_HOST", "localhost");
        String port = valueOrDefault(environment, "DB_PORT", "3306");
        String database = valueOrDefault(environment, "DB_NAME", "eurekabank");
        String user = valueOrDefault(environment, "DB_USER", "monster");
        String password = valueOrDefault(environment, "DB_PASSWORD", "monster9");
        String url = "jdbc:mysql://%s:%s/%s?sslMode=DISABLED".formatted(host, port, database);
        return new ConnectionSettings(url, user, password);
    }

    private static String valueOrDefault(Map<String, String> environment, String name, String defaultValue) {
        String value = environment.get(name);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    record ConnectionSettings(String url, String user, String password) {
    }
}
