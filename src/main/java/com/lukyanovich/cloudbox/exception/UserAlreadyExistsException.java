package com.lukyanovich.cloudbox.exception;

public class UserAlreadyExistsException extends UserServiceException {

    public UserAlreadyExistsException(String userName) {
        super("Пользователь с именем '" + userName + "' уже существует");
    }
}
