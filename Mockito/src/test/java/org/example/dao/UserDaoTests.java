package org.example.dao;

import org.example.model.UserEntity;
import org.example.config.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("user_service_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private UserDao userDao;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        System.setProperty("hibernate.hbm2ddl.auto", "update");
        System.setProperty("hibernate.show_sql", "true");
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE", Void.class).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("save: Успешное сохранение пользователя в контейнер")
    void save_Success() {
        UserEntity user = new UserEntity("Алексей", "alex@example.com", 25);

        UserEntity savedUser = userDao.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(1, savedUser.getId(), "Первый ID всегда равен 1");
        assertEquals("alex@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("save: Ошибка уникальности при дублировании Email")
    void save_DuplicateEmail_ThrowsException() {
        UserEntity user1 = new UserEntity("Дмитрий", "duplicate@example.com", 30);
        UserEntity user2 = new UserEntity("Сергей", "duplicate@example.com", 40);

        userDao.save(user1);
        assertThrows(Exception.class, () -> userDao.save(user2));
    }

    @Test
    @DisplayName("findById: Возвращает пустой Optional, если ID нет")
    void findById_NotFound_ReturnsEmpty() {
        Optional<UserEntity> foundUser = userDao.findById(999);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("findAll: Возвращает все существующие записи")
    void findAll_ReturnsAllRecords() {
        userDao.save(new UserEntity("User1", "user1@example.com", 20));
        userDao.save(new UserEntity("User2", "user2@example.com", 21));

        List<UserEntity> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("update: Изменение полей существующего пользователя")
    void update_ModifiesDataCorrectly() {
        UserEntity user = userDao.save(new UserEntity("Старое Имя", "update@example.com", 50));

        user.setName("Новое Имя");
        user.setAge(55);
        userDao.update(user);

        UserEntity updatedUser = userDao.findById(user.getId()).orElseThrow();
        assertEquals("Новое Имя", updatedUser.getName());
        assertEquals(55, updatedUser.getAge());
    }

    @Test
    @DisplayName("delete: Полное удаление сущности из базы по ID")
    void delete_RemovesUserFromDatabase() {
        UserEntity user = userDao.save(new UserEntity("Удаляемый", "delete@example.com", 19));
        Integer id = user.getId();

        userDao.delete(id);

        Optional<UserEntity> deletedUser = userDao.findById(id);
        assertTrue(deletedUser.isEmpty());
    }
}
