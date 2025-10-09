package com.shorokhov.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            configuration.addAnnotatedClass(com.shorokhov.entity.User.class);

            sessionFactory = configuration.buildSessionFactory();
            logger.info("SessionFactory created successfully");

        } catch (Exception e) {
            logger.error("Failed to create SessionFactory", e);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("SessionFactory closed");
        }
    }
}
