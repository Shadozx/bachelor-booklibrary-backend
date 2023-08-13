package com.shadoww.BookLibraryApp.services;

import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.repositories.BookCatalogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookCatalogsService {

    private BookCatalogsRepository bookCatalogsRepository;

    @Autowired
    public BookCatalogsService(BookCatalogsRepository bookCatalogsRepository) {
        this.bookCatalogsRepository = bookCatalogsRepository;
    }


    public Optional<BookCatalog> findById(int id) {
        return bookCatalogsRepository.findById(id);
    }

    public Optional<BookCatalog> findByIdAndPerson(int id, Person person) {
        return bookCatalogsRepository.findBookCatalogByIdAndPerson(id, person);
    }

    public List<BookCatalog> findByPerson(Person person) {

        return bookCatalogsRepository.findBookCatalogByPerson(person);
    }

    @Transactional
    public void save(BookCatalog bookCatalog) {
        bookCatalogsRepository.save(bookCatalog);
    }


    @Transactional
    public void delete(BookCatalog bookCatalog) {
        bookCatalogsRepository.delete(bookCatalog);
    }
    @Transactional
    public void deleteCatalog(int id) {
        bookCatalogsRepository.deleteById(id);
    }
}
