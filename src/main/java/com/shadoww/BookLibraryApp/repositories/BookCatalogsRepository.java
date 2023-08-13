package com.shadoww.BookLibraryApp.repositories;

import com.shadoww.BookLibraryApp.models.BookCatalog;

import com.shadoww.BookLibraryApp.models.user.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCatalogsRepository extends JpaRepository<BookCatalog, Integer> {


    List<BookCatalog> findBookCatalogByPerson(Person person);


    Optional<BookCatalog> findBookCatalogByIdAndPerson(int id, Person person);
}
