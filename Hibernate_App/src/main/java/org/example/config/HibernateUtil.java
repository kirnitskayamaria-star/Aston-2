package org.example.config;

import lombok.Getter;
import org.example.model.UserEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    @Getter
    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().addAnnotatedClass(UserEntity.class) .buildSessionFactory();
            logger.info("Соединение с PostgreSQL успешно настроено.");
        } catch (Throwable ex) {
            logger.error("Ошибка соединения с PostgreSQL.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Соединение с Hibernate закрыто.");
        }
    }
}
