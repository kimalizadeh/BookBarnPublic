package com.example.bookbarnproject.entity;

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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    @Size(min = 1, max = 100, message = "Title must be between 1-100 characters long")
    private String title;

    @Column(unique = true,length = 13, nullable = false)
    @Pattern(regexp="^[0-9]{13}$", message="ISBN must only contain 13 digits")
    private String isbn;

    @Column(length = 100, nullable = false)
    @Size(min = 1, max = 100, message = "Author must be between 1-100 characters long")
    private  String authorFullName;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private  BookType bookType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] coverPhoto;

//    @Lob
//    @Type(type="org.hibernate.type.BinaryType")
//    private byte[] ebookFile;

    @Column(nullable = false)
    private boolean isAvailable = true;

    private boolean deprecated = false;

    @Column(nullable = true)
    private int frontPageOrder = 0;

    @Transient
    private String parsedData;

    @Transient
    private boolean isEbook;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)//orphanRemoval = true  //, cascade = CascadeType.ALL

    private Set<Rental> rentals;

    @Column(length = 100, nullable = false)
    @Size(min = 1, max = 1000, message = "Description must be between 1-1000 characters long")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pdf_id", referencedColumnName = "id")
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

    public Book(String title, String isbn, String authorFullName, String category, String bookType, LocalDate publicationDate, String description) {
      this.title = title;
      this.isbn = isbn;
      this.authorFullName = authorFullName;
      this.category = Category.valueOf(category);
      this.bookType = BookType.valueOf(bookType);
      this.publicationDate = publicationDate;
      this.description = description;
    }






}
