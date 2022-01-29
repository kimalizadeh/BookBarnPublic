package com.example.bookbarnproject.service;

import com.example.bookbarnproject.entity.Book;
import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.entity.User;
import com.example.bookbarnproject.repository.BookRepoPagination;
import com.example.bookbarnproject.repository.RentalRepoPagination;
import com.example.bookbarnproject.repository.UserRepoPagination;
import com.example.bookbarnproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private UserRepoPagination userRepoPagination;

    @Autowired
    private RentalRepoPagination rentalRepoPagination;


    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    public Page<User> listAllUsers(int pageNumber, String keyword, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);

        if(keyword != null){
            return userRepoPagination.findAllByDeprecatedFalse(keyword, pageable);
        }

        return userRepoPagination.findAllByDeprecatedFalse(pageable);
    }



}
