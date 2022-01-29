package com.example.bookbarnproject.controller;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.repository.BookRepository;
import com.example.bookbarnproject.repository.UserRepository;

import com.example.bookbarnproject.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping({"/","home"})
    public ModelAndView getIndex(){
        ModelAndView mav = new ModelAndView("index");
       /** Book boo1 = bookRepository.findById(1L).get();
        Book book2 = bookRepository.findById(2L).get();
        Book book3 = bookRepository.findById(3L).get();
        Book book4 = bookRepository.findById(4L).get();
        Book book5 = bookRepository.findById(5L).get();
        Book book6 = bookRepository.findById(6L).get();

        List<Book> bookList = new ArrayList<>();
        bookList.add(boo1);
        bookList.add(book2);
        bookList.add(book3);
        bookList.add(book4);
        bookList.add(book5);
        bookList.add(book6);*/

        List<Book> bookList = bookRepository.findTop10ByDeprecatedOrderByIdDesc(false);
//        List<Book> temp = new ArrayList<Book>();
        for(Book book: bookList) {
//            if(book.isDeprecated()){
//                temp.add(book);
//            }
            if(book.getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(book.getCoverPhoto());
                book.setParsedData(image);
            }
            String newBody = book.getDescription().substring(0, Math.min(book.getDescription().length(), 50)) + "...";
            book.setDescription(newBody);
            System.out.println(book.getId());
        }
//        for(Book book: temp){
//            bookList.remove(book);
//        }
//        if(bookList.size()==10 && bookList.get(9) != null) {
//            bookList.remove(9);
//
//        }
        bookList.remove(9);
        mav.addObject("bookList",bookList);
        return mav;
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login_err")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "accessdenied";
    }



    @GetMapping("/bookDetails")
    public ModelAndView viewArticle(@RequestParam Long bookId) {
        ModelAndView mav = new ModelAndView("bookdetails");
        Book book = bookRepository.findById(bookId).get();

        if(book.getCoverPhoto() != null) {
            String image = Base64.getEncoder().encodeToString(book.getCoverPhoto());
            book.setParsedData(image);
        }
        mav.addObject("book", book);
        return mav;
    }

    @GetMapping("/searchCatalog")
    public String showBooks(Model model) {
        String keyword = null;
        String available = null;
        return listByPage(model,1, keyword, "title", "asc", available);
    }

    @GetMapping("/page/{pageNumber}")
    public String listByPage(Model model, @PathVariable("pageNumber") int currentPage,
                             @Param("keyword") String keyword,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("available") String available) {

        if(keyword != null) {
            keyword = keyword.toLowerCase();
            System.out.println(keyword);
        }


        Page<Book> page = bookService.listAllAvailable(currentPage, keyword, sortField, sortDir, available);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<Book> bookList = page.getContent();

        for (Book book: bookList) {
            if(book.getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(book.getCoverPhoto());
                book.setParsedData(image);
            }
        }

        System.out.println(keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("bookList", bookList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("available", available);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
//        mav.addObject(bookList);
        return "searchcatalog";

    }





}
