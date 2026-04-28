package main.java.com.lukyanovich.cloudbox.db;

import main.java.com.lukyanovich.cloudbox.db.util.PropertiesUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool {

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";

    private static final int DEFAULT_POOL_SIZE = 10;

    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String CLOSE_METHOD_NAME = "close";

    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;

    static {
        loadDriver();
        initConnectionPool();
    }

    private ConnectionPool() {
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Поток был прерван при получении соединения из пула", e);
        }
    }

    public static void closePool() {
        try {
            for (Connection sourceConnection : sourceConnections) {
                sourceConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при закрытии пула соединений", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC-драйвер PostgreSQL не найден", e);
        }
    }

    private static void initConnectionPool() {
        int poolSize = getPoolSize();

        pool = new ArrayBlockingQueue<>(poolSize);
        sourceConnections = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            Connection sourceConnection = open();
            Connection proxyConnection = createProxyConnection(sourceConnection);

            pool.add(proxyConnection);
            sourceConnections.add(sourceConnection);
        }
    }

    private static Connection createProxyConnection(Connection sourceConnection) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if (CLOSE_METHOD_NAME.equals(method.getName())) {
                        returnConnectionToPool((Connection) proxy, sourceConnection);
                        return null;
                    }
                    return method.invoke(sourceConnection, args);
                }
        );
    }

    private static void returnConnectionToPool(Connection proxyConnection, Connection sourceConnection) throws SQLException {
        if (!sourceConnection.getAutoCommit()) {
            sourceConnection.rollback();
            sourceConnection.setAutoCommit(true);
        }
        pool.add(proxyConnection);
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при открытии соединения с базой данных", e);
        }
    }

    private static int getPoolSize() {
        String size = PropertiesUtil.get(POOL_SIZE_KEY);

        if (size == null || size.isBlank()) {
            return DEFAULT_POOL_SIZE;
        }

        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Некорректное значение размера пула соединений: " + size, e);
        }
    }
}
