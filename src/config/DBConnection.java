package config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Quản lý kết nối MySQL.
 *
 * - Thông tin kết nối đọc từ file db.properties (không commit lên Git),
 *   hoặc biến môi trường DB_URL / DB_USER / DB_PASSWORD.
 * - Có connection pool đơn giản: connection.close() không đóng kết nối thật
 *   mà trả lại pool để tái sử dụng, tránh phải mở TCP + handshake MySQL
 *   cho mỗi câu truy vấn.
 *
 * Code cũ dùng try-with-resources với DBConnection.open() vẫn chạy y nguyên.
 */
public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://127.0.0.1:3306/qlcuahangthucpham?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
    private static final int MAX_IDLE = 5;
    private static final int VALIDATE_TIMEOUT_SECONDS = 2;

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    private static final BlockingQueue<Connection> IDLE = new ArrayBlockingQueue<>(MAX_IDLE);

    static {
        Properties props = loadProperties();
        URL = pick("DB_URL", props.getProperty("db.url"), DEFAULT_URL);
        USER = pick("DB_USER", props.getProperty("db.user"), "root");
        PASSWORD = pick("DB_PASSWORD", props.getProperty("db.password"), "");

        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeAll));
    }

    private DBConnection() {
    }

    public static Connection open() {
        Connection real;
        while ((real = IDLE.poll()) != null) {
            if (isUsable(real)) {
                return wrap(real);
            }
            closeQuietly(real);
        }

        try {
            real = DriverManager.getConnection(URL, USER, PASSWORD);
            return wrap(real);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi kết nối DB (" + URL + ", user=" + USER + "). "
                    + "Kiểm tra MySQL đang chạy và file db.properties. Chi tiết: " + e.getMessage(), e);
        }
    }

    private static boolean isUsable(Connection c) {
        try {
            return !c.isClosed() && c.isValid(VALIDATE_TIMEOUT_SECONDS);
        } catch (SQLException e) {
            return false;
        }
    }

    /** Bọc connection thật để close() trả về pool thay vì đóng hẳn. */
    private static Connection wrap(Connection real) {
        InvocationHandler handler = new InvocationHandler() {
            private boolean returned = false;

            @Override
            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                String name = method.getName();

                if ("close".equals(name)) {
                    if (!returned) {
                        returned = true;
                        release(real);
                    }
                    return null;
                }
                if ("isClosed".equals(name)) {
                    return returned || real.isClosed();
                }
                if (returned) {
                    throw new SQLException("Connection đã được trả về pool.");
                }

                try {
                    return method.invoke(real, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        };

        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(), new Class<?>[]{Connection.class}, handler);
    }

    private static void release(Connection real) {
        try {
            if (real.isClosed()) {
                return;
            }
            if (!real.getAutoCommit()) {
                real.rollback();
                real.setAutoCommit(true);
            }
            if (!IDLE.offer(real)) {
                real.close();
            }
        } catch (SQLException e) {
            closeQuietly(real);
        }
    }

    private static void closeAll() {
        Connection c;
        while ((c = IDLE.poll()) != null) {
            closeQuietly(c);
        }
    }

    private static void closeQuietly(Connection c) {
        try {
            c.close();
        } catch (SQLException ignored) {
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        Path file = Paths.get("db.properties");
        if (Files.isRegularFile(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                props.load(in);
                return props;
            } catch (IOException e) {
                System.err.println("Không đọc được db.properties: " + e.getMessage());
            }
        }

        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Không đọc được db.properties trong classpath: " + e.getMessage());
        }
        return props;
    }

    private static String pick(String envName, String fileValue, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue.trim();
        }
        return defaultValue;
    }
}
