package dev.magadiflo.springbootwebcrud.web.api;

import dev.magadiflo.springbootwebcrud.model.dto.RegisterBookDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IBookProjection;
import dev.magadiflo.springbootwebcrud.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookRestController {

    private final IBookService bookService;

    @GetMapping(path = "/with-authors/{bookId}")
    public ResponseEntity<IBookProjection> showBookWithAuthors(@PathVariable Long bookId) {
        return ResponseEntity.ok(this.bookService.findBookAuthorByBookId(bookId));
    }

    @PostMapping(path = "/with-authors")
    public ResponseEntity<Void> saveBookWithAuthors(@RequestBody RegisterBookDTO registerBookDTO) {
        this.bookService.saveBookWithAuthorsIdList(registerBookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(path = "/with-authors-list/{bookId}")
    public ResponseEntity<Void> deleteBookWithAuthorsList(@PathVariable Long bookId) {
        return this.bookService.deleteBookById(bookId)
                .map(wasDeleted -> new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                .orElseThrow();
    }

}
