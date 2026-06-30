package org.example.user.dto;

import jakarta.validation.constraints.*;

public class UserDto {
    private Integer id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @Min(value = 0, message = "Возраст должен быть от 0 до 120 лет")
    @Max(value = 120, message = "Возраст должен быть от 0 до 120 лет")
    @NotNull(message = "Возраст обязателен для заполнения")
    private Integer age;


    public UserDto() {}


    public UserDto(Integer id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
