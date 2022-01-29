package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepoPagination extends PagingAndSortingRepository <Book, Long> {


    @Query("SELECT b FROM Book b WHERE b.deprecated = false AND " +
            "CONCAT(lower(b.title), ' ', lower(b.authorFullName), ' ', lower(b.category), ' ', lower(b.bookType), ' ', lower(b.isbn)) " +
            " LIKE %?1%")
    public Page<Book> findAllByDeprecatedFalse(String keyword, Pageable pageable);

    public Page<Book> findAllByDeprecatedFalse(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deprecated = false AND b.isAvailable=true AND " +
            "CONCAT(lower(b.title), ' ', lower(b.authorFullName), ' ', lower(b.category), ' ', lower(b.bookType), ' ', lower(b.isbn)) " +
            " LIKE %?1%")
    public Page<Book> findAllByDeprecatedFalseAndAvailable(String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deprecated = false AND b.isAvailable=false AND " +
            "CONCAT(lower(b.title), ' ', lower(b.authorFullName), ' ', lower(b.category), ' ', lower(b.bookType), ' ', lower(b.isbn)) " +
            " LIKE %?1%")
    public Page<Book> findAllByDeprecatedFalseAndAvailableIsFalse(String keyword, Pageable pageable);




}
