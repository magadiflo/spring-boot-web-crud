package dev.magadiflo.springbootwebcrud.web.api;

import dev.magadiflo.springbootwebcrud.model.dto.RegisterAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.dto.UpdateAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import dev.magadiflo.springbootwebcrud.service.IAuthorService;
import dev.magadiflo.springbootwebcrud.web.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/authors")
public class AuthorRestController {

    private final IAuthorService authorService;

    @GetMapping(path = "/{authorId}")
    public ResponseEntity<IAuthorProjection> showAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(this.authorService.findAuthorById(authorId));
    }

    @PostMapping
    public ResponseEntity<Void> saveAuthor(@RequestBody RegisterAuthorDTO registerAuthorDTO) {
        this.authorService.saveAuthor(registerAuthorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{authorId}")
    public ResponseEntity<ResponseMessage<IAuthorProjection>> updateAuthor(@PathVariable Long authorId,
                                                                           @RequestBody UpdateAuthorDTO updateAuthorDTO) {
        IAuthorProjection authorProjection = this.authorService.updateAuthor(authorId, updateAuthorDTO);
        ResponseMessage<IAuthorProjection> responseMessage = new ResponseMessage<>("Registro actualizado", authorProjection);
        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping(path = "/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long authorId) {
        return this.authorService.deleteAuthorById(authorId)
                .map(wasDeleted -> new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                .orElseThrow();
    }
}
