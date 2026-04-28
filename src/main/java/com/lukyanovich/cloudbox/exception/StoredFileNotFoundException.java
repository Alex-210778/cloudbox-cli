package main.java.com.lukyanovich.cloudbox.exception;

public class StoredFileNotFoundException extends StoredFileServiceException {

    public StoredFileNotFoundException(Long fileId) {
        super("Файл с id " + fileId + " не найден");
    }

    public StoredFileNotFoundException(Long fileId, Long userId) {
        super("Файл с id " + fileId + " для пользователя с id " + userId + " не найден");
    }
}
