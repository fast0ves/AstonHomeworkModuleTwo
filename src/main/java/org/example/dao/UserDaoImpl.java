package org.example.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.User;
import org.example.util.SessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger();
    private final SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

    @Override
    public User findById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, userId);
        } catch (HibernateException e) {
            logger.error(e);
            throw new HibernateException("Ошибка операции с БД");
        }
    }

    @Override
    public User create(User user) {
        Transaction createTransaction = null;
        try (Session session = sessionFactory.openSession()) {
            createTransaction = session.beginTransaction();
            session.persist(user);
            createTransaction.commit();
            return user;
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getMessage());
        } catch (HibernateException e) {
            if (createTransaction != null) createTransaction.rollback();
            logger.error(e);
            throw new HibernateException("Ошибка операции с БД");
        }
    }


    @Override
    public User update(User user) {
        Transaction updateTransaction = null;
        try (Session session = sessionFactory.openSession()) {
            updateTransaction = session.beginTransaction();
            session.merge(user);
            updateTransaction.commit();
            return user;
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getMessage());
        } catch (HibernateException e) {
            if (updateTransaction != null) updateTransaction.rollback();
            logger.error(e);
            throw new HibernateException("Ошибка операции с БД");
        }
    }

    @Override
    public void delete(User user) {
        Transaction deleteTransaction = null;
        try (Session session = sessionFactory.openSession()) {
            deleteTransaction = session.beginTransaction();
            session.remove(user);
            deleteTransaction.commit();
        } catch (NullPointerException e) {
            logger.error(e);
            throw new NullPointerException(e.getMessage());
        } catch (HibernateException e) {
            if (deleteTransaction != null) deleteTransaction.rollback();
            logger.error(e);
            throw new HibernateException("Ошибка операции с БД");
        }
    }
}