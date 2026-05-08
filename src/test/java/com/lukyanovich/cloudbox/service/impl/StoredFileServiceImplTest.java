package com.lukyanovich.cloudbox.service.impl;

import com.lukyanovich.cloudbox.dao.StoredFileDao;
import com.lukyanovich.cloudbox.dto.UserLoginDto;
import com.lukyanovich.cloudbox.exception.StoredFileNotFoundException;
import com.lukyanovich.cloudbox.exception.StoredFileServiceException;
import com.lukyanovich.cloudbox.exception.UserAlreadyExistsException;
import com.lukyanovich.cloudbox.exception.UserNotFoundException;
import com.lukyanovich.cloudbox.exception.UserServiceException;
import com.lukyanovich.cloudbox.model.StoredFile;
import com.lukyanovich.cloudbox.model.User;
import com.lukyanovich.cloudbox.service.StoredFileService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class StoredFileServiceImplTest {
    private StoredFileDao storedFileDao;
    private StoredFileService storedFileService;

    @TempDir
    Path tempDir;

    private Path storagePath;
    private final String storageName = "storage";

    private final long fileId = 1L;
    private final long userId = 10L;
    private final String fileName = "test.txt";

    private StoredFile storedFile;

    @BeforeEach
    void init() throws IOException {
        storedFileDao = mock(StoredFileDao.class);

        storagePath = tempDir.resolve(storageName);
        Files.createDirectories(storagePath);

        storedFileService = new StoredFileServiceImpl(storedFileDao, storagePath);

        storedFile = new StoredFile(
                fileId,
                fileName,
                storagePath.resolve(fileName).toString(),
                LocalDateTime.now(),
                userId
        );
    }

    @Test
    void saveFileTest() {
        when(storedFileDao.save(storedFile)).thenReturn(storedFile);

        StoredFile savedFile = storedFileService.save(storedFile);

        assertEquals(storedFile, savedFile);
        assertEquals(fileId, savedFile.getId());
        assertEquals(fileName, savedFile.getFileName());
        assertEquals(userId, savedFile.getUserId());

        verify(storedFileDao).save(storedFile);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void saveFileThrowExceptionWhenStoredFileIsNullTest() {
        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(null);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void saveFileThrowExceptionWhenFileNameIsNullTest() {
        storedFile.setFileName(null);

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(storedFile);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void saveFileThrowExceptionWhenFileNameIsBlankTest() {
        storedFile.setFileName("   ");

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(storedFile);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void saveFileShouldThrowExceptionWhenPathIsNullTest() {
        storedFile.setPath(null);

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(storedFile);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void saveFileThrowExceptionWhenPathIsBlankTest() {
        storedFile.setPath("   ");

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(storedFile);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void saveFileThrowExceptionWhenUserIdIsInvalidTest() {
        storedFile.setUserId(0L);

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.save(storedFile);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void findAllTest() {
        StoredFile secondFile = new StoredFile(
                2L,
                "second.txt",
                storagePath.resolve("second.txt").toString(),
                LocalDateTime.now(),
                userId
        );

        List<StoredFile> files = List.of(storedFile, secondFile);

        when(storedFileDao.findAll()).thenReturn(files);

        List<StoredFile> allFiles = storedFileService.findAll();

        assertEquals(files, allFiles);
        assertEquals(files.size(), allFiles.size());

        verify(storedFileDao).findAll();
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findAllShouldReturnEmptyListTest() {
        when(storedFileDao.findAll()).thenReturn(List.of());

        List<StoredFile> allFiles = storedFileService.findAll();

        assertTrue(allFiles.isEmpty());

        verify(storedFileDao).findAll();
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findAllByUserIdTest() {
        List<StoredFile> files = List.of(storedFile);

        when(storedFileDao.findAllByUserId(userId)).thenReturn(files);

        List<StoredFile> userFiles = storedFileService.findAllByUserId(userId);

        assertEquals(files, userFiles);
        assertEquals(1, userFiles.size());

        verify(storedFileDao).findAllByUserId(userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findByIdTest() {
        when(storedFileDao.findById(fileId)).thenReturn(Optional.of(storedFile));

        StoredFile fileById = storedFileService.findById(fileId);

        assertEquals(storedFile, fileById);
        assertEquals(fileId, fileById.getId());

        verify(storedFileDao).findById(fileId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findByIdShouldThrowExceptionWhenFileNotFoundTest() {
        when(storedFileDao.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.findById(fileId);
        });

        verify(storedFileDao).findById(fileId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findByIdAndUserIdTest() {
        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.of(storedFile));

        StoredFile fileByIdAndUserId = storedFileService.findByIdAndUserId(fileId, userId);

        assertEquals(storedFile, fileByIdAndUserId);
        assertEquals(userId, fileByIdAndUserId.getUserId());

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void findByIdAndUserIdShouldThrowExceptionWhenFileNotFoundTest() {
        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.findByIdAndUserId(fileId, userId);
        });

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void updateTest() {
        when(storedFileDao.findById(fileId)).thenReturn(Optional.of(storedFile));

        storedFileService.update(storedFile);

        verify(storedFileDao).findById(fileId);
        verify(storedFileDao).update(storedFile);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void updateShouldThrowExceptionWhenStoredFileIsNullTest() {
        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.update(null);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void updateShouldThrowExceptionWhenFileNotFoundTest() {
        when(storedFileDao.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.update(storedFile);
        });

        verify(storedFileDao).findById(fileId);
        verify(storedFileDao, never()).update(any());
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void deleteTest() throws IOException {
        Path filePath = storagePath.resolve(fileName);
        Files.writeString(filePath, "test content", CREATE);

        storedFile.setPath(filePath.toString());

        when(storedFileDao.findById(fileId)).thenReturn(Optional.of(storedFile));
        when(storedFileDao.delete(fileId)).thenReturn(true);

        boolean isDeleted = storedFileService.delete(fileId);

        assertTrue(isDeleted);
        assertFalse(Files.exists(filePath));

        verify(storedFileDao).findById(fileId);
        verify(storedFileDao).delete(fileId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void deleteShouldReturnFalseWhenDaoReturnsFalseTest() throws IOException {
        Path filePath = storagePath.resolve(fileName);
        Files.writeString(filePath, "test content", CREATE);

        storedFile.setPath(filePath.toString());

        when(storedFileDao.findById(fileId)).thenReturn(Optional.of(storedFile));
        when(storedFileDao.delete(fileId)).thenReturn(false);

        boolean isDeleted = storedFileService.delete(fileId);

        assertFalse(isDeleted);
        assertFalse(Files.exists(filePath));

        verify(storedFileDao).findById(fileId);
        verify(storedFileDao).delete(fileId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void deleteShouldThrowExceptionWhenFileNotFoundInDatabaseTest() {
        when(storedFileDao.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.delete(fileId);
        });

        verify(storedFileDao).findById(fileId);
        verify(storedFileDao, never()).delete(anyLong());
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void deleteByUserIdTest() throws IOException {
        Path filePath = storagePath.resolve(fileName);
        Files.writeString(filePath, "test content", CREATE);

        storedFile.setPath(filePath.toString());

        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.of(storedFile));
        when(storedFileDao.deleteByIdAndUserId(fileId, userId)).thenReturn(true);

        boolean isDeleted = storedFileService.delete(fileId, userId);

        assertTrue(isDeleted);
        assertFalse(Files.exists(filePath));

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verify(storedFileDao).deleteByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void deleteByUserIdShouldThrowExceptionWhenFileNotFoundTest() {
        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.delete(fileId, userId);
        });

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verify(storedFileDao, never()).deleteByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void uploadTest() throws IOException {
        Path sourceDirectory = tempDir.resolve("source");
        Files.createDirectories(sourceDirectory);

        Path sourceFile = sourceDirectory.resolve(fileName);
        Files.writeString(sourceFile, "test content", CREATE);

        when(storedFileDao.save(any(StoredFile.class))).thenAnswer(invocation -> {
            StoredFile file = invocation.getArgument(0);
            file.setId(fileId);
            return file;
        });

        StoredFile uploadedFile = storedFileService.upload(sourceFile.toString(), userId);

        Path expectedTargetPath = storagePath.resolve(fileName);

        assertEquals(fileId, uploadedFile.getId());
        assertEquals(fileName, uploadedFile.getFileName());
        assertEquals(userId, uploadedFile.getUserId());
        assertEquals(expectedTargetPath.toString(), uploadedFile.getPath());
        assertTrue(Files.exists(expectedTargetPath));

        ArgumentCaptor<StoredFile> captor = ArgumentCaptor.forClass(StoredFile.class);
        verify(storedFileDao).save(captor.capture());

        StoredFile savedMetadata = captor.getValue();

        assertEquals(fileName, savedMetadata.getFileName());
        assertEquals(expectedTargetPath.toString(), savedMetadata.getPath());
        assertEquals(userId, savedMetadata.getUserId());

        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void uploadShouldThrowExceptionWhenSourcePathIsBlankTest() {
        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.upload("   ", userId);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void uploadShouldThrowExceptionWhenSourceFileDoesNotExistTest() {
        Path missingFile = tempDir.resolve("missing.txt");

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.upload(missingFile.toString(), userId);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void uploadShouldThrowExceptionWhenSourcePathIsDirectoryTest() throws IOException {
        Path directory = tempDir.resolve("directory");
        Files.createDirectories(directory);

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.upload(directory.toString(), userId);
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void downloadTest() throws IOException {
        Path sourceFile = storagePath.resolve(fileName);
        Files.writeString(sourceFile, "test content", CREATE);

        storedFile.setPath(sourceFile.toString());

        Path targetDirectory = tempDir.resolve("downloads");

        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.of(storedFile));

        storedFileService.download(fileId, userId, targetDirectory.toString());

        Path downloadedFile = targetDirectory.resolve(fileName);

        assertTrue(Files.exists(downloadedFile));
        assertEquals("test content", Files.readString(downloadedFile));

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void downloadShouldThrowExceptionWhenTargetPathIsBlankTest() {
        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.download(fileId, userId, "   ");
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void downloadShouldThrowExceptionWhenFileNotFoundInDatabaseTest() {
        Path targetDirectory = tempDir.resolve("downloads");

        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.download(fileId, userId, targetDirectory.toString());
        });

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void downloadShouldThrowExceptionWhenPhysicalFileDoesNotExistTest() {
        Path missingFile = storagePath.resolve(fileName);
        storedFile.setPath(missingFile.toString());

        Path targetDirectory = tempDir.resolve("downloads");

        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.of(storedFile));

        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.download(fileId, userId, targetDirectory.toString());
        });

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void renameTest() throws IOException {
        Path oldPath = storagePath.resolve(fileName);
        Files.writeString(oldPath, "test content", CREATE);

        storedFile.setPath(oldPath.toString());

        String newName = "renamed.txt";

        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.of(storedFile));

        storedFileService.rename(fileId, userId, newName);

        Path newPath = storagePath.resolve(newName);

        assertFalse(Files.exists(oldPath));
        assertTrue(Files.exists(newPath));
        assertEquals(newName, storedFile.getFileName());
        assertEquals(newPath.toString(), storedFile.getPath());

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verify(storedFileDao).update(storedFile);
        verifyNoMoreInteractions(storedFileDao);
    }

    @Test
    void renameShouldThrowExceptionWhenNewNameIsBlankTest() {
        assertThrows(StoredFileServiceException.class, () -> {
            storedFileService.rename(fileId, userId, "   ");
        });

        verifyNoInteractions(storedFileDao);
    }

    @Test
    void renameShouldThrowExceptionWhenFileNotFoundInDatabaseTest() {
        when(storedFileDao.findByIdAndUserId(fileId, userId)).thenReturn(Optional.empty());

        assertThrows(StoredFileNotFoundException.class, () -> {
            storedFileService.rename(fileId, userId, "renamed.txt");
        });

        verify(storedFileDao).findByIdAndUserId(fileId, userId);
        verify(storedFileDao, never()).update(any());
        verifyNoMoreInteractions(storedFileDao);
    }
}
