package main.java.com.lukyanovich.cloudbox.cli;

import java.util.Arrays;
import java.util.Optional;

public enum CLICommand {

    REGISTER("register", "Зарегистрировать нового пользователя", "регистрации"),
    LOGIN("login", "Вход в систему", "входа в систему"),
    LOGOUT("logout", "Выход из системы", "выхода из системы"),
    UPLOAD("upload", "Загрузить файл", "загрузки"),
    LIST("list", "Список файлов", "списка файлов"),
    DELETE("delete", "Удалить файл", "удаления"),
    DOWNLOAD("download", "Скачать файл", "скачивания"),
    RENAME("rename", "Переименовать файл", "переименования"),
    EXIT("exit", "Выход из программы", "выхода из программы");

    private final String command;
    private final String description;
    private final String shotDescription;

    CLICommand(String command, String description, String shotDescription) {
        this.command = command;
        this.description = description;
        this.shotDescription = shotDescription;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getShotDescription() {
        return shotDescription;
    }

    public static Optional<CLICommand> findByCommand(String input) {
        return Arrays.stream(CLICommand.values())
                .filter(cmd -> cmd.command.equalsIgnoreCase(input))
                .findFirst();
    }
}
