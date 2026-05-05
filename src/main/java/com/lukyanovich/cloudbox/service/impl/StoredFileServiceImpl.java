package com.lukyanovich.cloudbox.service.impl;

import com.lukyanovich.cloudbox.dao.StoredFileDao;
import com.lukyanovich.cloudbox.dao.impl.StoredFileDaoImpl;
import com.lukyanovich.cloudbox.db.util.PropertiesUtil;
import com.lukyanovich.cloudbox.exception.StoredFileNotFoundException;
import com.lukyanovich.cloudbox.exception.StoredFileServiceException;
import com.lukyanovich.cloudbox.model.StoredFile;
import com.lukyanovich.cloudbox.service.StoredFileService;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.file.StandardCopyOption.*;

public class StoredFileServiceImpl implements StoredFileService {

    private static final StoredFileService INSTANCE = new StoredFileServiceImpl();

    private static final String DEFAULT_PATH = "data/storage";
    private static final String PATH_KEY = "storage.path";
    private final Path storagePath;

    private final StoredFileDao storedFileDao = StoredFileDaoImpl.getInstance();

    private StoredFileServiceImpl() {
        this.storagePath = initStoragePath();
    }

    @Override
    public StoredFile save(StoredFile storedFile) {
        checkFields(storedFile);
        return storedFileDao.save(storedFile);
    }

    @Override
    public List<StoredFile> findAll() {
        return storedFileDao.findAll();
    }

    @Override
    public List<StoredFile> findAllByUserId(long userId) {
        return storedFileDao.findAllByUserId(userId);
    }

    @Override
    public StoredFile findById(long id) {
        return storedFileDao.findById(id)
                .orElseThrow(() -> new StoredFileNotFoundException(id));
    }

    @Override
    public StoredFile findByIdAndUserId(long fileId, long userId) {
        return storedFileDao.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new StoredFileNotFoundException(fileId, userId));
    }

    @Override
    public void update(StoredFile storedFile) {
        checkFields(storedFile);
        findById(storedFile.getId());
        storedFileDao.update(storedFile);
    }

    @Override
    public boolean delete(long id) {
        StoredFile storedFile = findById(id);
        deletePhysicalFile(storedFile);

        return storedFileDao.delete(id);
    }

    @Override
    public boolean delete(long fileId, long userId) {
        StoredFile storedFile = findByIdAndUserId(fileId, userId);
        deletePhysicalFile(storedFile);

        return storedFileDao.deleteByIdAndUserId(fileId, userId);
    }

    @Override
    public StoredFile upload(String sourcePathString, long userId) {
        checkSingleField("Путь к файлу ", sourcePathString);

        Path sourcePath = Path.of(sourcePathString);

        if (!Files.exists(sourcePath)) {
            throw new StoredFileServiceException("Файл не найден: " + sourcePathString);
        }

        if (!Files.isRegularFile(sourcePath)) {
            throw new StoredFileServiceException("Указанный путь не является файлом: " + sourcePathString);
        }

        String fileName = sourcePath.getFileName().toString();
        Path targetPath = storagePath.resolve(fileName);

        try {
            Files.copy(sourcePath, targetPath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StoredFileServiceException("Ошибка при копировании файла в хранилище" + e.getMessage());
        }

        StoredFile storedFile = new StoredFile(
                fileName,
                targetPath.toString(),
                LocalDateTime.now(),
                userId
        );

        return storedFileDao.save(storedFile);
    }

    @Override
    public void download(long fileId, long userId, String targetDirectoryPathString) throws IOException {
        checkSingleField("Путь для скачивания файла ", targetDirectoryPathString);

        StoredFile storedFile = findByIdAndUserId(fileId, userId);

        Path sourcePath = Path.of(storedFile.getPath());
        Path targetDirectory = Path.of(targetDirectoryPathString);

        if (!Files.exists(sourcePath)) {
            throw new StoredFileServiceException("Файл в хранилище не найден: " + sourcePath);
        }

        if (!Files.isRegularFile(sourcePath)) {
            throw new StoredFileServiceException("Путь в хранилище не является файлом: " + sourcePath);
        }

        try {
            Files.createDirectories(targetDirectory);

            String fileName = storedFile.getFileName();
            Path targetPathFile = targetDirectory.resolve(fileName);
            Files.copy(sourcePath, targetPathFile, REPLACE_EXISTING);
        } catch (FileSystemException e) {
            throw new StoredFileServiceException(
                    "Файл используется другой программой. Закройте его и попробуйте снова.", e);
        } catch (IOException e) {
            throw new StoredFileServiceException("Ошибка при скачивании файла", e);
        }
    }

    @Override
    public void rename(long fileId, long userId, String newName) {
        checkSingleField("Новое имя файла", newName);

        StoredFile storedFile = findByIdAndUserId(fileId, userId);

        Path oldPath = Path.of(storedFile.getPath());
        Path newPath = oldPath.resolveSibling(newName.trim());

        try {
            Files.move(oldPath, newPath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StoredFileServiceException("Ошибка при переименовании файла", e);
        }

        storedFile.setFileName(newName);
        storedFile.setPath(newPath.toString());

        storedFileDao.update(storedFile);
    }

    private Path initStoragePath() {
        String path = PropertiesUtil.get(PATH_KEY);

        Path resultPath = path == null || path.isBlank()
                ? Path.of(DEFAULT_PATH)
                : Path.of(path);

        try {
            Files.createDirectories(resultPath);
            return resultPath;
        } catch (IOException e) {
            throw new StoredFileServiceException("Не удалось создать директорию хранилища: " + resultPath, e);
        }
    }

    private void deletePhysicalFile(StoredFile storedFile) {
        Path path = Path.of(storedFile.getPath());

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new StoredFileServiceException("Ошибка при удалении файла из хранилища", e);
        }
    }

    private static void checkFields(StoredFile storedFile) {
        if (storedFile == null) {
            throw new StoredFileServiceException("Файл не может быть равен null");
        }

        checkSingleField("Имя файла ", storedFile.getFileName());
        checkSingleField("Путь к файлу ", storedFile.getPath());

        if (storedFile.getUserId() <= 0) {
            throw new StoredFileServiceException("Id пользователя должен быть больше 0");
        }
    }

    private static void checkSingleField(String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) {
            throw new StoredFileServiceException(fieldName + " не может быть пустым");
        }
    }

    public static StoredFileService getInstance() {
        return INSTANCE;
    }
}
