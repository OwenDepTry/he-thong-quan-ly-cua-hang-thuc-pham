package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

<<<<<<< HEAD
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/qlcuahangthucpham?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
=======
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/QLCuaHangThucPham";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "ngaoda110";
>>>>>>> 3833e0a (update: fix hoa don + phieu nhap, them sua va chi tiet)

    private static final String URL = getEnvOrDefault("APP_DB_URL", DEFAULT_URL);
    private static final String USER = getEnvOrDefault("APP_DB_USER", DEFAULT_USER);
    private static final String PASSWORD = getEnvOrDefault("APP_DB_PASSWORD", DEFAULT_PASSWORD);

    public static Connection open() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}