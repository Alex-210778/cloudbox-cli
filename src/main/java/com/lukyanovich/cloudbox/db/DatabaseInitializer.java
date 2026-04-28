package main.java.com.lukyanovich.cloudbox.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import main.java.com.lukyanovich.cloudbox.exception.DaoException;

public final class DatabaseInitializer {

    private static final String INIT_SCRIPT_PATH = "db/init.sql";

    private DatabaseInitializer() {
    }

    public static void init() {
        String sqlScript = readInitScript();

        try (Connection connection = ConnectionPool.get();
             Statement statement = connection.createStatement()) {

            for (String sql : sqlScript.split(";")) {
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }

        } catch (SQLException e) {
            throw new DaoException("Ошибка при инициализации базы данных", e);
        }
    }

    private static String readInitScript() {
        try (InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(INIT_SCRIPT_PATH)) {

            if (inputStream == null) {
                throw new DaoException("Файл " + INIT_SCRIPT_PATH + " не найден", null);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new DaoException("Ошибка при чтении файла " + INIT_SCRIPT_PATH, e);
        }
    }
}
