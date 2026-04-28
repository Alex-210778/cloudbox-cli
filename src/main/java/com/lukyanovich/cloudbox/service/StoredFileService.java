package main.java.com.lukyanovich.cloudbox.service;

import main.java.com.lukyanovich.cloudbox.model.StoredFile;

import java.io.IOException;
import java.util.List;

public interface StoredFileService {

    /**
     * Сохраняет файл в базу данных
     */
    StoredFile save(StoredFile storedFile);

    /**
     * Возвращает список всех файлов
     */
    List<StoredFile> findAll();


    /**
     * Возвращает список всех файлов, принадлежащих конкретному пользователю.
     */
    List<StoredFile> findAllByUserId(long userId);

    /**
     * Находит файл по его ID.
     */
    StoredFile findById(long id);

    /**
     * Находит файл по его ID, принадлежащий конкретному пользователю.
     */
    StoredFile findByIdAndUserId(long fileId, long userId);

    /**
     * Обновляет имя файла (переименование).
     */
    void update(StoredFile storedFile);

    /**
     * Удаляет файл из базы данных (и, возможно, из файловой системы).
     */
    boolean delete(long id);

    /**
     * Удаляет файл у конкретного пользователя.
     */
    boolean delete(long id, long userId);

    /**
     * Загружает файл: копирует в хранилище и сохраняет мета-данные.
     */
    StoredFile upload(String sourcePath, long userId);

    /**
     * Скачивает файл: копирует его из хранилища в указанный путь.
     */
    void download(long fileId, long userId, String targetPath) throws IOException;

    /**
     * Переименовывает файл по ID.
     */
    void rename(long fileId, long userId, String newName);
}
