package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //@Query("SELECT u FROM User U WHERE u.isDeprecated = ?1")
    //List<User> findAllByDeprecatedNotNull(boolean isDeprecated);

    //@Query("SELECT u FROM User U WHERE u.isDeprecated = ?1")
    //List<User> findAllByIdAndDeprecated(boolean isDeprecated);

    Optional<User> findByUsername(String username);
    Optional<User> findFirstByUsernameIsNotNull();

    List<User> findAllByDeprecatedFalse();


    //List<Rental> findByBookBookTitleAndStartDateIsNotNullAndReturnDateIsNull();

    //List<Rental> findByBookBookTitleAndStartDateIsNotNullAndReturnDateIsNull();
}
