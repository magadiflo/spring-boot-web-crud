package dev.magadiflo.springbootwebcrud.service.impl;

import dev.magadiflo.springbootwebcrud.exception.ApiException;
import dev.magadiflo.springbootwebcrud.model.dto.RegisterBookDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IBookProjection;
import dev.magadiflo.springbootwebcrud.persistence.entity.Author;
import dev.magadiflo.springbootwebcrud.persistence.entity.Book;
import dev.magadiflo.springbootwebcrud.persistence.entity.BookAuthor;
import dev.magadiflo.springbootwebcrud.persistence.entity.BookAuthorPK;
import dev.magadiflo.springbootwebcrud.persistence.repository.IAuthorRepository;
import dev.magadiflo.springbootwebcrud.persistence.repository.IBookAuthorRepository;
import dev.magadiflo.springbootwebcrud.persistence.repository.IBookRepository;
import dev.magadiflo.springbootwebcrud.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookServiceImpl implements IBookService {

    private final IBookRepository bookRepository;
    private final IAuthorRepository authorRepository;
    private final IBookAuthorRepository bookAuthorRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public IBookProjection findBookById(Long bookId) {
        return this.bookAuthorRepository.findBookAuthorByBookId(bookId)
                .orElseThrow(() -> new ApiException("No se encontró el ID del libro buscado", HttpStatus.NOT_FOUND));
    }

    /**
     * @param registerBookDTO
     * @return book id
     */
    @Override
    @Transactional
    public Long saveBook(RegisterBookDTO registerBookDTO) {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Book bookRequest = this.modelMapper.map(registerBookDTO, Book.class);
        Book bookDB = this.bookRepository.save(bookRequest);

        if (registerBookDTO.authorIdList() != null && !registerBookDTO.authorIdList().isEmpty()) {
            Integer countOnDB = this.authorRepository.countAuthorsByIds(registerBookDTO.authorIdList());

            if (registerBookDTO.authorIdList().size() != countOnDB) {
                throw new ApiException("Hay id de autores que no están registrados en la BD", HttpStatus.NOT_FOUND);
            }

            registerBookDTO.authorIdList().stream().forEach(authorId -> {
                Author author = Author.builder()
                        .id(authorId)
                        .build();

                Book book = Book.builder()
                        .id(bookDB.getId())
                        .build();

                BookAuthorPK bookAuthorPK = BookAuthorPK.builder()
                        .author(author)
                        .book(book)
                        .build();

                BookAuthor bookAuthor = BookAuthor.builder()
                        .id(bookAuthorPK)
                        .build();

                this.bookAuthorRepository.save(bookAuthor);
            });
        }

        return bookDB.getId();
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteBookById(Long bookId) {
        return Optional.ofNullable(this.bookRepository.findById(bookId)
                .map(bookDB -> {
                    Optional<Boolean> existsOptional = this.bookAuthorRepository.existsBookAuthorByBookId(bookDB.getId());
                    if (existsOptional.isPresent()) {
                        this.bookAuthorRepository.deleteBookAuthorByBookId(bookDB.getId());
                    }
                    this.bookRepository.delete(bookDB);
                    return true;
                })
                .orElseThrow(() -> new ApiException("No existe el book con id a eliminar", HttpStatus.NOT_FOUND)));
    }
}
