package org.example.service;

import org.example.dao.UserDao;
import org.example.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    private void initServiceWithInput(String inputData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
        Scanner mockScanner = new Scanner(inputStream);
        this.userService = new UserService(mockScanner);


        try {
            java.lang.reflect.Field daoField = UserService.class.getDeclaredField("userDao");
            daoField.setAccessible(true);
            daoField.set(this.userService, userDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Успешное создание пользователя через DTO")
    void createUser_Success() {
        initServiceWithInput("Иван\nivan@mail.ru\n25\n");

        UserEntity savedEntity = new UserEntity("Иван", "ivan@mail.ru", 25);
        savedEntity.setId(1);
        when(userDao.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserEntity expectedUser = new UserEntity("Иван", "ivan@mail.ru", 25);

        userService.createUser();

        verify(userDao, times(1)).save(eq(expectedUser));
    }


    @Test
    @DisplayName("Неудачное создание пользователя при пустом имени")
    void createUser_ValidationFailed_BlankName() {
        initServiceWithInput("   \nivan@mail.ru\n25\n");

        userService.createUser();

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Успешный поиск пользователя по ID через Optional")
    void readUser_Found() {
        initServiceWithInput("1\n");
        UserEntity user = new UserEntity("Анна", "anna@mail.ru", 20);
        user.setId(1);

        when(userDao.findById(1)).thenReturn(Optional.of(user));

        userService.readUser();
        verify(userDao, times(1)).findById(1);
    }

    @Test
    @DisplayName("Поиск по ID завершился уведомлением, если Optional пуст")
    void readUser_NotFound() {
        initServiceWithInput("99\n");
        when(userDao.findById(99)).thenReturn(Optional.empty());

        userService.readUser();

        verify(userDao, times(1)).findById(99);
    }


    @Test
    @DisplayName("Успешный вывод списка всех пользователей в консоль")
    void readAllUsers_Success() {
        UserEntity user1 = new UserEntity("Саша", "sasha@yandex.ru", 18);
        user1.setId(1);
        UserEntity user2 = new UserEntity("Анна", "ann@yandex.ru", 20);
        user2.setId(5);

        List<UserEntity> mockUsers = List.of(user1, user2);
        when(userDao.findAll()).thenReturn(mockUsers);

        initServiceWithInput("");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            userService.readAllUsers();

            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains(user1.toString()));
            assertTrue(consoleOutput.contains(user2.toString()));

            verify(userDao, times(1)).findAll();

        } finally {
            System.setOut(originalOut);
        }

    }

    @Test
    @DisplayName("Вывод сообщения, если при чтении всех пользователей база данных пуста")
    void readAllUsers_EmptyDatabase() {
        when(userDao.findAll()).thenReturn(java.util.Collections.emptyList());

        initServiceWithInput("");
        userService.readAllUsers();

        verify(userDao, times(1)).findAll();
    }


    @Test
    @DisplayName("Успешное обновление полей пользователя")
    void updateUser_Success() {
        initServiceWithInput("5\nАнна Новое\n\n21\n");

        UserEntity existingUser = new UserEntity("Анна", "ann@yandex.ru", 20);
        existingUser.setId(5);

        when(userDao.findById(5)).thenReturn(Optional.of(existingUser));

        UserEntity expectedUpdatedUser = new UserEntity("Анна Новое", "ann@yandex.ru", 21);
        expectedUpdatedUser.setId(5);

        userService.updateUser();

        verify(userDao, times(1)).update(eq(expectedUpdatedUser));
    }


    @Test
    @DisplayName("Неудачное обновление, если пользователь с таким ID не существует")
    void updateUser_UserNotFound() {
        initServiceWithInput("99\n");

        when(userDao.findById(99)).thenReturn(Optional.empty());

        userService.updateUser();
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Успешное удаление существующего пользователя")
    void deleteUser_Success() {
        initServiceWithInput("1\n");

        UserEntity existingUser = new UserEntity("Саша", "sasha@yandex.ru", 18);
        existingUser.setId(1);

        when(userDao.findById(1)).thenReturn(Optional.of(existingUser));

        userService.deleteUser();
        verify(userDao, times(1)).delete(1);
    }

    @Test
    @DisplayName("Неудачное удаление, если пользователь не найден")
    void deleteUser_UserNotFound() {
        initServiceWithInput("404\n");

        when(userDao.findById(404)).thenReturn(Optional.empty());

        userService.deleteUser();
        verify(userDao, never()).delete(anyInt());
    }

}
