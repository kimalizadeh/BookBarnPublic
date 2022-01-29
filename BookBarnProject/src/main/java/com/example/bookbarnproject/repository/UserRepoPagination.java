package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepoPagination extends PagingAndSortingRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.deprecated = false AND " +
            "CONCAT(u.username, u.firstName, u.lastName, u.address, u.role, u.email, u.sex, u.age)" +
            " LIKE %?1%")
    public Page<User> findAllByDeprecatedFalse(String keyword, Pageable pageable);

    public Page<User> findAllByDeprecatedFalse(Pageable pageable);

}
