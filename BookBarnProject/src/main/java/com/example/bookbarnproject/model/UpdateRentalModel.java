package com.example.bookbarnproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRentalModel {


    @NotNull
    @Pattern(regexp="^[a-z0-9]{4,20}$", message="Username can only contains lowercase letters and numbers. And must be between 4-20 characters.")
    private String username;


    @NotNull
    @NotNull
    @Pattern(regexp="^[0-9]{13}$", message="ISBN must only contain 13 digits")
    private String isbn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    private String title;

    private String authorFullName;

    private Long id;
}
