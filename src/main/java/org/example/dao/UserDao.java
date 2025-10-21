package org.example.dao;

import org.example.entity.User;

public interface UserDao {
    User create(User user);

    User findById(int userId);

    User update(User user);

    void delete(User user);
}
