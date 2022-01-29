package com.example.bookbarnproject.config;


import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.User;
import com.example.bookbarnproject.repository.BookRepository;
import com.example.bookbarnproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DBDataConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final static String description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
            "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a " +
            "galley of type and scrambled it to make a type specimen book.";


    @Bean
    CommandLineRunner addUsers(UserRepository userRepository, BookRepository bookRepository) {

        if(userRepository.findFirstByUsernameIsNotNull().isEmpty() && bookRepository.findFirstByDescriptionIsNotNull().isEmpty()) {

            String password = bCryptPasswordEncoder.encode("Password123");
            return args -> {
                User admin = new User(
                        1L,
                        "admin123",
                        "admin@admin.com",
                        password,
                        "admin",
                        "boss",
                        "123 street",
                        50,
                        "Female",
                        "Admin"
                );

                User jim123 = new User(
                        2L,
                        "jim123",
                        "jim@jim.com",
                        password,
                        "jim",
                        "smith",
                        "111 street",
                        38,
                        "Male",
                        "Member"
                );

                User helen123 = new User(
                        3L,
                        "helen123",
                        "helen@helen.com",
                        password,
                        "helen",
                        "smart",
                        "1531 street",
                        28,
                        "Female",
                        "Member"
                );

                User john123 = new User(
                        4L,
                        "john123",
                        "john@john.com",
                        password,
                        "john",
                        "stop",
                        "1920 street",
                        20,
                        "Male",
                        "Member"
                );

                User eman123 = new User(
                        4L,
                        "eman123",
                        "eman@eman.com",
                        password,
                        "eman",
                        "nerd",
                        "120 up street",
                        29,
                        "Female",
                        "Member"
                );

                User flor123 = new User(
                        4L,
                        "flor123",
                        "flor@flor.com",
                        password,
                        "flor",
                        "flora",
                        "120 down street",
                        19,
                        "Female",
                        "Member"
                );

                userRepository.saveAll(
                        List.of(admin, jim123,helen123,john123,eman123,flor123)
                );

                Book b1 = new Book(
                        "Book1",
                        "1112223334445",
                        "author",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2010, 1, 1),
                        description
                );


                Book b2 = new Book(
                        "Book2",
                        "1112222334445",
                        "John Bark",
                        "ART",
                        "PAPER",
                        LocalDate.of(2000, 1, 1),
                        description
                );



                Book b3 = new Book(
                        "Spring Programming",
                        "0012222334445",
                        "Feng",
                        "ART",
                        "PAPER",
                        LocalDate.of(2020, 11, 11),
                        description
                );



                Book b4 = new Book(
                        "Spring Programming",
                        "0012211334445",
                        "Feng",
                        "ART",
                        "PAPER",
                        LocalDate.of(2000, 11, 11),
                        description
                );

                Book b5 = new Book(
                        "Spring Programming",
                        "0012777734445",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2000, 11, 11),
                        description
                );

                Book b6 = new Book(
                        "Design",
                        "8012777734445",
                        "Anthony",
                        "ART",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );

                Book b7 = new Book(
                        "Programming",
                        "8012777004405",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );

                Book b8 = new Book(
                        "Web Design",
                        "8012007004405",
                        "Anthony",
                        "SCIENCE",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );

                Book b9 = new Book(
                        "Data Structure",
                        "8012007005555",
                        "Anthony",
                        "SCIENCE",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );


                Book b10 = new Book(
                        "Data Science",
                        "8012033305555",
                        "Anthony",
                        "SCIENCE",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );


                Book b11 = new Book(
                        "Computer Science",
                        "9012033305555",
                        "Anthony",
                        "SCIENCE",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );


                Book b12 = new Book(
                        "Science",
                        "9012032205777",
                        "Anthony",
                        "SCIENCE",
                        "PAPER",
                        LocalDate.of(1900, 11, 11),
                        description
                );

                Book b13 = new Book(
                        "Story",
                        "1112032205777",
                        "Anthony",
                        "ART",
                        "PAPER",
                        LocalDate.of(1991, 11, 11),
                        description
                );

                Book b14 = new Book(
                        "Advocate",
                        "1192032205777",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(1999, 12, 14),
                        description
                );

                Book b15 = new Book(
                        "Devils",
                        "1112034444757",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(1910, 11, 11),
                        description
                );

                Book b16 = new Book(
                        "Story",
                        "1312065555577",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2017, 12, 11),
                        description
                );


                Book b17 = new Book(
                        "Short Story",
                        "1112066205707",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2020, 11, 11),
                        description
                );

                Book b18 = new Book(
                        "Hardwork",
                        "1130366205777",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2014, 10, 11),
                        description
                );

                Book b19 = new Book(
                        "Shory",
                        "1111111111111",
                        "Anthony",
                        "FICTION",
                        "PAPER",
                        LocalDate.of(2010, 11, 21),
                        description
                );

                Book b20 = new Book(
                        "Shortage",
                        "1112066205767",
                        "Anthony",
                        "ART",
                        "PAPER",
                        LocalDate.of(2021, 01, 14),
                        description
                );

                bookRepository.saveAll(
                        List.of(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20)
                );
            };

        }
        return args -> {};
    }
}
