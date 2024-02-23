package dev.magadiflo.springbootwebcrud.service.impl;

import dev.magadiflo.springbootwebcrud.exception.ApiException;
import dev.magadiflo.springbootwebcrud.model.dto.RegisterAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.dto.UpdateAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import dev.magadiflo.springbootwebcrud.persistence.entity.Author;
import dev.magadiflo.springbootwebcrud.persistence.repository.IAuthorRepository;
import dev.magadiflo.springbootwebcrud.persistence.repository.IBookAuthorRepository;
import dev.magadiflo.springbootwebcrud.persistence.repository.specification.AuthorSpecification;
import dev.magadiflo.springbootwebcrud.service.IAuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthorServiceImpl implements IAuthorService {

    private final IAuthorRepository authorRepository;
    private final IBookAuthorRepository bookAuthorRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public IAuthorProjection findAuthorById(Long authorId) {
        return this.authorRepository.findAuthorById(authorId)
                .orElseThrow(() -> new ApiException("No existe el author buscado", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Integer saveAuthor(RegisterAuthorDTO authorDTO) {
        Integer affectedRows = null;
        try {
            Author author = this.modelMapper.map(authorDTO, Author.class);
            affectedRows = this.authorRepository.saveAuthor(author);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("No se pudo registrar al author", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return affectedRows;
    }

    @Override
    @Transactional
    public IAuthorProjection updateAuthor(Long authorId, UpdateAuthorDTO authorDTO) {
        return this.authorRepository.findAuthorById(authorId)
                .map(authorProjectionDB -> {
                    Author author = this.modelMapper.map(authorDTO, Author.class);
                    author.setId(authorId);
                    return author;
                })
                .map(this.authorRepository::updateAuthor)
                .map(affectedRows -> this.authorRepository.findAuthorById(authorId).orElseThrow(() -> new ApiException("No se pudo encontrar al author actualizado", HttpStatus.NOT_FOUND)))
                .orElseThrow(() -> new ApiException("No existe el author para actualizar", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteAuthorById(Long authorId) {
        Optional<Boolean> existOptional = this.bookAuthorRepository.existsBookAuthorByAuthorId(authorId);

        if (existOptional.isPresent()) {
            this.bookAuthorRepository.deleteBookAuthorByAuthorId(authorId);
        }

        return Optional.ofNullable(this.authorRepository.findAuthorById(authorId)
                .map(authorProjectionDB -> {
                    this.authorRepository.deleteAuthorById(authorId);
                    return true;
                })
                .orElseThrow(() -> new ApiException("Author no encontrado para su eliminación", HttpStatus.NOT_FOUND)));
    }

    //----------- Se trabajaron con JpaSpecificationExecutor y Specification (API Criteria) ----------------------------
    @Override
    public List<IAuthorProjection> findAllAuthorWithSpecification(AuthorSpecification authorSpecification) {
        return this.authorRepository.findAll(authorSpecification);
    }

    @Override
    public List<IAuthorProjection> findAllAuthorWithSpecs(Specification<IAuthorProjection> authorSpecs) {
        return this.authorRepository.findAll(authorSpecs);
    }

    @Override
    public Page<IAuthorProjection> findAllToPage(Specification<IAuthorProjection> authorSpecs, Pageable pageable) {
        return this.authorRepository.findAll(authorSpecs, pageable);
    }
}
