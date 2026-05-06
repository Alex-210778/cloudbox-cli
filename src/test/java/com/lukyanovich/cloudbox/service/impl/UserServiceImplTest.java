package com.lukyanovich.cloudbox.service.impl;

import com.lukyanovich.cloudbox.dao.UserDao;
import com.lukyanovich.cloudbox.dto.UserLoginDto;
import com.lukyanovich.cloudbox.exception.UserAlreadyExistsException;
import com.lukyanovich.cloudbox.exception.UserNotFoundException;
import com.lukyanovich.cloudbox.exception.UserServiceException;
import com.lukyanovich.cloudbox.model.User;
import com.lukyanovich.cloudbox.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserDao userDao;
    private UserService userService;

    private final long id = 1L;
    private final String userName = "alex";
    private final String password = "pass";
    private User user;

    @BeforeEach
    void init() {
        userDao = mock(UserDao.class);
        userService = new UserServiceImpl(userDao);

        user = new User(id, userName, password);
    }

    @Test
    void saveUserTest() {
        when(userDao.existsByUsername(userName)).thenReturn(false);
        when(userDao.save(user)).thenReturn(new User(id, userName, password));

        User savedUser = userService.save(user);

        assertEquals(id, savedUser.getId());
        assertEquals(userName, savedUser.getUserName());
        assertEquals(password, savedUser.getPasswordHash());

        verify(userDao).existsByUsername(userName);
        verify(userDao).save(user);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void saveUserThrowExceptionWhenUserIsNullTest() {
        assertThrows(UserServiceException.class, () -> {
            userService.save(null);
        });

        verifyNoInteractions(userDao);
    }

    @Test
    void saveUserThrowExceptionWhenUserNameIsNullTest() {
        user.setUserName(null);

        assertThrows(UserServiceException.class, () -> {
            userService.save(user);
        });

        verifyNoInteractions(userDao);
    }

    @Test
    void saveUserThrowExceptionWhenUserPasswordIsNullTest() {
        user.setPasswordHash(null);

        assertThrows(UserServiceException.class, () -> {
            userService.save(user);
        });

        verifyNoInteractions(userDao);
    }

    @Test
    void saveUserThrowExceptionWhenUserNameAlreadyExistsTest() {
        User newUser = new User(userName, password);

        when(userDao.existsByUsername(userName)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.save(newUser);
        });

        verify(userDao).existsByUsername(userName);
        verify(userDao, never()).save(any());
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void findAllTest() {
        user.setId(id);
        User secondUser = new User(2L, "Ivan", "password");
        List<User> users = List.of(user, secondUser);

        when(userDao.findAll()).thenReturn(users);

        List<User> allUsers = userService.findAll();

        assertEquals(users, allUsers);
        assertEquals(users.size(), allUsers.size());

        verify(userDao).findAll();
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void findByIdTest() {
        when(userDao.findById(id)).thenReturn(Optional.of(user));

        User userById = userService.findById(id);

        assertEquals(user, userById);
        assertEquals(user.getId(), userById.getId());

        verify(userDao).findById(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void findByIdThrowExceptionWhenUserNotFoundTest() {
        when(userDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(id);
        });

        verify(userDao).findById(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void updateTest() {
        User updatedUser = new User(id, "alex-updated", "new-pass");

        when(userDao.findById(id)).thenReturn(Optional.of(user));

        userService.update(updatedUser);

        verify(userDao).findById(id);
        verify(userDao).update(updatedUser);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteUserTest() {
        when(userDao.findById(id)).thenReturn(Optional.of(user));
        when(userDao.delete(id)).thenReturn(true);

        boolean isDelete = userService.delete(id);

        assertTrue(isDelete);

        verify(userDao).findById(id);
        verify(userDao).delete(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void deleteShouldReturnFalseWhenDaoReturnsFalseTest() {
        when(userDao.findById(id)).thenReturn(Optional.of(user));
        when(userDao.delete(id)).thenReturn(false);

        boolean isDelete = userService.delete(id);

        assertFalse(isDelete);

        verify(userDao).findById(id);
        verify(userDao).delete(id);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void loginTest() {
        UserLoginDto loginDto = new UserLoginDto(userName, password);

        when(userDao.findByUsername(loginDto.userName())).thenReturn(Optional.of(user));

        Optional<User> loginUser = userService.login(loginDto);

        assertTrue(loginUser.isPresent());
        assertEquals(user.getUserName(), loginUser.get().getUserName());

        verify(userDao).findByUsername(userName);
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void loginShouldReturnEmptyOptionalWhenUserLoginDtoIsNullTest() {
        Optional<User> loginUser = userService.login(null);

        assertTrue(loginUser.isEmpty());

        verifyNoInteractions(userDao);
    }

    @Test
    void loginShouldReturnEmptyOptionalWhenUserNameIsNullTest() {
        UserLoginDto loginDto = new UserLoginDto(null, password);

        Optional<User> loginUser = userService.login(loginDto);

        assertTrue(loginUser.isEmpty());

        verifyNoInteractions(userDao);
    }
}
