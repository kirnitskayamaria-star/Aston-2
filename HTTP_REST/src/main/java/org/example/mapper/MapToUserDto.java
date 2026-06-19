package org.example.mapper;

import org.example.dto.UserDto;
import org.example.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapToUserDto {


    public UserDto mapToUserDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setAge(entity.getAge());
        return dto;
    }

    public List<UserDto> mapToListUserDto(List<UserEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserEntity mapToUserEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAge(dto.getAge());
        return entity;
    }
}
