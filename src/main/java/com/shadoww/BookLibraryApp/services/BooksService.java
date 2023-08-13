package com.shadoww.BookLibraryApp.services;

import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.models.images.BookImage;
import com.shadoww.BookLibraryApp.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private BooksRepository booksRepository;


    private ImagesService imagesService;
    //
    private ChaptersService chaptersService;

    @Autowired
    public BooksService(BooksRepository booksRepository, ImagesService imagesService, ChaptersService chaptersService) {
        this.booksRepository = booksRepository;
        this.imagesService = imagesService;
        this.chaptersService = chaptersService;
    }

    public List<Book> findBooks() {
        return booksRepository.findAll();
    }

    public Page<Book> findBooksByPage(int page) {
        return booksRepository.findBooksByOrderByAddedDesc(PageRequest.of(page, 20));
    }

    public boolean existByTitle(String title) {
        return booksRepository.existsBookByTitle(title);
    }

    public Page<Book> findByTitle(String bookName, int page) {
        return booksRepository.findByTitleContainingIgnoreCase(PageRequest.of(page, 20), bookName);
    }

    public Optional<Book> findByUrl(String url) {
        return booksRepository.findByUploadedUrl(url);
    }

    public Optional<Book> findOne(int id) {
        return booksRepository.findById(id);
    }

    public List<Book> findLastBooks() {
        //return booksRepository.findFirst1OrderByAdded();
//        return booksRepository.findTop10(Sort.by(Sort.Direction.ASC, "added"));
        return booksRepository.findTop10ByOrderByAddedDesc();
//        return List.of();
    }


    public boolean exists(String uploadedUrl) {
        return booksRepository.existsBookByUploadedUrl(uploadedUrl);
    }


    @Transactional
    public Book save(Book book) {
        if (book != null) {
            book.setAdded(new Date());
            booksRepository.save(book);
        }
        return book;
    }


    @Transactional
    public void saveChapter(int bookId, Chapter newChapter) {
        Optional<Book> foundBook = findOne(bookId);

        if (foundBook.isPresent()) {

            foundBook.get().setAmount(foundBook.get().getAmount() + 1);

            newChapter.setBook(foundBook.get());


            chaptersService.save(newChapter);
            update(bookId, foundBook.get());
        }
    }

    @Transactional
    public void saveBookImage(Book book, BookImage bookImage) {
        if (book != null && bookImage != null) {

            save(book);

            updateBookImage(book, bookImage);
        }
    }

    @Transactional
    public void updateBookImage(Book book, BookImage bookImage) {
        book.setBookImage(bookImage);

        bookImage.setBook(book);

        imagesService.save(bookImage);

        save(book);
    }

    @Transactional
    public void saveBook(Book book, List<Chapter> chapters) {
        if (book != null && !chapters.isEmpty()) {
            book.setAmount(chapters.size());
            System.out.println("?");

            for (Chapter c : chapters) {
                c.setBook(book);
                System.out.println("Here");
                chaptersService.save(c);
            }

//            book.setChapters(chapters);


            save(book);

        } else {
            System.out.println("Глав немає");
        }
    }

    @Transactional
    public void saveBook(Book book, BookImage bookImage, List<Chapter> chapters) {

        if (book != null && bookImage != null && !chapters.isEmpty()) {

            save(book);


            book.setBookImage(bookImage);

//            bookImage.setBook(book);


            for (Chapter c : chapters) {
                c.setBook(book);
                System.out.println("Here");
                chaptersService.save(c);
            }

//            book.setChapters(chapters);

            book.setAmount(chapters.size());
            System.out.println("?");

            save(book);

            System.out.println("?");

            imagesService.save(bookImage);


        }
    }


    @Transactional
    public void update(int id, Book updatedBook) {

        Optional<Book> forUpdate = findOne(id);

        if (forUpdate.isPresent()) {

            forUpdate.get().setTitle(updatedBook.getTitle());
            forUpdate.get().setDescription(updatedBook.getDescription());

            save(forUpdate.get());
        }
    }

    @Transactional
    public void updateBookAndImage(int id, Book updated, BookImage bookImage) {

        Optional<Book> forUpdate = findOne(id);

        if (forUpdate.isPresent()) {
            if (bookImage != null) {
                forUpdate.get().setBookImage(bookImage);
//                bookImage.setBook(forUpdate.get());

                imagesService.save(bookImage);
            }
            if (!forUpdate.get().equals(updated)) {
                forUpdate.get().setTitle(updated.getTitle());
                forUpdate.get().setDescription(updated.getDescription());
            }

            save(forUpdate.get());
        }
    }

    @Transactional
    public void deleteBook(Book book) {


        booksRepository.delete(book);
    }


    @Transactional
    public void deleteChapter(Chapter chapter) {

        Book book = chapter.getBook();
        if (book != null) {
            book.setAmount(book.getAmount() - 1);

            save(book);
        }

        chaptersService.deleteOne(chapter);
    }


    @Transactional
    public void deleteAll() {
        booksRepository.deleteAll();
    }

}
