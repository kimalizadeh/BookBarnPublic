package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    //List<Rental> findByBookBookTitleAndStartDateIsNotNullAndReturnDateIsNull();

    //List<Rental> findByBookBookTitleAndStartDateIsNotNullAndReturnDateIsNull();

    List<Rental> findByUserUsernameAndReturnDateIsNull(String username);

    List<Rental> findByBook_IdAndAndReturnDateIsNull(Long bookId);

    Optional<Rental> findByBookIsbnAndReturnDateIsNotNull(String isbn);



    List<Rental> findByUserUsernameAndBook_IdAndReturnDateIsNull(String username,Long bookId);

    Optional<Rental> findByBookIsbnAndReturnDateIsNull(String isbn);


}


