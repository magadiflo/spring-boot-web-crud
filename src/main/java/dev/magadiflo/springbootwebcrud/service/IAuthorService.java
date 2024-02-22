package dev.magadiflo.springbootwebcrud.service;

import dev.magadiflo.springbootwebcrud.model.dto.RegisterAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.dto.UpdateAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;

import java.util.Optional;

public interface IAuthorService {
    IAuthorProjection findAuthorById(Long authorId);

    Integer saveAuthor(RegisterAuthorDTO authorDTO);

    IAuthorProjection updateAuthor(Long authorId, UpdateAuthorDTO authorDTO);

    Optional<Boolean> deleteAuthorById(Long authorId);
}
