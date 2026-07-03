package org.example.user.assembler;

import org.example.user.controller.UserController;
import org.example.user.dto.UserDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(UserDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).update(dto.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).getAll()).withRel("all-users")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserDto>> toCollectionModel(Iterable<? extends UserDto> entities) {
        CollectionModel<EntityModel<UserDto>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(UserController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
