package org.example.dao;

import lombok.NonNull;
import org.example.config.HibernateUtil;
import org.example.model.UserEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public UserEntity save(@NonNull UserEntity user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь успешно сохранен: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback() && session.isOpen()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Не удалось откатить транзакцию", rollbackEx);
                }
            }

            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException
                    || e instanceof org.hibernate.exception.ConstraintViolationException) {
                logger.error("Ошибка: Пользователь с таким email уже существует!");
            } else {
                logger.error("Ошибка при сохранении пользователя", e);
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public Optional <UserEntity> findById(@NonNull Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserEntity user = session.get(UserEntity.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: {}", id, e);
            return Optional.empty();
        }
    }

    public List<UserEntity> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from UserEntity", UserEntity.class).list();
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей", e);
            return Collections.emptyList();
        }
    }

    public void update(@NonNull UserEntity user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Данные пользователя обновлены: ID {}", user.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при обновлении пользователя с ID: {}", user.getId(), e);
        }
    }

    public void delete(@NonNull Integer id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            UserEntity user = session.get(UserEntity.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь с ID {} удален.", id);
            } else {
                logger.warn("Пользователь с ID {} не найден.", id);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при удалении пользователя с ID: {}", id, e);
        }
    }
}
