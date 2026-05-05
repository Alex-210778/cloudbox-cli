package com.lukyanovich.cloudbox.cli;

public final class CLIMessages {

    public static final String INCORRECT_VALUE = "Некорректное значение. Повторите ввод.";
    public static final String ENTER_VALUE = "Введите число:";
    public static final String EMPTY_NOT_ALLOWED = "Значение не может быть пустым.";
    public static final String WELCOME_TO_APP = "Добро пожаловать в CloudBox CLI!";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда: ";
    public static final String USER_NAME = "Имя пользователя: ";
    public static final String PASSWORD = "Пароль: ";
    public static final String REGISTRATION_SUCCESS = "Регистрация успешна. ID: ";
    public static final String LOGGED_IN_AS = "Вы вошли как: ";
    public static final String WRONG_USERNAME_OR_PASSWORD = "Неверные имя пользователя или пароль";
    public static final String LOGOUT_MESSAGE = "Вы вышли из аккаунта.";
    public static final String LOG_IN_FIRST = "Сначала выполните вход (login).";
    public static final String ENTER_THE_PATH_TO_DOWNLOADED_FILE = "Введите путь к загружаемому файлу: ";
    public static final String ENTER_THE_PATH_TO_FILE = "Введите путь для %s файла: ";
    public static final String WRONG_PATH_TO_FILE = "Путь не может быть пустым.";
    public static final String WRONG_FILE_NAME = "Имя не может быть пустым.";
    public static final String FILE_UPLOADED = "Файл загружен";
    public static final String FILES_NOT_FOUND = "У пользователя %s файлы не найдены%n";
    public static final String NO_FILES = "У вас нет файлов для %s .%n";
    public static final String FILES_FOR = "Файлы для ";
    public static final String ENTER_NUMBER_OF_FILE = "Введите номер файла для ";
    public static final String ENTER_NEW_FILE_NAME = "Введите новое имя для файла ";
    public static final String FILE_DELETED = "Файл удалён.";
    public static final String FILE_NOT_DELETED = "Файл не удалён.";
    public static final String GOODBYE = "До свидания!";
    public static final String FILE_DOWNLOADED = "Файл %s скачан.%n";
    public static final String DOWNLOAD_ERROR = "Ошибка при скачивании файла: %s%n";
    public static final String FILE_RENAMED = "Файл переименован в %s%n";
    public static final String RENAME_ERROR = "Ошибка при переименовании файла: %s%n";
    public static final String DELETE_ERROR = "Ошибка при удалении файла: %s%n";
    public static final String COMMAND_HANDLER_NOT_FOUND = "Предупреждение: для команды '%s' не назначен обработчик.%n";
    public static final String COMMAND_NOT_IMPLEMENTED = "Команда '%s' пока не реализована.%n";

    private CLIMessages() {
    }

}
