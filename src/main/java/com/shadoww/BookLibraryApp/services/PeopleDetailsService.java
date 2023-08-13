package com.shadoww.BookLibraryApp.services;


import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.models.user.Role;
import com.shadoww.BookLibraryApp.repositories.PeopleRepository;

import com.shadoww.BookLibraryApp.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class PeopleDetailsService implements UserDetailsService {

    private PeopleRepository peopleRepository;

    @Autowired
    public PeopleDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> foundUser = peopleRepository.findByUsername(username);

        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("User is not found!!!!");
        }
        return new PersonDetails(foundUser.get());
    }


}
