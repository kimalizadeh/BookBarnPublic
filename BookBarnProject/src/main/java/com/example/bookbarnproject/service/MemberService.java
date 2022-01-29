package com.example.bookbarnproject.service;

import com.example.bookbarnproject.entity.Rental;
import com.example.bookbarnproject.repository.RentalRepoPagination;
import com.example.bookbarnproject.repository.RentalRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Service
@AllArgsConstructor
public class MemberService {

    @Autowired
    private final RentalRepository rentalRepository;

    @Autowired
    private RentalRepoPagination rentalRepoPagination;

  public Page<Rental> listUserRentals(Principal principal ,String keyword, int pageNumber, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);

        if(keyword != null){
            return rentalRepoPagination.findByUserUsernameOrderByStartDateDesc(keyword, principal.getName(),pageable);
        }

        return rentalRepoPagination.findByUserUsernameOrderByStartDateDesc(principal.getName(),pageable);
    }

   /*public Page<Rental> listUserRentals(Principal principal, int pageNumber) {

       Pageable pageable = PageRequest.of(pageNumber - 1, 2);

       return rentalRepoPagination.findByUserUsernameOrderByStartDateDesc(principal.getName(),pageable);
   }*/
}
