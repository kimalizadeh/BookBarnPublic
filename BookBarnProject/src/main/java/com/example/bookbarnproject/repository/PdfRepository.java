package com.example.bookbarnproject.repository;

import com.example.bookbarnproject.entity.Pdf;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PdfRepository extends JpaRepository<Pdf, Long> {

    Optional<Pdf> findByName(String name);
}
