package com.example.bookbarnproject.controller;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Pdf;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import com.example.bookbarnproject.model.AddRentalModel;
import com.example.bookbarnproject.model.UpdateBookModel;
import com.example.bookbarnproject.model.UpdateRentalModel;
import com.example.bookbarnproject.model.UpdateUserModel;
import com.example.bookbarnproject.repository.BookRepository;
import com.example.bookbarnproject.repository.PdfRepository;
import com.example.bookbarnproject.repository.RentalRepository;
import com.example.bookbarnproject.repository.UserRepository;
import com.example.bookbarnproject.service.AdminService;
import com.example.bookbarnproject.service.BookService;
import com.example.bookbarnproject.service.EmailSenderService;
import com.example.bookbarnproject.service.UserService;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.jsoup.Jsoup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;


@Controller
@ControllerAdvice
public class AdminController{

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;


    @Autowired
    private PdfRepository pdfRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private EmailSenderService service;


    //@GetMapping({"admin/manageBooks"})
    /*@GetMapping({"admin/manageBooks"})
    public ModelAndView showBooks() {
        ModelAndView mav = new ModelAndView("admin/manage_books");
        List<Book> list = bookRepository.findAllByDeprecatedFalse();
        mav.addObject("books", list);
        return mav;
    }*/

    @GetMapping("/admin/manageBooks")
    public String showBooksToAdmin(Model model) {
        String keyword = null;
        return listByPage(model, 1, keyword, "title", "asc");

    }

    @GetMapping("/admin/page/{pageNumber}")
    public String listByPage(Model model, @PathVariable("pageNumber") int currentPage,
                             @Param("keyword") String keyword,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir) {


        Page<Book> page = bookService.listAll(currentPage, keyword, sortField, sortDir);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<Book> bookList = page.getContent();

        for (Book book: bookList) {
            if(book.getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(book.getCoverPhoto());
                book.setParsedData(image);
            }
            String newBody = book.getDescription().substring(0, Math.min(book.getDescription().length(), 50)) + "...";
            book.setDescription(newBody);
        }


        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("bookList", bookList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
//        mav.addObject(bookList);
        return "admin/manage_books";

    }

    @GetMapping("/admin/dashboard")
    public ModelAndView getAdminDashboard(Principal principal)  {
        ModelAndView mav = new ModelAndView("admin/dashboard");
        Optional<User> admin = userRepository.findByUsername(principal.getName());
        User currAdmin = admin.get();
        System.out.println(currAdmin);
        mav.addObject("admin",currAdmin);
        return mav;
    }



    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {

        // Convert multipart object to byte[]
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @GetMapping("/admin/addBook")
    public ModelAndView getAddBook() {
        ModelAndView mav = new ModelAndView("admin/addbook");
        Book book = new Book();
        mav.addObject("book", book);
        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileUploadError(RedirectAttributes redirAttrs) {
        redirAttrs.addFlashAttribute("error", "File cannot exceed 2MB");
        return "redirect:/admin/manageBooks";
    }


    @PostMapping("/admin/addBook")
    public String addBook(@Valid Book book, BindingResult bindingResult,
                          RedirectAttributes redirAttrs,
                          @RequestParam("coverPhoto") MultipartFile multipartFile1,
                          @RequestParam("ebookFile") MultipartFile multipartFile2) throws IOException {

        byte[] file1 = multipartFile1.getBytes();
        byte[] file2 = multipartFile2.getBytes();

        Long maxImageSize = 1024*1024L;


        if(book.getBookType().toString() == "PAPER" && multipartFile2.getOriginalFilename().length() != 0) {
            FieldError publicationDate = new FieldError("book", "pdf", "Book type must be E-Book if you wish to upload a file");
            bindingResult.addError(publicationDate);
        }

        if(book.getBookType().toString() == "EBOOK" && multipartFile2.getOriginalFilename().length() == 0) {
            FieldError publicationDate = new FieldError("book", "pdf", "You must upload an E-book");
            bindingResult.addError(publicationDate);
        }
        if(book.getPublicationDate() == null){
            FieldError publicationDate = new FieldError("book", "publicationDate", "You must select a date");
            bindingResult.addError(publicationDate);

        }

        if(multipartFile1.getOriginalFilename().length() > 0) {
            String extension1 = StringUtils.getFilenameExtension(multipartFile1.getOriginalFilename());
            if(!extension1.equals("jpg")){
                FieldError publicationDate = new FieldError("book", "coverPhoto", "Only jpg files are accepted");
                bindingResult.addError(publicationDate);
            }
        }
        if(multipartFile2.getOriginalFilename().length() > 0){
            String extension2 = StringUtils.getFilenameExtension(multipartFile2.getOriginalFilename());
            if(!extension2.equals("pdf")){
                FieldError publicationDate = new FieldError("book", "pdf", "Only pdf files are accepted");
                bindingResult.addError(publicationDate);
            }
        }

        if(multipartFile1.getSize() > maxImageSize){
            FieldError imageSize = new FieldError("book", "coverPhoto", "File size must not exceed 1MB.");
            bindingResult.addError(imageSize);
        }

        if(multipartFile2.getOriginalFilename().length() != 0) {
            if(pdfRepository.findByName(StringUtils.cleanPath(multipartFile2.getOriginalFilename())).isPresent()){
                FieldError publicationDate = new FieldError("book", "pdf", "File name already exists. Please rename your file.");
                bindingResult.addError(publicationDate);
            }
        }

        if(bookRepository.findByIsbn(book.getIsbn()).isPresent()){
            FieldError isbnTaken = new FieldError("book", "isbn", "ISBN is taken");
            bindingResult.addError(isbnTaken);
        }
        if(book.getPublicationDate() != null){
            if(book.getPublicationDate().isAfter(LocalDate.now())){
                FieldError publicationDate = new FieldError("books", "publicationDate", "Invalid publication date");
                bindingResult.addError(publicationDate);
            }
        }
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult.getAllErrors());
            return "admin/addbook";
        }

        bookRepository.save(book);

        Book insertedBook = bookRepository.findByIsbn(book.getIsbn()).get();

        if(multipartFile2.getOriginalFilename().length() != 0) {
            Pdf newPdf = new Pdf();
            newPdf.setFile(file2);
            newPdf.setSize(multipartFile2.getSize());
            newPdf.setName(StringUtils.cleanPath(multipartFile2.getOriginalFilename()));
            newPdf.setBook(insertedBook);
            pdfRepository.save(newPdf);
            Pdf pdf = pdfRepository.findByName(newPdf.getName()).get();
            insertedBook.setPdf(pdf);
        }
        if(multipartFile1.getOriginalFilename().length() != 0){
            insertedBook.setCoverPhoto(file1);

        }
        insertedBook.setDescription(Jsoup.clean(insertedBook.getDescription(), Safelist.basic()));
        insertedBook.setFrontPageOrder(0);
        insertedBook.setAvailable(true);
        insertedBook.setDeprecated(false);
        redirAttrs.addFlashAttribute("bookAdded", "Book successfully added");
        bookRepository.save(insertedBook);
        return "redirect:/admin/manageBooks";
    }


    @GetMapping("/admin/updateBook")
    public ModelAndView updateBook(@RequestParam Long bookId) {
        ModelAndView mav = new ModelAndView("admin/updatebook");
        Book book = bookRepository.findById(bookId).get();
        UpdateBookModel updateBookModel = new UpdateBookModel();
        updateBookModel.setId(bookId);
        updateBookModel.setTitle(book.getTitle());
        updateBookModel.setAuthorFullName(book.getAuthorFullName());
        updateBookModel.setIsbn(book.getIsbn());
        updateBookModel.setCategory(book.getCategory());
        updateBookModel.setBookType(book.getBookType());
        updateBookModel.setDescription(book.getDescription());
        updateBookModel.setPublicationDate(book.getPublicationDate());
        System.out.println(updateBookModel);
        mav.addObject(updateBookModel);
        return mav;
    }


    @PostMapping("/admin/updateBook")
    public String updateBook(@Valid UpdateBookModel updateBookModel, BindingResult bindingResult, RedirectAttributes redirAttrs,
                          @RequestParam("coverPhoto") MultipartFile multipartFile1, @RequestParam("ebookFile") MultipartFile multipartFile2) throws IOException {

        Long maxSize = 1L*1024*1024;

        byte[] file1 = multipartFile1.getBytes();
        byte[] file2 = multipartFile2.getBytes();

        Book oldBook = bookRepository.findById(updateBookModel.getId()).get();

        if(!oldBook.getIsbn().equals(updateBookModel.getIsbn())){
            if(bookRepository.findByIsbn(updateBookModel.getIsbn()).isPresent()) {
                FieldError nameTaken = new FieldError("isbn", "isbn", "ISBN is taken");
                bindingResult.addError(nameTaken);
            }
        }

        if(updateBookModel.getBookType().toString() == "EBOOK" && oldBook.getBookType().toString() == "PAPER" && multipartFile2.getOriginalFilename().length() == 0){
            FieldError publicationDate = new FieldError("book", "pdf", "You must upload an E-book");
            bindingResult.addError(publicationDate);
        }

        if(updateBookModel.getBookType().toString() == "EBOOK" && updateBookModel.isUpdatePdf() && multipartFile2.getOriginalFilename().length() == 0) {
            FieldError publicationDate = new FieldError("book", "pdf", "You must upload an E-book");
            bindingResult.addError(publicationDate);
        }

        if(updateBookModel.getPublicationDate() == null){
            FieldError publicationDate = new FieldError("book", "publicationDate", "You must select a date");
            bindingResult.addError(publicationDate);
        }
        if(multipartFile1.getOriginalFilename().length() > 0) {
            String extension1 = StringUtils.getFilenameExtension(multipartFile1.getOriginalFilename());
            if(!extension1.equals("jpg")){
                FieldError publicationDate = new FieldError("book", "coverPhoto", "Only jpg files are accepted");
                bindingResult.addError(publicationDate);
            }
        }
        if(multipartFile2.getOriginalFilename().length() > 0){
            String extension2 = StringUtils.getFilenameExtension(multipartFile2.getOriginalFilename());
            if(!extension2.equals("pdf")){
                FieldError publicationDate = new FieldError("book", "pdf", "Only pdf files are accepted");
                bindingResult.addError(publicationDate);
            }
        }

        if(multipartFile1.getSize() > maxSize){
            FieldError imageSize = new FieldError("book", "coverPhoto", "File size must not exceed 1MB.");
            System.out.println("Image size error");
            bindingResult.addError(imageSize);

        }

        if(pdfRepository.findByName(StringUtils.cleanPath(multipartFile2.getOriginalFilename())).isPresent()){
            FieldError publicationDate = new FieldError("book", "pdf", "File name already exists. Please rename your file.");
            bindingResult.addError(publicationDate);
        }

        if(updateBookModel.getPublicationDate() != null){
            if(updateBookModel.getPublicationDate().isAfter(LocalDate.now())){
                FieldError publicationDate = new FieldError("books", "publicationDate", "Invalid publication date");
                bindingResult.addError(publicationDate);
            }
        }

        if(!updateBookModel.isUpdateCoverPhoto() && multipartFile1.getOriginalFilename().length() != 0) {
            FieldError publicationDate = new FieldError("book", "coverPhoto", "You must check the box for updating the cover photo");
            bindingResult.addError(publicationDate);

        }

        if(!updateBookModel.isUpdatePdf() && multipartFile2.getOriginalFilename().length() != 0) {
            FieldError publicationDate = new FieldError("book", "pdf", "You must check the box for updating the ebook file");
            bindingResult.addError(publicationDate);

        }

        Book book = bookRepository.findById(updateBookModel.getId()).get();


        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/updatebook";
        }


        if(multipartFile2.getOriginalFilename().length() != 0 && updateBookModel.isUpdatePdf() && updateBookModel.getBookType() == Book.BookType.EBOOK) {
            Pdf newPdf = new Pdf();

            if(book.getPdf() != null) {
                newPdf = book.getPdf();
            }

            newPdf.setFile(file2);
            newPdf.setSize(multipartFile2.getSize());
            newPdf.setName(StringUtils.cleanPath(multipartFile2.getOriginalFilename()));
            newPdf.setBook(book);
            pdfRepository.save(newPdf);
            Pdf pdf = pdfRepository.findByName(newPdf.getName()).get();
            book.setPdf(pdf);
        }
        if(updateBookModel.isUpdateCoverPhoto()){
            book.setCoverPhoto(updateBookModel.getCoverPhoto());
        }

        book.setTitle(updateBookModel.getTitle());
        book.setAuthorFullName(updateBookModel.getAuthorFullName());
        book.setIsbn(updateBookModel.getIsbn());
        book.setDescription(Jsoup.clean(book.getDescription(), Safelist.basic()));
        book.setBookType(updateBookModel.getBookType());
        book.setCategory(updateBookModel.getCategory());
        book.setPublicationDate(updateBookModel.getPublicationDate());

        redirAttrs.addFlashAttribute("bookUpdated", "Book successfully updated");

        bookRepository.save(book);
        return "redirect:/admin/manageBooks";
    }

    @GetMapping("/admin/deleteBook")
    public String deleteBook(@RequestParam Long bookId, RedirectAttributes redirAttrs){
        Book book = bookRepository.findById(bookId).get();
        List<Rental> rental = rentalRepository.findByBook_IdAndAndReturnDateIsNull(bookId);
        if(!rental.isEmpty()){
            redirAttrs.addFlashAttribute("bookNotDeleted", "WARNING: Cannot delete! This book is rented");
        }
        else {
            book.setDeprecated(true);
            bookRepository.save(book);
            redirAttrs.addFlashAttribute("bookDeleted", "Book successfully deleted");
            //bookRepository.deleteById(bookId);
        }
        return "redirect:/admin/manageBooks";
    }



        /**@GetMapping({"/admin/manageUsers", "/admin"})
        public ModelAndView showUsers () {
            ModelAndView mav = new ModelAndView("admin/manage_users");
            List<User> list = userRepository.findAllByDeprecatedFalse();
            mav.addObject("users", list);
            return mav;
        }*/

        //=========================== Sorting Search and Showing users to the admin ================//

    @GetMapping({"/admin/manageUsers","/admin"})
    public String showUsers(Model model) {

        String keyword = null;
        return listUsersByPage(model, 1, keyword, "username", "asc");

    }

    @GetMapping("/admin/showUsersPage/{pageNumber}")
    public String listUsersByPage(Model model, @PathVariable("pageNumber") int currentPage,
                             @Param("keyword") String keyword,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir) {


        Page<User> page = userService.listAllUsers(currentPage, keyword, sortField, sortDir);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<User> userList = page.getContent();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("userList", userList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
//        mav.addObject(bookList);
        return "admin/manage_users";

    }

        @GetMapping("/admin/register")
        public ModelAndView registerPage () {
            ModelAndView mav = new ModelAndView("admin/register");
            User newUser = new User();
            mav.addObject("user", newUser);
            return mav;
        }

        @PostMapping("/admin/register")
        public String register (@Valid User user, BindingResult bindingResult, RedirectAttributes redirAttrs){

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                FieldError nameTaken = new FieldError("user", "username", "Username is taken");
                bindingResult.addError(nameTaken);
            }

            if (!user.getPassword().equals(user.getPasswordRepeat())) {
                FieldError pwRepeat = new FieldError("user", "passwordRepeat", "Passwords do not match");
                bindingResult.addError(pwRepeat);
            }

            if (user.getEmail().isEmpty()){
                FieldError email = new FieldError("user", "email", "Please Enter your email");
                bindingResult.addError(email);
            }

            if (bindingResult.hasErrors()) {
                System.out.println(bindingResult.getAllErrors());
                return "admin/register";
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setDeprecated(false);
            redirAttrs.addFlashAttribute("userCreated", "User successfully created");
            userRepository.save(user);
            return "redirect:/admin/manageUsers";
        }


    @GetMapping("/admin/updateUserForm")
    public ModelAndView updateUser (@RequestParam Long userId){
        ModelAndView mav = new ModelAndView("admin/updateuser");
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
        updateUserModel.setRole(user.getRole());
        mav.addObject("updateUserModel", updateUserModel);
        return mav;
    }

    @PostMapping("/admin/updateUser")
    public String update (@Valid UpdateUserModel updateUserModel, BindingResult bindingResult, RedirectAttributes redirAttrs){

        User user1 = userRepository.findById(updateUserModel.getId()).get();

        if(!user1.getUsername().equals(updateUserModel.getUsername())){
            if(userRepository.findByUsername(updateUserModel.getUsername()).isPresent()) {
                FieldError nameTaken = new FieldError("user", "username", "Username is taken");
                bindingResult.addError(nameTaken);
            }
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/updateuser";
        }

        if(updateUserModel.getPassword().length() != 0) {
            if (!updateUserModel.getPassword().equals(updateUserModel.getPasswordRepeat())) {
                FieldError pwRepeat = new FieldError("user", "passwordRepeat", "Passwords do not match");
                bindingResult.addError(pwRepeat);
            }
        }

        if(updateUserModel.getAge() < 0 || updateUserModel.getAge() > 150 ) {
            FieldError age = new FieldError("user", "age", "Members should be between 0 and 150 years old");
            bindingResult.addError(age);
        }
        if(updateUserModel.getEmail().isEmpty()){
            FieldError email = new FieldError("user", "email", "Members should have an email address");
            bindingResult.addError(email);
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/updateuser";
        }

//        if(updateUserModel.getPassword().length() != 0) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String encodedPassword = passwordEncoder.encode(updateUserModel.getPassword());
//            user1.setPassword(encodedPassword);
//        }

        user1.setUsername(updateUserModel.getUsername());
        user1.setFirstName(updateUserModel.getFirstName());
        user1.setLastName(updateUserModel.getLastName());
        user1.setEmail(updateUserModel.getEmail());
        user1.setAddress(updateUserModel.getAddress());
        user1.setAge(updateUserModel.getAge());
        user1.setSex(updateUserModel.getSex());
        user1.setRole(updateUserModel.getRole());

        redirAttrs.addFlashAttribute("userUpdated", "User is updated successfully");

        userRepository.save(user1);
        return "redirect:/admin/manageUsers";
    }

        @GetMapping("/admin/deleteUser")
        public String deleteUser (@RequestParam Long userId,RedirectAttributes redirAttrs) {
            User user = userRepository.findById(userId).get();
            List<Rental> rental = rentalRepository.findByUserUsernameAndReturnDateIsNull(user.getUsername());
            if (!rental.isEmpty()) {
                redirAttrs.addFlashAttribute("userNotDeleted", "WARNING: Cannot delete! This user has not returned a rented book!");
            } else {
                user.setDeprecated(true);
                userRepository.save(user);
            redirAttrs.addFlashAttribute("userDeleted", "User successfully deleted");
            //userRepository.deleteById(userId);
        }
            return "redirect:/admin/manageUsers";
        }


    //=========================== Sorting Search and Showing Rentals to the admin ================//
    @GetMapping("/admin/manageRentals")
    public String showRentals(Model model) {

        String keyword = null;
        return listRentalByPage(model, 1, keyword, "startDate", "asc");

    }

    @GetMapping("/admin/showRentalsPage/{pageNumber}")
    public String listRentalByPage(Model model, @PathVariable("pageNumber") int currentPage,
                                  @Param("keyword") String keyword,
                                  @Param("sortField") String sortField,
                                  @Param("sortDir") String sortDir) {


        Page<Rental> page = adminService.listAllRentals(currentPage, keyword, sortField, sortDir);
        Long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<Rental> rentalList = page.getContent();

        for (Rental rental: rentalList) {
            if(rental.getBook().getCoverPhoto() != null) {
                String image = Base64.getEncoder().encodeToString(rental.getBook().getCoverPhoto());
                rental.getBook().setParsedData(image);
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
        return "admin/manage_rentals";

    }





//    @GetMapping("/admin/addRental")
//    public ModelAndView getAddRental() {
//        ModelAndView mav = new ModelAndView("admin/addrental");
//        AddRentalModel addRentalModel = new AddRentalModel();
//        mav.addObject(addRentalModel);
//        return mav;
//    }

    @GetMapping("/admin/addRental")
    public ModelAndView getAddRental(@RequestParam Long bookId) {
        ModelAndView mav = new ModelAndView("admin/addrental");
        Book book = bookRepository.findById(bookId).get();
        AddRentalModel addRentalModel = new AddRentalModel();
        addRentalModel.setIsbn(book.getIsbn());
        addRentalModel.setTitle(book.getTitle());
        addRentalModel.setBookId(bookId);
        addRentalModel.setAuthorFullName(book.getAuthorFullName());
        mav.addObject(addRentalModel);
        return mav;
    }

    @PostMapping("/admin/addRental")
    public String addRental(@Valid AddRentalModel addRentalModel, BindingResult bindingResult, RedirectAttributes redirAttrs){

        if (!userRepository.findByUsername(addRentalModel.getUsername()).isPresent()) {
            FieldError nameTaken = new FieldError("username", "username", "Username does not exist");
            bindingResult.addError(nameTaken);
        }

        if(addRentalModel.getStartDate() == null){
            FieldError startDate = new FieldError("book", "startDate", "You must select a start date");
            bindingResult.addError(startDate);
        }
        if(addRentalModel.getDueDate() == null){
            FieldError dueDate = new FieldError("book", "dueDate", "You must select a due date");
            bindingResult.addError(dueDate);
        }
//        if(addRentalModel.getStartDate() != null){
//            if(addRentalModel.getStartDate().isAfter(LocalDate.now())){
//                FieldError startDate = new FieldError("books", "startDate", "Invalid start date");
//                bindingResult.addError(startDate);
//            }
//        }
        if(addRentalModel.getDueDate() != null){
            if(addRentalModel.getDueDate().isBefore(addRentalModel.getStartDate())){
                FieldError dueDate = new FieldError("books", "dueDate", "Due date cannot be before start date");
                bindingResult.addError(dueDate);
            }
        }

        if (!bookRepository.findByIsbn(addRentalModel.getIsbn()).isPresent()) {
            FieldError nameTaken = new FieldError("isbn", "isbn", "ISBN does not exist");
            bindingResult.addError(nameTaken);
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/addrental";
        }
        Book book = bookRepository.findByIsbn(addRentalModel.getIsbn()).get();

        if(!book.isAvailable()) {
            FieldError nameTaken = new FieldError("addRentalModel", "isbn", "This book is unavailable");
            bindingResult.addError(nameTaken);
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/addrental";
        }
        User user = userRepository.findByUsername(addRentalModel.getUsername()).get();

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBook(book);
        rental.setStartDate(addRentalModel.getStartDate());
        rental.setDueDate(addRentalModel.getDueDate());

        book.setAvailable(false);
        rentalRepository.save(rental);
        bookRepository.save(book);
        redirAttrs.addFlashAttribute("rentalAdded", "A book was rented succesfully for a user!");

        service.sendSimpleEmail("bookbarn.fsd01@gmail.com", user.getUsername(), book.getTitle(), book.getAuthorFullName(), rental.getDueDate().toString());

        return "redirect:/admin/manageRentals";
    }

    @GetMapping("/admin/deleteRental")
    public String deleteRental(@RequestParam Long rentalId, RedirectAttributes redirAttrs){

        Rental rental = rentalRepository.findById(rentalId).get();
        Book book = rental.getBook();


//        if(!rentalRepository.findByBookIsbnAndReturnDateIsNotNull(book.getIsbn()).isPresent()) {
//            book.setAvailable(true);
//        }

        if(rentalRepository.findByBookIsbnAndReturnDateIsNull(book.getIsbn()).isPresent()) {
            redirAttrs.addFlashAttribute("rentalNotDeleted", "Rental cannot be deleted while book is not returned!");

        } else {
            book.setAvailable(true);
            bookRepository.save(book);
            rentalRepository.deleteById(rentalId);
            redirAttrs.addFlashAttribute("rentalDeleted", "Rental was deleted!");

        }

        return "redirect:/admin/manageRentals";
    }

    @GetMapping("/admin/updateRental")
    public ModelAndView getUpdateRental(@RequestParam Long rentalId) {
        ModelAndView mav = new ModelAndView("admin/updaterental");
        Rental rental = rentalRepository.findById(rentalId).get();
        UpdateRentalModel updateRentalModel = new UpdateRentalModel();
        updateRentalModel.setUsername(rental.getUser().getUsername());
        updateRentalModel.setIsbn(rental.getBook().getIsbn());
        updateRentalModel.setStartDate(rental.getStartDate());
        updateRentalModel.setDueDate(rental.getDueDate());
        updateRentalModel.setReturnDate(rental.getReturnDate());
        updateRentalModel.setTitle(rental.getBook().getTitle());
        updateRentalModel.setId(rentalId);
        updateRentalModel.setAuthorFullName(rental.getBook().getAuthorFullName());
        mav.addObject(updateRentalModel);
        return mav;

    }

    @PostMapping("/admin/updateRental")
    public String updateRental(@Valid UpdateRentalModel updateRentalModel, BindingResult bindingResult, RedirectAttributes redirAttrs){

        if (!userRepository.findByUsername(updateRentalModel.getUsername()).isPresent()) {
            FieldError nameTaken = new FieldError("username", "username", "Username does not exist");
            bindingResult.addError(nameTaken);
        }

        if(updateRentalModel.getStartDate() == null){
            FieldError startDate = new FieldError("startDate", "startDate", "You must select a start date");
            bindingResult.addError(startDate);
        }

        if(updateRentalModel.getDueDate() == null) {
            FieldError dueDate = new FieldError("emptyDueDate", "dueDate", "You must select a date");
            bindingResult.addError(dueDate);
        }
        if(updateRentalModel.getDueDate() != null) {
            if(updateRentalModel.getDueDate().isBefore(updateRentalModel.getStartDate())){
                FieldError dueDate = new FieldError("dueDate", "dueDate", "Due date should not be before start date");
                bindingResult.addError(dueDate);
            }
        }
        if(updateRentalModel.getStartDate() != null && updateRentalModel.getReturnDate() != null) {
            if(updateRentalModel.getReturnDate().isBefore(updateRentalModel.getStartDate())){
                FieldError returnDate = new FieldError("returnDate", "returnDate", "Return date should not be before start date");
                bindingResult.addError(returnDate);
            }
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "admin/updaterental";
        }

        Book book = bookRepository.findByIsbn(updateRentalModel.getIsbn()).get();
        User user = userRepository.findByUsername(updateRentalModel.getUsername()).get();
        Rental rental = rentalRepository.findById(updateRentalModel.getId()).get();


        rental.setUser(user);
        rental.setBook(book);
        rental.setStartDate(updateRentalModel.getStartDate());
        rental.setDueDate(updateRentalModel.getDueDate());
        rental.setReturnDate(updateRentalModel.getReturnDate());

        if(rental.getReturnDate() != null) {
            book.setAvailable(true);
        } else {
            book.setAvailable(false);
        }

        rentalRepository.save(rental);
        bookRepository.save(book);
        redirAttrs.addFlashAttribute("rentalUpdated", "Rental was updated!");

        return "redirect:/admin/manageRentals";
    }


}
