package com.example.bookbarnproject.service;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.repository.BookRepoPagination;
import com.example.bookbarnproject.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepoPagination bookRepoPagination;

    public Page<Book> listAllAvailable(int pageNumber, String keyword, String sortField, String sortDir, String available) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 3, sort);

        if(available != null){
        if(keyword != null && available.equals("true")){
            return bookRepoPagination.findAllByDeprecatedFalseAndAvailable(keyword, pageable);
        }
        if(keyword != null && available.equals("false")){
            return bookRepoPagination.findAllByDeprecatedFalseAndAvailableIsFalse(keyword, pageable);
        }
        }
        if(keyword != null){
            return bookRepoPagination.findAllByDeprecatedFalse(keyword, pageable);
        }
        return bookRepoPagination.findAllByDeprecatedFalse(pageable);
    }

    public Page<Book> listAll(int pageNumber, String keyword, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);

        if(keyword != null){
            return bookRepoPagination.findAllByDeprecatedFalse(keyword, pageable);
        }
        return bookRepoPagination.findAllByDeprecatedFalse(pageable);
    }
}
