package dev.magadiflo.springbootwebcrud.persistence.repository;

import dev.magadiflo.springbootwebcrud.persistence.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IBookRepository extends CrudRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
}
