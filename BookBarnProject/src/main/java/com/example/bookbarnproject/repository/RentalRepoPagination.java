package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RentalRepoPagination extends PagingAndSortingRepository<Rental,Long> {


    // =========== Manage Rental For Admin =====================//


  /*@Query("SELECT r FROM Rental r JOIN r.book b WHERE  " +
          "b.title LIKE %?1%"
        + "OR b.authorFullName LIKE %?1% "
        + "OR b.isbn LIKE %?1% "
        )*/
  @Query("SELECT r FROM Rental r JOIN r.book b JOIN r.user u WHERE "
          + "CONCAT(b.title, ' ', b.authorFullName, ' ', b.category, ' ', b.bookType, ' ', b.isbn, ' ', u.username) " +
          " LIKE %?1% "
  )
  Page<Rental> findAll(String keyword, Pageable pageable);

  Page<Rental> findAll(Pageable pageable);

    // ====================================== User Rental Dashboard =============================================//

    /*@Query("SELECT r FROM Rental r JOIN r.book b WHERE " +
            "b.title LIKE %?1%"
            + "OR b.authorFullName LIKE %?1% "
            + "OR b.isbn LIKE %?1% "
    )*/
    /*@Query("SELECT r FROM Rental r JOIN r.user u WHERE " +
            "u.username LIKE %?1%" +
            "AND u.firstName LIKE %?2%"
    )*/
    /*@Query("SELECT r FROM Rental r JOIN r.book b WHERE " +
            "b.title LIKE %?2% "
            + "OR b.authorFullName LIKE %?2% "
            + "OR b.isbn LIKE %?2% "
            + "OR b.description LIKE %?1% "
    )*/
  //"CONCAT(b.title, ' ', b.authorFullName, ' ', b.category, ' ', b.bookType, ' ', b.isbn) " +
    //      " LIKE %?1%"
  /*@Query("SELECT r FROM Rental r JOIN r.book b WHERE "
          + "CONCAT(b.title, ' ', b.authorFullName, ' ', b.category, ' ', b.bookType, ' ', b.isbn) " +
          " LIKE %?2% "
          + "OR b.description LIKE %?1%"
  )
    Page<Rental> findByUserUsernameOrderByStartDateDesc(String username,String keyword, Pageable pageable);

    Page<Rental> findByUserUsernameOrderByStartDateDesc(String username,Pageable pageable);*/

  @Query("SELECT r FROM Rental r JOIN r.book b WHERE "
          + "CONCAT(b.title, ' ', b.authorFullName, ' ', b.category, ' ', b.bookType, ' ', b.isbn) " +
          " LIKE %?1% "
  )
  Page<Rental> findByUserUsernameOrderByStartDateDesc(String keyword, String username, Pageable pageable);

  Page<Rental> findByUserUsernameOrderByStartDateDesc(String username,Pageable pageable);
}
