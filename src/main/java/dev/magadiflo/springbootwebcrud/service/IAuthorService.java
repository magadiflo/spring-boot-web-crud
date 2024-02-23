package dev.magadiflo.springbootwebcrud.service;

import dev.magadiflo.springbootwebcrud.model.dto.RegisterAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.dto.UpdateAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import dev.magadiflo.springbootwebcrud.persistence.repository.specification.AuthorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface IAuthorService {
    IAuthorProjection findAuthorById(Long authorId);

    Integer saveAuthor(RegisterAuthorDTO authorDTO);

    IAuthorProjection updateAuthor(Long authorId, UpdateAuthorDTO authorDTO);

    Optional<Boolean> deleteAuthorById(Long authorId);

    /**
     * Métodos para usar JpaSpecificationExecutor para la ejecución de Specification (API Criteria) para consultas
     * dinámicas.
     * <p>
     * JpaSpecificationExecutor, interfaz para permitir la ejecución de Specifications basadas en la
     * API de criterios JPA.
     */
    List<IAuthorProjection> findAllAuthorWithSpecification(AuthorSpecification authorSpecification);

    List<IAuthorProjection> findAllAuthorWithSpecs(Specification<IAuthorProjection> authorSpecs);

    Page<IAuthorProjection> findAllToPage(Specification<IAuthorProjection> authorSpecs, Pageable pageable);
}
