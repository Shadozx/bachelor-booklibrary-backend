package com.shadoww.BookLibraryApp.services;

import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.models.user.Role;
import com.shadoww.BookLibraryApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private PeopleRepository peopleRepository;

    private PasswordEncoder passwordEncoder;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Person> findALl(int page) {
        return peopleRepository.findAll(PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "username")));
    }

    public Page<Person> findByUsername(String username, int page) {
        return peopleRepository.findByUsernameContainingIgnoreCase(PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "username")), username);
    }

    public Optional<Person> findById(int id) {
        return peopleRepository.findById(id);
    }


    public boolean existByUsername(String username) {
        return peopleRepository.existsByUsernameIgnoreCase(username);
    }

    @Transactional
    public void savePerson(Person user) {
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
    }

    @Transactional
    public void update(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        save(person);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }


    @Transactional
    public void delete(Person person) {
        peopleRepository.delete(person);
    }
    @Transactional
    public void deletePerson(int id) {
        peopleRepository.deleteById(id);
    }
}

