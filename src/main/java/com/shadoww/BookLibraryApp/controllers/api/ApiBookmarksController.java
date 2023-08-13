package com.shadoww.BookLibraryApp.controllers.api;


import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.BookMark;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.security.PersonDetails;
import com.shadoww.BookLibraryApp.services.BookCatalogsService;
import com.shadoww.BookLibraryApp.services.BookMarksService;
import com.shadoww.BookLibraryApp.services.BooksService;
import com.shadoww.BookLibraryApp.services.ChaptersService;
import com.shadoww.BookLibraryApp.util.responsers.ResponseBookMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookmarks")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class ApiBookmarksController {

    private final BooksService booksService;
    private final BookCatalogsService bookCatalogsService;

    private final ChaptersService chaptersService;

    private final BookMarksService bookMarksService;

    @Autowired
    public ApiBookmarksController(BooksService booksService, BookCatalogsService bookCatalogsService, ChaptersService chaptersService, BookMarksService bookMarksService) {
        this.booksService = booksService;
        this.bookCatalogsService = bookCatalogsService;
        this.chaptersService = chaptersService;
        this.bookMarksService = bookMarksService;
    }

    @PostMapping("/catalog/{catalogId}/book/{bookId}")
    public ResponseEntity addBookMark(Authentication authentication,
                                      @PathVariable int catalogId,
                                      @PathVariable int bookId) {

        System.out.println("Here!!!!!!");
        System.out.println("METHOD ADD BOOKMARK:" + catalogId + ". " + bookId);

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();




        Optional<BookCatalog> foundCatalog = bookCatalogsService.findById(catalogId);
        if (foundCatalog.isEmpty()) return new ResponseEntity("Не існує такий каталог для книжок", HttpStatus.NOT_FOUND);

        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) return new ResponseEntity("Не існує така книжка", HttpStatus.NOT_FOUND);

        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Optional<BookMark> foundMark = bookMarksService.findByBookAndPerson(foundBook.get(), personDetails.getPerson());


        BookMark bookMark = foundMark.orElseGet(BookMark::new);


        bookMark.setCatalog(foundCatalog.get());
        bookMark.setPerson(personDetails.getPerson());
        bookMark.setParagraph(0);
        bookMark.setBook(foundBook.get());

        Optional<Chapter> foundChapter = chaptersService.findFirstChapterByBook(foundBook.get());

        if (foundChapter.isEmpty()) return new ResponseEntity("Не існує така глава книжки", HttpStatus.NOT_FOUND);

        bookMark.setChapter(foundChapter.get());


        System.out.println("Done!" + bookMark);

        if (bookMark.isValid()) bookMarksService.save(bookMark);

        return ResponseBookMark.addSuccess();

    }

    @PutMapping("/")
    public ResponseEntity updateBookMark(Authentication authentication,
                                         @RequestParam("ch") int chapterId,
                                         @RequestParam(value = "par", defaultValue = "0") int paragraph) {

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();



        Optional<Chapter> foundChapter = chaptersService.findById(chapterId);
        if (foundChapter.isEmpty()) return new ResponseEntity("Не існує така глава книжки", HttpStatus.NOT_FOUND);


        Book book = foundChapter.get().getBook();
        if (book == null) return new ResponseEntity("Не існує така книжка", HttpStatus.NOT_FOUND);



        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Optional<BookMark> foundMark = bookMarksService.findByBookAndPerson(book, personDetails.getPerson());

        BookMark bookMark = null;

        if (foundMark.isPresent()) {

            bookMark = foundMark.get();
        }


        // якщо хочемо зробити закладку на рівні глави
        if (bookMark != null && bookMark.getParagraph() == paragraph) {
            System.out.println("Deleting bookMark");
            bookMark.setParagraph(0);
            bookMarksService.saveBookMark(bookMark);
//                        bookMarksService.deleteBookMark(chapterId, paragraph);
        }
        // якщо хочемо зробити закладку на рівні параграфу глави
        else if (bookMark != null) {

            System.out.println("Updating bookMark");
            bookMark.setChapter(foundChapter.get());
            bookMark.setBook(book);
            bookMark.setParagraph(paragraph);
            bookMark.setPerson(personDetails.getPerson());
            bookMarksService.saveBookMark(bookMark);
        }
        //
        else {
            System.out.println("Creating bookMark");
            BookMark newBookMark = new BookMark();
            newBookMark.setBook(book);
            newBookMark.setChapter(foundChapter.get());
            newBookMark.setParagraph(paragraph);
            newBookMark.setPerson(personDetails.getPerson());

            bookMarksService.saveBookMark(newBookMark);
        }

        return ResponseBookMark.addSuccess();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMark(@PathVariable int id) {
        System.out.println("DELETE METHOD BOOKMARK");
        bookMarksService.deleteById(id);

        return ResponseBookMark.deleteSuccess();
    }


}
