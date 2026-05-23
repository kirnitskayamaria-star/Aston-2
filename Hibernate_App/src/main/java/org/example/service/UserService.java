package org.example.service;

import org.example.dto.UserCreateDto;
import org.example.model.UserEntity;
import org.example.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;


public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final Scanner scanner = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void createUser() {
        log.debug("\nСтарт метода createUser()");

        System.out.print("Введите имя: \n");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите возраст: ");
        String ageInput = scanner.nextLine();

        UserCreateDto dto = new UserCreateDto(name, email, ageInput);
        log.debug("Получен DTO на вход: {}", dto);

        if (name.isBlank()) {
            log.warn("Валидация провалена: Имя пользователя пустое");
            System.out.println("Ошибка: Имя не может быть пустым!");
            return;
        }

        int age;
        while (true) {
            try {

                age = Integer.parseInt(dto.getAgeInput());

                if (age < 0 || age > 120) {
                    log.warn("Валидация провалена: Возраст {} вне допустимого диапазона", age);
                    System.out.println("Ошибка: Возраст должен быть в диапазоне от 0 до 120 лет!");


                    System.out.print("Введите возраст повторно: ");
                    dto = new UserCreateDto(name, email, scanner.nextLine());
                    continue;
                }
                break;

            } catch (NumberFormatException e) {
                log.warn("Валидация провалена: Введены буквы вместо возраста '{}'", dto.getAgeInput());
                System.out.println("Ошибка: Возраст должен быть числом!");


                System.out.print("Введите возраст повторно: ");
                dto = new UserCreateDto(name, email, scanner.nextLine());
            }
        }

        UserEntity user = new UserEntity(name, email, age);
        log.debug("DTO успешно преобразован в Entity, отправка в DAO");


        boolean isSaved = userDao.save(user);
        if (!isSaved) {
            log.error("Не удалось сохранить пользователя с email {}", email);
            System.out.println("Не удалось создать пользователя. Возможно, такой email уже занят.");
        }

        log.debug("Завершение метода createUser()");
    }

    public void readUser() {
        log.debug("\nСтарт метода readUser()");

        System.out.print("Введите ID пользователя: ");
        int id = Integer.parseInt(scanner.nextLine());
        UserEntity user = userDao.findById(id);
        if (user != null) {
            log.debug("DAO вернул пользователя с id {} ", user.getId());
            System.out.println("Найден: " + user);
        } else {
            log.debug("Пользователь с id {} не найден.", id);
            System.out.println("Пользователь с таким ID не найден.");
        }
        log.debug("Завершение метода readUser()");
    }

    public void readAllUsers() {
        log.debug("\nСтарт метода readAllUsers()");

        List<UserEntity> users = userDao.findAll();
        log.debug("DAO вернул список из {} пользователей", users.size());

        if (users.isEmpty()) {
            System.out.println("База данных пуста.");
        } else {
            users.forEach(System.out::println);
        }
        log.debug("Завершение метода readAllUsers()");
    }

    public void updateUser() {
        log.debug("\nСтарт метода updateUser()");

        System.out.print("Введите ID пользователя для обновления: ");
        int id = Integer.parseInt(scanner.nextLine());
        UserEntity user = userDao.findById(id);

        if (user == null) {
            log.debug("Пользователь с id {} не найден.", id);
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Введите новое имя (оставьте пустым для пропуска): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Введите новый email (оставьте пустым для пропуска): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Введите новый возраст (или -1 для пропуска): ");
        int age = Integer.parseInt(scanner.nextLine());
        if (age != -1) user.setAge(age);

        log.debug("Пользователь изменен.");
        userDao.update(user);

        log.debug("Завершение метода updateUser()");
    }

    public void deleteUser() {
        log.debug("\nСтарт метода deleteUser()");
        System.out.print("Введите ID пользователя для удаления: ");
        int id = Integer.parseInt(scanner.nextLine());
        UserEntity user = userDao.findById(id);
        if (user != null) {
            userDao.delete(id);
            log.debug("Удаление пользователя с id {} ", id);
            System.out.println("Пользователь удален.");
        } else {
            log.debug("Пользователь с id {} не найден.", id);
            System.out.println("Пользователь с таким ID не найден.");
        }
        log.debug("Завершение метода deleteUser()");

    }
}
