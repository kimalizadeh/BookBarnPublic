package com.example.bookbarnproject.service;


import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.repository.BookRepository;
import com.example.bookbarnproject.repository.RentalRepoPagination;
import com.example.bookbarnproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminService {

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private RentalRepoPagination rentalRepoPagination;


    public Page<Rental> listAllRentals(int pageNumber, String keyword, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 2);



        if(keyword != null){
            return rentalRepoPagination.findAll(keyword, pageable);
        }

        return rentalRepoPagination.findAll(pageable);
    }

}
