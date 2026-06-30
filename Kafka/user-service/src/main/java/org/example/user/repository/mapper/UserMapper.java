package org.example.user.repository.mapper;

import org.example.user.dto.UserDto;
import org.example.user.model.UserEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserMapper {

    public UserDto toDto(UserEntity entity) {
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

    public List<UserDto> toDtoList(List<UserEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public UserEntity toEntity(UserDto dto) {
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
