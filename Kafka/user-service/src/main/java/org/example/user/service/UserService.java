package org.example.user.service;

import org.example.user.dto.UserDto;
import org.example.user.dto.UserEventDto;
import org.example.user.model.UserEntity;
import org.example.user.repository.UserRepository;
import org.example.user.repository.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    private static final String TOPIC = "user-notification-topic";

    public UserService(UserRepository userRepository, UserMapper mapper, KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return mapper.toDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        return mapper.toDto(entity);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }
        UserEntity entity = mapper.toEntity(dto);
        UserEntity saved = userRepository.save(entity);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send(TOPIC, new UserEventDto("CREATE", saved.getEmail()));
            }
        });
        return mapper.toDto(saved);
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
        return mapper.toDto(userRepository.save(entity));
    }

    @Transactional
    public void deleteUser(Integer id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        String email = entity.getEmail();
        userRepository.deleteById(id);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send(TOPIC, new UserEventDto("DELETE", email));
            }
        });
    }
}
