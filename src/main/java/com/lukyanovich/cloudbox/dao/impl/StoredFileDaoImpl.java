package com.lukyanovich.cloudbox.dao.impl;

import com.lukyanovich.cloudbox.dao.StoredFileDao;
import com.lukyanovich.cloudbox.dao.util.StoredFileSql;
import com.lukyanovich.cloudbox.dao.util.StatementUtil;
import com.lukyanovich.cloudbox.db.ConnectionPool;
import com.lukyanovich.cloudbox.exception.DaoException;
import com.lukyanovich.cloudbox.model.StoredFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StoredFileDaoImpl implements StoredFileDao {

    private static final StoredFileDao INSTANCE = new StoredFileDaoImpl();

    private StoredFileDaoImpl() {
    }

    @Override
    public StoredFile save(StoredFile storedFile) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepare(
                     connection,
                     StoredFileSql.SAVE_STORED_FILE_SQL,
                     storedFile.getFileName(),
                     storedFile.getPath(),
                     Timestamp.valueOf(storedFile.getUploadDate()),
                     storedFile.getUserId())) {

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    storedFile.setId(resultSet.getLong(1));
                }
            }

            return storedFile;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при сохранении файла", e);
        }
    }

    @Override
    public List<StoredFile> findAll() {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.FIND_ALL_STORED_FILES_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<StoredFile> storedFiles = new ArrayList<>();

            while (resultSet.next()) {
                storedFiles.add(buildStoredFile(resultSet));
            }
            return storedFiles;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при получении списка файлов", e);
        }
    }

    @Override
    public Optional<StoredFile> findById(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.FIND_STORED_FILE_BY_ID_SQL, id);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(buildStoredFile(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске файла по id", e);
        }
    }

    @Override
    public void update(StoredFile storedFile) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.UPDATE_STORED_FILE_BY_ID_SQL,
                     storedFile.getFileName(),
                     storedFile.getPath(),
                     Timestamp.valueOf(storedFile.getUploadDate()),
                     storedFile.getUserId(),
                     storedFile.getId())) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при обновлении файла", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.DELETE_STORED_FILE_BY_ID_SQL, id)) {

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении файла", e);
        }
    }

    @Override
    public List<StoredFile> findAllByUserId(Long userId) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.FIND_ALL_STORED_FILES_BY_USER_ID_SQL, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<StoredFile> storedFiles = new ArrayList<>();

            while (resultSet.next()) {
                storedFiles.add(buildStoredFile(resultSet));
            }
            return storedFiles;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при получении файлов пользователя", e);
        }
    }

    @Override
    public Optional<StoredFile> findByIdAndUserId(Long fileId, Long userId) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.FIND_STORED_FILE_BY_ID_AND_USER_ID_SQL,
                     fileId, userId);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(buildStoredFile(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске файла пользователя", e);
        }
    }

    @Override
    public boolean deleteByIdAndUserId(Long fileId, Long userId) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     StoredFileSql.DELETE_STORED_FILE_BY_ID_AND_USER_ID_SQL,
                     fileId, userId
             )) {

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении файла пользователя", e);
        }
    }

    private static StoredFile buildStoredFile(ResultSet resultSet) throws SQLException {
        return new StoredFile(
                resultSet.getLong("id"),
                resultSet.getString("file_name"),
                resultSet.getString("path"),
                resultSet.getTimestamp("upload_date").toLocalDateTime(),
                resultSet.getLong("user_id")
        );
    }

    public static StoredFileDao getInstance() {
        return INSTANCE;
    }
}
