package dev.magadiflo.springbootwebcrud.persistence.repository;

import dev.magadiflo.springbootwebcrud.model.projection.IBookProjection;
import dev.magadiflo.springbootwebcrud.persistence.entity.BookAuthor;
import dev.magadiflo.springbootwebcrud.persistence.entity.BookAuthorPK;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IBookAuthorRepository extends CrudRepository<BookAuthor, BookAuthorPK> {
    @Query(value = """
            SELECT b.id AS id, b.title AS title, b.publication_date AS publicationDate, b.online_availability AS onlineAvailability,
            	GROUP_CONCAT(CONCAT(a.first_name, ' ', a.last_name) SEPARATOR ', ') AS concatAuthors
            FROM books AS b
            	INNER JOIN books_authors AS ba ON(b.id = ba.book_id)
            	INNER JOIN authors AS a ON(ba.author_id = a.id)
            WHERE b.id = :bookId
            GROUP BY b.id, b.title, b.publication_date, b.online_availability
            """, nativeQuery = true)
    Optional<IBookProjection> findBookAuthorByBookId(@Param("bookId") Long id);

    @Query("""
            SELECT CASE
                        WHEN COUNT(ba.id.book.id) > 0 THEN true
                        ELSE false
                    END
            FROM BookAuthor AS ba
            WHERE ba.id.book.id = :bookId
            """)
    Optional<Boolean> existsBookAuthorByBookId(@Param("bookId") Long id);

    @Query("""
            SELECT CASE
                        WHEN COUNT(ba.id.author.id) > 0 THEN true
                        ELSE false
                    END
            FROM BookAuthor AS ba
            WHERE ba.id.author.id = :authorId
            """)
    Optional<Boolean> existsBookAuthorByAuthorId(@Param("authorId") Long id);

    @Modifying
    @Query("DELETE FROM BookAuthor AS ba WHERE ba.id.book.id = :bookId")
    Integer deleteBookAuthorByBookId(@Param("bookId") Long id);

    @Modifying
    @Query("DELETE FROM BookAuthor AS ba WHERE ba.id.author.id = :authorId")
    Integer deleteBookAuthorByAuthorId(@Param("authorId") Long id);

}
