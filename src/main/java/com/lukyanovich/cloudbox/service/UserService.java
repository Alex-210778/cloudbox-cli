package com.lukyanovich.cloudbox.service;

import com.lukyanovich.cloudbox.dto.UserLoginDto;
import com.lukyanovich.cloudbox.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    List<User> findAll();

    User findById(long id);

    void update(User user);

    boolean delete(long id);

    Optional<User> login(UserLoginDto userLoginDto);

    boolean existsByUsername(String username);
}
