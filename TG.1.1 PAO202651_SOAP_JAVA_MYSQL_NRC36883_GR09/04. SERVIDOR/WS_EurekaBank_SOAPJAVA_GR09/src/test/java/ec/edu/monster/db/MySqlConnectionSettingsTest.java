package ec.edu.monster.db;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySqlConnectionSettingsTest {

    @Test
    void usesMonsterCredentialsAndLocalMySqlDefaults() {
        AccesoDB.ConnectionSettings settings = AccesoDB.connectionSettings(Map.of());

        assertEquals("jdbc:mysql://localhost:3307/eurekabank?sslMode=DISABLED", settings.url());
        assertEquals("monster", settings.user());
        assertEquals("monster9", settings.password());
    }

    @Test
    void allowsConnectionValuesToBeOverriddenByEnvironment() {
        AccesoDB.ConnectionSettings settings = AccesoDB.connectionSettings(Map.of(
                "DB_HOST", "db.local",
                "DB_PORT", "3308",
                "DB_NAME", "bank_test",
                "DB_USER", "app_user",
                "DB_PASSWORD", "secret"
        ));

        assertEquals("jdbc:mysql://db.local:3308/bank_test?sslMode=DISABLED", settings.url());
        assertEquals("app_user", settings.user());
        assertEquals("secret", settings.password());
    }

    @Test
    void treatsBlankEnvironmentValuesAsDefaults() {
        AccesoDB.ConnectionSettings settings = AccesoDB.connectionSettings(Map.of(
                "DB_HOST", " ",
                "DB_PORT", "",
                "DB_NAME", " ",
                "DB_USER", "",
                "DB_PASSWORD", " "
        ));

        assertEquals("jdbc:mysql://localhost:3307/eurekabank?sslMode=DISABLED", settings.url());
        assertEquals("monster", settings.user());
        assertEquals("monster9", settings.password());
    }
}
