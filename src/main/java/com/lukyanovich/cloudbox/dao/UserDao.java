package main.java.com.lukyanovich.cloudbox.dao;

import main.java.com.lukyanovich.cloudbox.model.User;
import java.util.Optional;

public interface UserDao extends Dao<Long, User> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
