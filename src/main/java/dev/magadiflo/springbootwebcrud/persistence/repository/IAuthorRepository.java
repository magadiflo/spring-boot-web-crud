package dev.magadiflo.springbootwebcrud.persistence.repository;

import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import dev.magadiflo.springbootwebcrud.persistence.entity.Author;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAuthorRepository extends PagingAndSortingRepository<Author, Long> {

    /**
     * @param ids de los autores
     * @return cantidad de autores encontrados por su id
     */
    @Query(value = """
            SELECT COUNT(a.id) AS count
            FROM authors a
            WHERE a.id IN(:authorsId)
            """, nativeQuery = true)
    Integer countAuthorsByIds(@Param("authorsId") List<Long> ids);

    /**
     * @param id, es el id del author
     * @return Optional<IAuthorProjection>, interfaz donde se definieron métodos correspondientes a los campos
     * devueltos en el select. (Ver tema de Projections)
     */
    @Query(value = """
            SELECT a.id AS id, a.first_name AS firstName, a.last_name AS lastName,
                    CONCAT(a.first_name, ' ' , a.last_name) AS fullName, a.birthdate AS birthdate
            FROM authors AS a
            WHERE a.id = :id
            """, nativeQuery = true)
    Optional<IAuthorProjection> findAuthorById(@Param("id") Long id);

    /**
     * @param author
     * @return affected rows
     */
    @Modifying
    @Query(value = """
            INSERT INTO authors(first_name, last_name, birthdate)
            VALUES(:#{#author.firstName}, :#{#author.lastName}, :#{#author.birthdate})
            """, nativeQuery = true)
    Integer saveAuthor(@Param("author") Author author);

    /**
     * @param author
     * @return affected rows
     */
    @Modifying
    @Query(value = """
            UPDATE authors AS a
            SET a.first_name = :#{#author.firstName}, a.last_name = :#{#author.lastName}, a.birthdate = :#{#author.birthdate}
            WHERE a.id = :#{#author.id}
            """, nativeQuery = true)
    Integer updateAuthor(@Param("author") Author author);

    /**
     * @param id, es el id del author
     * @return void
     */
    @Modifying
    @Query(value = "DELETE FROM authors WHERE id = :id", nativeQuery = true)
    void deleteAuthorById(@Param("id") Long id);
}
