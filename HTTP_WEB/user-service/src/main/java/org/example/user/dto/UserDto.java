package org.example.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "Data Transfer Object for User information with HATEOAS support")
public class UserDto extends RepresentationModel<UserDto> {

    @Schema(
            description = "Unique identifier of the user",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    @Schema(
            description = "Full name of the user",
            example = "Ivan Ivanov",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Email(message = "Incorrect Email")
    @NotBlank(message = "Email cannot be blank")
    @Schema(
            description = "Unique email address of the user",
            example = "ivan@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Min(value = 0, message = "Age must be from 0 to 120 years old")
    @Max(value = 120, message = "Age must be from 0 to 120 years old")
    @NotNull(message = "Age cannot be blank")
    @Schema(
            description = "Age of the user",
            example = "25",
            minimum = "0",
            maximum = "120",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
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
