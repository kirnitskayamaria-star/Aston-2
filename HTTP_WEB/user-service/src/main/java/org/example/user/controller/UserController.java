package org.example.user.controller;

import jakarta.validation.Valid;
import org.example.user.assembler.UserModelAssembler;
import org.example.user.dto.UserDto;
import org.example.user.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    public UserController(UserService userService, UserModelAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAll() {
        List<UserDto> users = userService.getAllUsers();
        return assembler.toCollectionModel(users);
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> getById(@PathVariable Integer id) {
        UserDto userDto = userService.getUserById(id);
        return assembler.toModel(userDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> create(@Valid @RequestBody UserDto dto) {
        UserDto createdUser = userService.createUser(dto);
        return assembler.toModel(createdUser);
    }

    @PutMapping("/{id}")
    public EntityModel<UserDto> update(@PathVariable Integer id, @Valid @RequestBody UserDto dto) {
        UserDto updatedUser = userService.updateUser(id, dto);
        return assembler.toModel(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
