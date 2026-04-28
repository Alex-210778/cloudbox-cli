package main.java.com.lukyanovich.cloudbox.db.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();
    private static final String APPLICATION_PROPERTIES = "main/resources/application.properties";

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getRequired(String key) {
        String value = get(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException("Не найдено обязательное свойство: " + key);
        }
        return value;
    }

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream(APPLICATION_PROPERTIES)) {

            if (inputStream == null) {
                throw new RuntimeException("Файл " + APPLICATION_PROPERTIES + " не найден");
            }
            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла " + APPLICATION_PROPERTIES, e);
        }
    }
}
