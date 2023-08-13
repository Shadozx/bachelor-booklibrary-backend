package com.shadoww.BookLibraryApp.services;

import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.BookMark;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.repositories.BookMarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookMarksService {

    private BookMarksRepository bookMarksRepository;


    @Autowired
    public BookMarksService(BookMarksRepository bookMarksRepository) {
        this.bookMarksRepository = bookMarksRepository;
    }


    /*public boolean existsBookMark(int chapter, int paragraph) {

        return bookMarksRepository.existsBookMarkByChapter_IdAndParagraph(chapter, paragraph);
    }*/

    public Optional<BookMark> findOne(int id) { return bookMarksRepository.findById(id);}

    public Optional<BookMark> findBookMark(int chapter, int paragraph) {

        return bookMarksRepository.findBookMarkByChapter_IdAndParagraph(chapter, paragraph);
    }

    public Optional<BookMark> findByCatalogAndBook(int catalogId, int bookId) {
        return bookMarksRepository.findByCatalog_IdAndBook_Id(catalogId, bookId);
    }

    public Optional<BookMark> findByBookAndPerson(Book book, Person person) {
        return bookMarksRepository.findByBookAndPerson(book, person);
    }

    @Transactional
    public void saveBookMark(BookMark bookMark) {

        if (bookMark != null) {

            save(bookMark);
        }

    }

    @Transactional
    public void save(BookMark bookMark) {
        bookMarksRepository.save(bookMark);
    }


    @Transactional
    public void deleteBookMark(int chapter, int paragraph) {
        bookMarksRepository.deleteBookMarkByChapter_IdAndParagraph(chapter, paragraph);
    }


    @Transactional
    public void update(BookMark updated, int id) {

        Optional<BookMark> forUpdate = findOne(id);

        if(forUpdate.isPresent()) {

            forUpdate.get().setPerson(updated.getPerson());
            forUpdate.get().setBook(updated.getBook());

            forUpdate.get().setChapter(updated.getChapter());
            forUpdate.get().setCatalog(updated.getCatalog());
            forUpdate.get().setParagraph(updated.getParagraph());

            save(forUpdate.get());
        }
    }
    @Transactional
    public void deleteById(int id) {
        bookMarksRepository.deleteById(id);
    }


    @Transactional
    public void delete(BookMark bookMark) {
        bookMarksRepository.delete(bookMark);
    }
}
