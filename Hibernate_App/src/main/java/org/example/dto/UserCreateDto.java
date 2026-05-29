package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UserCreateDto {
    private final String name;
    private final String email;
    private final String ageInput;
}

