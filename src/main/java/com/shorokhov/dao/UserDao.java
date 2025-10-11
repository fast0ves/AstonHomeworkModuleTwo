package com.shorokhov.dao;

import com.shorokhov.entity.User;
import com.shorokhov.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final Session session;
    public UserDao() {
        this.session = HibernateUtil.getSessionFactory().openSession();
    }

    public User create(User user) {
            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
                logger.info("User saved successfully with ID: {}", user.getId());

                return user;

            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                logger.error("Error saving user", e);
                throw new RuntimeException("Failed to save user", e);
            }
        }

    public Optional<User> findById(Long id) {
        try {
            User user = session.get(User.class, id);

            return Optional.ofNullable(user);

        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Failed to find user by ID", e);
        }
    }

    public List<User> findAll() {
        try {
            Query<User> query = session.createQuery("FROM User", User.class);

            return query.list();

        } catch (Exception e) {
            logger.error("Error finding all users", e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }
    public User update(User user) {
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            logger.info("User updated successfully with ID: {}", user.getId());

            return user;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating user with ID: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;

        try  {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                logger.info("User deleted successfully with ID: {}", id);
            }
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting user with ID: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
