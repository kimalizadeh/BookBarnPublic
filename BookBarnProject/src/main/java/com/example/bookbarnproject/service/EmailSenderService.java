package com.example.bookbarnproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String username, String title, String author, String dueDate) {

        SimpleMailMessage message = new SimpleMailMessage();
        String body = String.format("Congradulations %s, you just rented the book: %s by %s! You must return it by: %s ",
                        username, title, author, dueDate);

        String subject = "Book Rented";
        message.setFrom("bookbarn.fsd01@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        try{
            mailSender.send(message);
        }
        catch(MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
