package com.example.bookbarnproject.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pdfs")
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    @Size(min = 1, max = 50, message = "File name must be 1-50 characters long")
    private String name;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] file;

    private Long size;

    @OneToOne(mappedBy = "pdf")
    private Book book;
}
