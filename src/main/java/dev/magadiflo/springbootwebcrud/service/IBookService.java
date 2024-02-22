package dev.magadiflo.springbootwebcrud.service;

import dev.magadiflo.springbootwebcrud.model.dto.RegisterBookDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IBookProjection;

import java.util.Optional;

public interface IBookService {
    IBookProjection findBookById(Long bookId);

    Long saveBook(RegisterBookDTO registerBookDTO);

    Optional<Boolean> deleteBookById(Long bookId);
}
