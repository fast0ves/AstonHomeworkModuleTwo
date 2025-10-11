package com.shorokhov.service;

import com.shorokhov.dao.UserDao;
import com.shorokhov.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public User createUser(String name, Integer age) {
        logger.info("Creating new user: {}, {}", name, age);

        User user = new User(name, age);
        return userDao.create(user);
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Getting user by ID: {}", id);
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        logger.info("Getting all users");
        return userDao.findAll();
    }

    public User updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());
        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        userDao.delete(id);
    }
}
