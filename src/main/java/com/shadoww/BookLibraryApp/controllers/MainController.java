package com.shadoww.BookLibraryApp.controllers;


import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.BookMark;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.security.PersonDetails;
import com.shadoww.BookLibraryApp.services.BookCatalogsService;
import com.shadoww.BookLibraryApp.services.BookMarksService;
import com.shadoww.BookLibraryApp.services.BooksService;
import com.shadoww.BookLibraryApp.services.ChaptersService;
import com.shadoww.BookLibraryApp.util.Counter;
import com.shadoww.BookLibraryApp.util.texformatters.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class MainController {

    private BooksService booksService;

    private ChaptersService chaptersService;

    private BookMarksService bookMarksService;

    private BookCatalogsService bookCatalogsService;


    //    private _222TextFormatter formatter = new _222TextFormatter();
    @Autowired
    public MainController(BooksService booksService, ChaptersService chaptersService, BookMarksService bookMarksService, BookCatalogsService bookCatalogsService) {
        this.booksService = booksService;
        this.chaptersService = chaptersService;
        this.bookMarksService = bookMarksService;
        this.bookCatalogsService = bookCatalogsService;
    }


    @GetMapping()
    public String home(Authentication authentication, Model model) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return "redirect:/auth/login";

        PersonDetails authUser = null;


        if (!(authentication.getPrincipal() instanceof User)) {
            authUser = (PersonDetails) authentication.getPrincipal();
        }


        model.addAttribute("authUser", authUser);

        model.addAttribute("books", booksService.findLastBooks());
        return "index/index";
    }

    @GetMapping("/catalog")
    public String showCatalog(Authentication authentication,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "search-text", required = false) String searchText,
                              Model model) {

        if (authentication == null) return "redirect:/auth/login";

        if (page < 1) return "redirect:/catalog";

        Page<Book> pageBooks;
        if(searchText != null && !searchText.equals("")) {
            pageBooks = booksService.findByTitle(searchText, page - 1);
            model.addAttribute("searchText", searchText);

        }
        else {
            pageBooks = booksService.findBooksByPage(page - 1);

            model.addAttribute("searchText", null);
        }


        PersonDetails authUser = null;


        if (authentication.getPrincipal() instanceof PersonDetails) {
            authUser = (PersonDetails) authentication.getPrincipal();
        }


        System.out.println("Books:" + pageBooks.getContent());

        model.addAttribute("authUser", authUser);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageBooks.getTotalPages());

        model.addAttribute("books", pageBooks.getContent().stream().filter(b -> b.getBookImage() != null).toList());

        return "books/catalog";

    }


    /*@GetMapping("/search")
    public String searchBooks(Authentication authentication,
                              @RequestParam("book-name") String bookName,
                              Model model) {


        if (authentication == null) return "redirect:/auth/login";

        PersonDetails authUser = null;


        if (!(authentication.getPrincipal() instanceof User)) {
            authUser = (PersonDetails) authentication.getPrincipal();
        }


        model.addAttribute("authUser", authUser);


        model.addAttribute("bookName", bookName);

        model.addAttribute("books", booksService.findByTitle(bookName));


        return "books/search";
    }
*/
    @GetMapping("/book/{id}")
    public String showBook(Authentication authentication,
                           @PathVariable int id,
                           Model model) {

        if (authentication == null) return "redirect:/auth/login";

        Optional<Book> book = booksService.findOne(id);

        if (book.isEmpty()) {
            model.addAttribute("message", "Такої книжки не існує...");

            return "errors/error";
        }

        BookMark bookMark = null;
        PersonDetails authUser = null;


        List<BookCatalog> catalogs = null;


        if (authentication.getPrincipal() instanceof PersonDetails) {
            authUser = (PersonDetails) authentication.getPrincipal();

            Optional<BookMark> foundBookMark = bookMarksService.findByBookAndPerson(book.get(), authUser.getPerson());

            if (foundBookMark.isPresent()) bookMark = foundBookMark.get();

            catalogs = bookCatalogsService.findByPerson(authUser.getPerson());
        }

        List<Chapter> chapters = chaptersService.findByBook(book.get());

        model.addAttribute("book", book.get());

        model.addAttribute("authUser", authUser);
        model.addAttribute("catalogs", catalogs);
        model.addAttribute("chapters", chapters);
        model.addAttribute("mark", bookMark);

        return "books/book";

    }


    @GetMapping("/book/{bookId}/ch/{numberPage}")
    public String showChapter(Authentication authentication,
                              @PathVariable int bookId,
                              @PathVariable int numberPage,
//                              @RequestParam(value = "par", defaultValue = "0") int paragraph,
                              Model model) {

        System.out.println("Book Id: " + bookId + ". Ch: " + numberPage);

        if (authentication == null) return "redirect:/auth/login";


        Optional<Book> book = booksService.findOne(bookId);

        if (book.isEmpty()) {
            model.addAttribute("message", "Такої книжки не існує...");

            return "errors/error";
        }


        if (numberPage < 1) {
            return "redirect:/book/" + bookId + "/ch/" + 1;
        } else if (numberPage > book.get().getAmount()) {
            return "redirect:/book/" + bookId + "/ch/" + book.get().getAmount();
        } else {
            Optional<Chapter> foundChapter = chaptersService.findByBookAndNumber(book.get(), numberPage);

            if (foundChapter.isEmpty()) {
                model.addAttribute("message", "Такої глави не існує...");

                return "errors/error";
            }

            BookMark mark = null;

            PersonDetails authUser = null;

            if (authentication.getPrincipal() instanceof PersonDetails) {
                authUser = (PersonDetails) authentication.getPrincipal();
                Optional<BookMark> foundMark = bookMarksService.findByBookAndPerson(book.get(), authUser.getPerson());

                if (foundMark.isPresent()) mark = foundMark.get();
            }


            int totalChapters = book.get().getAmount();

            model.addAttribute("book", book.get());

            Chapter chapter = foundChapter.get();

            chapter.setText(TextFormatter.parsePatterns(chapter.getText()).html());


            boolean isUser = false;

            int paragraph = 0;
            if (mark != null) {
                Chapter bookMarkChapter = mark.getChapter();
                isUser = true;
                if (bookMarkChapter != null) {
                    paragraph = !bookMarkChapter.equals(chapter) ? 0 : mark.getParagraph();
                }
            }
            List<Chapter> chapters = chaptersService.findChaptersByBook(book.get());


            model.addAttribute("isUser", isUser);

            model.addAttribute("chapter", chapter);
            model.addAttribute("chapters", chapters);

            model.addAttribute("mark", mark);

            model.addAttribute("paragraph", paragraph);
            model.addAttribute("counter", new Counter());
            model.addAttribute("currentChapter", numberPage);

            model.addAttribute("totalChapters", totalChapters);

            return "chapters/chapter";

        }
    }


    @GetMapping("/book/{id}/full-read")
    public String fullRead(Authentication authentication,
                           @PathVariable int id,
                           Model model) {

        if (authentication == null) return "redirect:/auth/login";

        Optional<Book> book = booksService.findOne(id);

        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("chapters", TextFormatter.parsePatternText(chaptersService.findByBook(book.get())));

            return "books/full-read";
        }

        model.addAttribute("message", "Такої книжки не існує...");

        return "errors/error";
    }
}