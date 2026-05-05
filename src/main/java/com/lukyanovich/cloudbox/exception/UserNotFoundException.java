package com.lukyanovich.cloudbox.exception;

public class UserNotFoundException extends UserServiceException {

    public UserNotFoundException(Long userId) {
        super("Пользователь с id " + userId + " не найден");
    }

    public UserNotFoundException(String userName) {
        super("Пользователь с именем '" + userName + "' не найден");
    }
}
