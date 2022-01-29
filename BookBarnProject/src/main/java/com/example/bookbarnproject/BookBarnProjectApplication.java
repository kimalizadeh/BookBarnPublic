package com.example.bookbarnproject;

//import com.example.bookbarnproject.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class BookBarnProjectApplication {



    public static void main(String[] args) {
        SpringApplication.run(BookBarnProjectApplication.class, args);
    }

}
