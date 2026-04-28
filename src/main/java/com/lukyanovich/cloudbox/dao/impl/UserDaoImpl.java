package main.java.com.lukyanovich.cloudbox.dao.impl;

import main.java.com.lukyanovich.cloudbox.dao.UserDao;
import main.java.com.lukyanovich.cloudbox.dao.util.UserSql;
import main.java.com.lukyanovich.cloudbox.dao.util.StatementUtil;
import main.java.com.lukyanovich.cloudbox.db.ConnectionPool;
import main.java.com.lukyanovich.cloudbox.exception.DaoException;
import main.java.com.lukyanovich.cloudbox.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserDaoImpl implements UserDao {

    private static final UserDao INSTANCE = new UserDaoImpl();

    private UserDaoImpl() {
    }

    @Override
    public User save(User user) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepare(
                     connection,
                     UserSql.SAVE_USER_SQL,
                     user.getUserName(), user.getPasswordHash())) {

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getLong(1));
                }
            }
            return user;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(connection,
                     UserSql.FIND_ALL_USERS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            return users;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при получении списка пользователей", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     UserSql.FIND_USER_BY_ID_SQL, id);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(buildUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске пользователя по id", e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     UserSql.UPDATE_USER_BY_ID_SQL,
                     user.getUserName(), user.getPasswordHash(), user.getId())) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     UserSql.DELETE_USER_BY_ID_SQL, id)) {

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении пользователя", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {

        if (username == null || username.isBlank()) {
            return false;
        }

        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     UserSql.EXISTS_BY_USERNAME_SQL, username.trim());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            return resultSet.next() && resultSet.getLong(1) > 0;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при проверке существования пользователя", e);
        }
    }

    public Optional<User> findByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = StatementUtil.prepareSimple(
                     connection,
                     UserSql.FIND_USER_BY_USERNAME_SQL,
                     username.trim());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return Optional.of(buildUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске пользователя по имени", e);
        }
    }

    private static User buildUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("user_name"),
                resultSet.getString("password_hash"));
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
