package dev.magadiflo.springbootwebcrud.web.api;

import dev.magadiflo.springbootwebcrud.model.dto.AuthorRequestParam;
import dev.magadiflo.springbootwebcrud.model.dto.RegisterAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.dto.UpdateAuthorDTO;
import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import dev.magadiflo.springbootwebcrud.persistence.repository.specification.AuthorSpecification;
import dev.magadiflo.springbootwebcrud.persistence.repository.specification.AuthorSpecs;
import dev.magadiflo.springbootwebcrud.service.IAuthorService;
import dev.magadiflo.springbootwebcrud.web.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    //----------- Se trabajaron con JpaSpecificationExecutor y Specification (API Criteria) ----------------------------
    @GetMapping(path = "/specifications")
    public ResponseEntity<List<IAuthorProjection>> findAllAuthorWithSpecification(AuthorRequestParam authorRequestParam) {
        AuthorSpecification authorSpecification = AuthorSpecification.builder()
                .q(authorRequestParam.q())
                .birthdate(authorRequestParam.birthdate())
                .build();
        return ResponseEntity.ok(this.authorService.findAllAuthorWithSpecification(authorSpecification));
    }

    @GetMapping(path = "/specs")
    public ResponseEntity<List<IAuthorProjection>> findAllAuthorWithSpecs(AuthorRequestParam authorRequestParam) {
        Specification<IAuthorProjection> condition1 = AuthorSpecs.isEqualToBirthdate(authorRequestParam.birthdate());
        Specification<IAuthorProjection> condition2 = AuthorSpecs.fullNameContainsTheSearchedTerm(authorRequestParam.q());

        return ResponseEntity.ok(this.authorService.findAllAuthorWithSpecs(condition1.and(condition2)));
    }

    @GetMapping(path = "/paginated")
    public ResponseEntity<Page<IAuthorProjection>> findAllToPage(@RequestParam(name = "q", required = false) String q,
                                                                 @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate birthdate,
                                                                 @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
                                                                 @SortDefault(sort = "id", direction = Sort.Direction.ASC) Sort sort) {

        Specification<IAuthorProjection> condition1 = AuthorSpecs.isEqualToBirthdate(birthdate);
        Specification<IAuthorProjection> condition2 = AuthorSpecs.fullNameContainsTheSearchedTerm(q);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return ResponseEntity.ok(this.authorService.findAllToPage(condition1.and(condition2), pageable));
    }
}
