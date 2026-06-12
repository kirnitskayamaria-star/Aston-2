package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserDto;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("integration_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users - Успешное создание")
    void createUser_Success() throws Exception {
        UserDto requestDto = new UserDto(null, "Мария", "maria@example.com", 25);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Мария"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @DisplayName("POST /api/users - Ошибка валидации при некорректном возрасте")
    void createUser_ValidationError() throws Exception {
        UserDto invalidDto = new UserDto(null, "Иван", "ivan@example.com", 150); // Возраст > 120

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users - Получение всех пользователей")
    void getAllUsers_Success() throws Exception {
        userRepository.save(new UserEntity(null, "User1", "user1@mail.com", 20, null));
        userRepository.save(new UserEntity(null, "User2", "user2@mail.com", 30, null));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("User1"))
                .andExpect(jsonPath("$[1].name").value("User2"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Успешное обновление пользователя")
    void updateUser_Success() throws Exception {
        UserEntity user = userRepository.save(new UserEntity(null, "Иван", "ivan@example.com", 25, null));

        UserDto updateRequest = new UserDto(null, "Иван Петров", "ivan_new@example.com", 31);

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("ivan_new@example.com"))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Негативный сценарий: Конфликт уникального Email")
    void updateUser_EmailConflict_ThrowsConflictStatus() throws Exception {
        UserEntity firstUser = userRepository.save(new UserEntity(null, "Иван", "ivan@example.com", 25, null));
        UserEntity secondUser = userRepository.save(new UserEntity(null, "Петр", "petr@example.com", 30, null));

        UserDto updateRequest = new UserDto(null, "Новый Петр", firstUser.getEmail(), 31);

        mockMvc.perform(put("/api/users/" + secondUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Успешное удаление")
    void deleteUser_Success() throws Exception {
        UserEntity user = userRepository.save(new UserEntity(null, "Удаляемый", "delete@example.com", 40, null));

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()));
    }
}
