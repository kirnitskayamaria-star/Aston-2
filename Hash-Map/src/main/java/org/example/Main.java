package org.example;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MyHashMap<String, String> phoneBook = new MyHashMap<>();


        phoneBook.put("Анна", "+79111111111");
        phoneBook.put("Петр", "+79222222222");
        phoneBook.put("Иван", "+79333333333");

        System.out.println("Размер базы после добавления 3 человек: " + phoneBook.size());
        System.out.println("Телефон Петра: " + phoneBook.get("Петр"));

        System.out.println("\n Проверка обновления значения ");
        String oldPhone = phoneBook.put("Петр", "+79999999999");
        System.out.println("Старый телефон Петра (вернул put): " + oldPhone);
        System.out.println("Новый телефон Петра (вернул get): " + phoneBook.get("Петр"));

        System.out.println("\n Проверка работы с null ");
        phoneBook.put(null, "+70000000000");
        System.out.println("Телефон для null-ключа: " + phoneBook.get(null));
        System.out.println("Размер базы с учетом null: " + phoneBook.size());

        System.out.println("\n Тестирование автоматического увеличения (resize) ");
        for (int i = 1; i <= 10; i++) {
            phoneBook.put("Абонент_" + i, "+790000000" + i);
        }

        System.out.println("Размер после массового добавления: " + phoneBook.size());
        System.out.println("Анна на месте после resize? " + (phoneBook.get("Анна") != null));
        System.out.println("Null-ключ на месте после resize? " + (phoneBook.get(null) != null));

        System.out.println("\n Обход базы через живой entrySet() ");
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            System.out.println("Ключ: " + entry.getKey() + " -> Значение: " + entry.getValue());
        }

        System.out.println("\n Проверка удаления (remove) ");
        String removedValue = phoneBook.remove("Иван");
        System.out.println("Удаленный телефон Ивана: " + removedValue);
        System.out.println("Попытка найти Ивана после удаления: " + phoneBook.get("Иван"));
        System.out.println("Финальный размер базы: " + phoneBook.size());

        System.out.println(" Очистка базы (clear) ");
        phoneBook.clear();
        System.out.println("Размер после clear(): " + phoneBook.size());
    }
}

