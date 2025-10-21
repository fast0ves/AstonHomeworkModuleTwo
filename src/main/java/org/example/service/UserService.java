package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.UserDaoImpl;
import org.example.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserService {

    private static final Logger logger = LogManager.getLogger();
    private UserDaoImpl userDao;

    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public User findUser(int id) {
        logger.info("Ищем пользователя!");
        return userDao.findById(id);
    }

    public void createUser(String name, String email, int age) {
        if (!name.isBlank() && !email.isBlank() && age > 0) {
            logger.info("Создание пользователя");
            LocalDateTime createdAt = LocalDateTime.now();
            User newUser = new User(name, email, age, createdAt.truncatedTo(ChronoUnit.MINUTES));
            userDao.create(newUser);
        } else {
            System.out.println("Неверно введены данные");
        }
    }

    public void updateUser(User user) {
        logger.info("Обновление пользователя");
        userDao.update(user);
    }

    public void deleteUser(User user) {
        logger.info("Удаление пользователя");
        userDao.delete(user);
    }

}