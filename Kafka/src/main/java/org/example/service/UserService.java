package org.example.service;

import org.example.dto.UserDto;
import org.example.dto.UserEventDto;
import org.example.mapper.MapToUserDto;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MapToUserDto mapper;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    private static final String TOPIC = "user-notification-topic";


    public UserService(UserRepository userRepository, MapToUserDto mapper, KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return mapper.mapToListUserDto(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        return mapper.mapToUserDto(entity);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }
        UserEntity entity = mapper.mapToUserEntity(dto);
        UserEntity saved = userRepository.save(entity);
        kafkaTemplate.send(TOPIC, new UserEventDto("CREATE", saved.getEmail()));
        return mapper.mapToUserDto(saved);
    }

    @Transactional
    public UserDto updateUser(Integer id, UserDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже занят другим пользователем");
        }

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAge(dto.getAge());

        return mapper.mapToUserDto(userRepository.save(entity));
    }

    @Transactional
    public void deleteUser(Integer id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        kafkaTemplate.send(TOPIC, new UserEventDto("DELETE", entity.getEmail()));
        userRepository.deleteById(id);
    }
}
