package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    //@Query("SELECT b FROM Book b WHERE b.isAvailable = ?1 AND b.deprecated = ?2 ")
    //List<Book> findTop10ByIdAnd(boolean isAvailable,boolean deprecated);//boolean isAvailable

    //@Query("SELECT b FROM Book b WHERE b.isAvailable = ?1 AND b.deprecated = ?2 ")

    //@Query("SELECT b FROM Book b WHERE b.isAvailable = ?1 AND b.deprecated = ?2 ")
    //List<Book> findTop10ByOrderByIdDesc(boolean isAvailable,boolean deprecated);//boolean isAvailable

    List<Book> findTop10ByDeprecatedOrderByIdDesc(boolean deprecated);
    //List<Book> findTop10ByOrderByIdDesc();//boolean isAvailable

//    @Query(
//            value = "SELECT b FROM Book b WHERE b.isAvailable = ?1 AND b.deprecated = ?2",
//            nativeQuery = true)
//    List<Book> findTopByIdOrderByIdDesc(boolean isAvailable,boolean deprecated);//boolean isAvailable
    Optional<Book> findFirstByDescriptionIsNotNull();

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAllByFrontPageOrderNotNull();

    List<Book> findAllByDeprecatedFalse();


}
