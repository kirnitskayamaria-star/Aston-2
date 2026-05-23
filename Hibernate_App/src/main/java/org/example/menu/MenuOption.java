package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuOption {

    CREATE("1", "Создать пользователя"),
    READ("2", "Найти пользователя по ID"),
    READ_ALL("3", "Показать всех пользователей"),
    UPDATE("4", "Обновить пользователя"),
    DELETE("5", "Удалить пользователя"),
    EXIT("6", "Выход");

    private final String key;
    private final String description;

    public static MenuOption fromKey(String key) {
        for (MenuOption option : values()) {
            if (option.key.equals(key)) return option;
        }
        return null;
    }
}
