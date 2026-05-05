package com.lukyanovich.cloudbox.service.impl;

import com.lukyanovich.cloudbox.dao.UserDao;
import com.lukyanovich.cloudbox.dao.impl.UserDaoImpl;
import com.lukyanovich.cloudbox.dto.UserLoginDto;
import com.lukyanovich.cloudbox.exception.UserAlreadyExistsException;
import com.lukyanovich.cloudbox.exception.UserNotFoundException;
import com.lukyanovich.cloudbox.exception.UserServiceException;
import com.lukyanovich.cloudbox.model.User;
import com.lukyanovich.cloudbox.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final UserService INSTANCE = new UserServiceImpl();
    private final UserDao userDao = UserDaoImpl.getInstance();

    public UserServiceImpl() {
    }

    @Override
    public User save(User user) {
        checkFields(user);

        if (existsByUsername(user.getUserName())) {
            throw new UserAlreadyExistsException(user.getUserName());
        }
        return userDao.save(user);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findById(long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public void update(User user) {
        checkFields(user);
        findById(user.getId());
        userDao.update(user);
    }

    @Override
    public boolean delete(long id) {
        findById(id);
        return userDao.delete(id);
    }

    @Override
    public Optional<User> login(UserLoginDto userLoginDto) {
        if (userLoginDto == null) {
            return Optional.empty();
        }

        String userName = userLoginDto.userName();
        String password = userLoginDto.password();

        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        return userDao.findByUsername(userName)
                .filter(user -> user.getPasswordHash().equals(password));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    private static void checkFields(User user) {
        if (user == null) {
            throw new UserServiceException("Пользователь не может быть равен null");
        }

        checkSingleField("Имя пользователя ", user.getUserName());
        checkSingleField("Пароль ", user.getPasswordHash());
    }

    private static void checkSingleField(String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isBlank()) {
            throw new UserServiceException(fieldName + " не может быть пустым");
        }
    }

    public static UserService getInstance() {
        return INSTANCE;
    }
}
