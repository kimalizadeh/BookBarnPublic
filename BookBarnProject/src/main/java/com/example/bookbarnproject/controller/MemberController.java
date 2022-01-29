package com.example.bookbarnproject.controller;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import com.example.bookbarnproject.model.UpdateUserModel;
import com.example.bookbarnproject.repository.BookRepository;
import com.example.bookbarnproject.repository.RentalRepository;
import com.example.bookbarnproject.repository.UserRepository;
import com.example.bookbarnproject.service.EmailSenderService;
import com.example.bookbarnproject.service.EmailSenderService;
import com.example.bookbarnproject.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class MemberController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EmailSenderService service;

    @Autowired
    private MemberService memberService;


   /* @GetMapping({"/member","/member/dashboard"})
    public  ModelAndView memberDashboard(Principal principal) {
        ModelAndView mav = new ModelAndView("member/dashboard");
        List<Rental> list = rentalRepository.findByUserUsernameAndReturnDateIsNull(principal.getName());

        for (Rental rental: list) {
            if(rental.getBook().getBookType() == Book.BookType.EBOOK) {
                rental.getBook().setEbook(true);
            } else {
                rental.getBook().setEbook(false);
            }
            if(rental.getDueDate().isBefore(LocalDate.now())) {
                rental.setPassedDueDate(true);
            } else {
                rental.setPassedDueDate(false);
            }
        }
        mav.addObject("rentals", list);
        return mav;
    }*/

    @GetMapping("/member/rentBook")
    public ModelAndView getRentBook(@RequestParam Long bookId) {
        ModelAndView mav = new ModelAndView("rentbook");
        Book book = bookRepository.findById(bookId).get();
        mav.addObject("book", book);

        return mav;
    }

    @PostMapping("/member/rentBook")
    public String rentBook(Book book, Principal principal, RedirectAttributes redirAttrs){

        User user = userRepository.findByUsername(principal.getName()).get();
        Book bookUpdated = bookRepository.findById(book.getId()).get();
        List<Rental> history = rentalRepository.findByUserUsernameAndBook_IdAndReturnDateIsNull(principal.getName(), book.getId());
        if(!history.isEmpty()){
            redirAttrs.addFlashAttribute("alreadyRented", "WARNING: Cannot rent this book! You already have it in your account");
        }
        else {
            Rental rental = new Rental();
            rental.setBook(book);
            rental.setUser(user);
            rental.setStartDate(LocalDate.now());
            rental.setDueDate(LocalDate.now().plusMonths(1));

            bookUpdated.setAvailable(false);
            rentalRepository.save(rental);
            bookRepository.save(bookUpdated);

            service.sendSimpleEmail("bookbarn.fsd01@gmail.com", principal.getName(), book.getTitle(),
                    book.getAuthorFullName(), rental.getDueDate().toString());

            redirAttrs.addFlashAttribute("bookRented", "You have rented a book! The Due Date is: " + rental.getDueDate().toString());
        }
        return "redirect:/member";
    }

    @GetMapping("/member/ebook")
    public void downloadFile(@Param("id") Long id, HttpServletResponse response) throws IOException {
        Optional<Book> result =  bookRepository.findById(id);

        if(!result.isPresent()) {
            throw new IOException("Could not find document with id:" + id);
        }
        Book book = result.get();
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=" + book.getIsbn()+ ".pdf";
        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(book.getPdf().getFile());
        outputStream.close();

    }

    @GetMapping("/member/returnRental")
    public String returnRental(@RequestParam Long rentalId, RedirectAttributes redirAttrs) {
        Rental rental = rentalRepository.findById(rentalId).get();
        Book book = rental.getBook();
        book.setAvailable(true);
        rental.setReturnDate(LocalDate.now());
        bookRepository.save(book);
        rentalRepository.save(rental);
        redirAttrs.addFlashAttribute("bookReturned", "Your book was returned!");
        return "redirect:/member";
    }

    @GetMapping("/member/personalInfo")
    public ModelAndView getMemberPersonalInfo(Principal principal)  {
        ModelAndView mav = new ModelAndView("member/personal_info");
        Optional<User> member = userRepository.findByUsername(principal.getName());
        User currMember = member.get();
        System.out.println(currMember);
        System.out.println(currMember);
        mav.addObject("member",currMember);
        return mav;
    }

    @GetMapping("member/updatePersonalInfoForm")
    public ModelAndView updateUser (@RequestParam Long userId){
        ModelAndView mav = new ModelAndView("member/update_personal_info");
        User user = userRepository.findById(userId).get();
        UpdateUserModel updateUserModel = new UpdateUserModel();
        updateUserModel.setId(user.getId());
        updateUserModel.setUsername(user.getUsername());
        updateUserModel.setFirstName(user.getFirstName());
        updateUserModel.setLastName(user.getLastName());
        updateUserModel.setEmail(user.getEmail());
        updateUserModel.setAddress(user.getAddress());
        updateUserModel.setAge(user.getAge());
        updateUserModel.setSex(user.getSex());
        mav.addObject("updateUserModel", updateUserModel);
        return mav;
    }

    @PostMapping("member/updatePersonalInfo")
    public String update (@Valid UpdateUserModel updateUserModel, BindingResult bindingResult, RedirectAttributes redirAttrs){

        User user1 = userRepository.findById(updateUserModel.getId()).get();

        if(!user1.getUsername().equals(updateUserModel.getUsername())){
            if(userRepository.findByUsername(updateUserModel.getUsername()).isPresent()) {
                FieldError nameTaken = new FieldError("user", "username", "Username is taken");
                bindingResult.addError(nameTaken);
            }
        }
//        if (bindingResult.hasErrors()) {
//            System.out.println(bindingResult.getAllErrors());
//            return "admin/updateuser";
//        }
        if(updateUserModel.getPassword().length() != 0) {
            if (!updateUserModel.getPassword().equals(updateUserModel.getPasswordRepeat())) {
                FieldError pwRepeat = new FieldError("user", "passwordRepeat", "Passwords do not match");
                bindingResult.addError(pwRepeat);
            }
        }
        if(updateUserModel.getAge() < 0 || updateUserModel.getAge() > 150) {
            FieldError age = new FieldError("user", "age", "Members should be between 0 and 150 years old");
            bindingResult.addError(age);
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "member/update_personal_info";
        }

        if(updateUserModel.getPassword().length() != 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(updateUserModel.getPassword());
            user1.setPassword(encodedPassword);
        }

        user1.setUsername(updateUserModel.getUsername());
        user1.setFirstName(updateUserModel.getFirstName());
        user1.setLastName(updateUserModel.getLastName());
        user1.setEmail(updateUserModel.getEmail());
        user1.setAddress(updateUserModel.getAddress());
        user1.setAge(updateUserModel.getAge());
        user1.setSex(updateUserModel.getSex());


        redirAttrs.addFlashAttribute("memberInfoUpdated", "Your personal info has been updated!");
        userRepository.save(user1);
        return "redirect:/member/personalInfo";
    }
    /**@GetMapping("/member/deleteAccount")
    public String deleteUser (@RequestParam Long userId){
        userRepository.deleteById(userId);
        return "redirect:/index";
    }*/

    //=============================MEMBER DASHBOARD RENTAL HISTORY============================//

    @GetMapping({"/member/dashboard","/member"})
    public String showRentals(Principal principal, Model model) {

        String keyword = null;
        return listRentalByPage(model, 1, keyword, principal ,"startDate", "desc");

    }

    @GetMapping("/member/dashboard/{pageNumber}")
    public String listRentalByPage(Model model, @PathVariable("pageNumber") int currentPage,
                                   @Param("keyword") String keyword,
                                   Principal principal,
                                   @Param("sortField") String sortField,
                                   @Param("sortDir") String sortDir) {


        Page<Rental> page = memberService.listUserRentals(principal, keyword, currentPage, sortField, sortDir);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<Rental> rentalList = page.getContent();

        for (Rental rental: rentalList) {
            if(rental.getBook().getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(rental.getBook().getCoverPhoto());
                rental.getBook().setParsedData(image);
            }
        }

        for (Rental rental: rentalList) {
            if (rental.getBook().getBookType() == Book.BookType.EBOOK) {
                rental.getBook().setEbook(true);
            } else {
                rental.getBook().setEbook(false);
            }
            if (rental.getDueDate().isBefore(LocalDate.now())) {
                rental.setPassedDueDate(true);
            } else {
                rental.setPassedDueDate(false);
            }
        }

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("rentalList", rentalList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
//        mav.addObject(bookList);
        return "member/dashboard";

    }

   /*@GetMapping({"/member/dashboard","/member"})
   public String showRentals(Model model,Principal principal) {

       String keyword = null;
       return listUserRentalByPage(model, 1,principal);

   }

    @GetMapping("/member/dashboard/{pageNumber}")
    public String listUserRentalByPage(Model model, @PathVariable("pageNumber") int currentPage,
                                   Principal principal) {


        Page<Rental> page = memberService.listUserRentals(principal,currentPage);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<Rental> rentalList = page.getContent();

        for (Rental rental: rentalList) {
            if(rental.getBook().getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(rental.getBook().getCoverPhoto());
                rental.getBook().setParsedData(image);
            }
        }

        for (Rental rental: rentalList) {
            if (rental.getBook().getBookType() == Book.BookType.EBOOK) {
                rental.getBook().setEbook(true);
            } else {
                rental.getBook().setEbook(false);
            }
            if (rental.getDueDate().isBefore(LocalDate.now())) {
                rental.setPassedDueDate(true);
            } else {
                rental.setPassedDueDate(false);
            }
        }

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("rentalList", rentalList);

//        mav.addObject(bookList);
        return "member/dashboard";

    }*/

}
