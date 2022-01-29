package com.example.bookbarnproject.model;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Pdf;
import com.example.bookbarnproject.entity.Rental;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookModel {

    private Long id;

    @Size(min = 1, max = 100, message = "Title must be between 1-100 characters long")
    private String title;

    @Pattern(regexp="^[0-9]{13}$", message="ISBN must only contain 13 digits")
    private String isbn;

    @Size(min = 1, max = 100, message = "Author must be between 1-100 characters long")
    private  String authorFullName;

    private Book.Category category;

    private Book.BookType bookType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private byte[] coverPhoto;

    private boolean isAvailable = true;

    private boolean deprecated = false;

    private boolean updateCoverPhoto = false;

    private boolean updatePdf = false;

    @Size(min = 1, max = 1000, message = "Description must be between 1-1000 characters long")
    private String description;

    private Pdf pdf;

    public enum Category {
        NONFICTION,
        FICTION,
        SCIENCE,
        ART
    }

    public enum BookType {
        PAPER,
        EBOOK
    }


}
