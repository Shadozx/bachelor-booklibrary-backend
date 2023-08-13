package com.shadoww.BookLibraryApp.controllers;


import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.models.user.Role;
import com.shadoww.BookLibraryApp.services.BooksService;
import com.shadoww.BookLibraryApp.services.ChaptersService;
import com.shadoww.BookLibraryApp.services.PeopleService;
import com.shadoww.BookLibraryApp.util.texformatters.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).ADMIN.getRoleName())")
public class AdminController {

    private BooksService booksService;

    private ChaptersService chaptersService;

    private PeopleService peopleService;

    @Autowired
    public AdminController(BooksService booksService, ChaptersService chaptersService, PeopleService peopleService) {
        this.booksService = booksService;
        this.chaptersService = chaptersService;
        this.peopleService = peopleService;
    }

    @GetMapping("/")
    public String showAdminPanel(Authentication authentication,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "search-text", required = false) String searchText,
                                 Model model) {

        if (authentication == null) return "redirect:/auth/login";

        if (page < 1) return "redirect:/admin/";

        Page<Book> pageBooks;

        if (searchText != null && !searchText.equals("")) {
            pageBooks = booksService.findByTitle(searchText, page - 1);
            model.addAttribute("searchText", searchText);
        } else {
            pageBooks = booksService.findBooksByPage(page - 1);

            model.addAttribute("searchText", null);
        }


        boolean isSUPERADMIN = false;

        UserDetails admin = (UserDetails) authentication.getPrincipal();

        if (admin != null) {

//                    System.out.println("ROLES:" + admin.getAuthorities());

            List<GrantedAuthority> roles = new ArrayList<>(admin.getAuthorities());

            isSUPERADMIN = roles.contains(new SimpleGrantedAuthority(Role.SUPER_ADMIN.getRoleName()));
            System.out.println(isSUPERADMIN);
        }

        model.addAttribute("isSuperAdmin", isSUPERADMIN);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageBooks.getTotalPages());

        model.addAttribute("books", pageBooks.getContent());


        return "admin/admin";

    }


    /**
     * Метод add який парсить книгу за допомогою силки
     **/
    @GetMapping("/book/add")
    public String addBookGet() {

        return "books/add";
    }

    @GetMapping("/book/add/")
    public String redirectToAddBookGet() {

        return "redirect:/admin/add";
    }

    /**
     * Аналог до метода add тільки різниця в тому що тут треба добавляти все власноручно
     **/
    @GetMapping("/book/create")
    public String createBook(Model model) {

        model.addAttribute("newBook", new Book());

        return "admin/create-book"; //"books/create";
    }


    @GetMapping("/book/{bookId}/chapter/create")
    public String createChapter(@PathVariable int bookId,
                                Model model) {
        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) {
            model.addAttribute("message", "Такої книжки не існує...");

            return "errors/error";
        }

        System.out.println("FoundBook: " + foundBook.get());
        model.addAttribute("book", foundBook.get());



        return "admin/chapter-add";
    }

    // Редагувати книжку
    @GetMapping("/book/{bookId}/edit")
    public String editBook(@PathVariable int bookId, Model model) {
        Optional<Book> foundBook = booksService.findOne(bookId);

        System.out.println("Found book: " + foundBook);

        if (foundBook.isEmpty()) {
            model.addAttribute("message", "Такої книги не існує...");

            return "error";
        }

        model.addAttribute("book", foundBook.get());
        model.addAttribute("chapters", chaptersService.findByBook(foundBook.get()));


        return "admin/book-edit";
    }

    // Редагувати главу
    @GetMapping("/book/{bookId}/chapter/{numberPage}/edit")
    public String editChapter(@PathVariable int bookId,
                              @PathVariable int numberPage,
                              Model model) {

        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) {
            model.addAttribute("message", "Такої книжки не існує...");

            return "errors/error";
        }

        Optional<Chapter> foundChapter = chaptersService.findByBookAndNumber(foundBook.get(), numberPage);


        if (foundChapter.isEmpty()) {
            model.addAttribute("message", "Такої глави не існує...");

            return "error";
        }


        foundChapter.get().setText(TextFormatter.parsePatterns(foundChapter.get().getText()).html());

        model.addAttribute("book", foundBook.get());
        model.addAttribute("chapter", foundChapter.get());

        return "admin/chapter-edit";
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).SUPER_ADMIN.getRoleName())")
    public String editUsers(Authentication authentication,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "search-text", required = false) String searchText,
                            Model model) {

        if (authentication == null) return "redirect:/auth/login";


        Page<Person> result;


        if (searchText != null && !searchText.equals("")) {
            result = peopleService.findByUsername(searchText, page - 1);
            model.addAttribute("searchText", searchText);

        } else {
            result = peopleService.findALl(page - 1);

            model.addAttribute("searchText", null);
        }


        model.addAttribute("people", result.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());

        model.addAttribute("roles", Arrays.stream(Role.values()).filter(r -> !r.equals(Role.SUPER_ADMIN)).toList());
        return "admin/users";

    }

    @GetMapping("/user/{id}/edit")
    @PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).SUPER_ADMIN.getRoleName())")
    public String editUser(@PathVariable int id, Model model) {
        Optional<Person> foundUser = peopleService.findById(id);

        if (foundUser.isEmpty()) {
            model.addAttribute("message", "Такий користувач не існує...");

            return "errors/error";
        }

        model.addAttribute("user", foundUser.get());

        model.addAttribute("roles", Arrays.stream(Role.values()).filter(r -> !r.equals(Role.SUPER_ADMIN)).toList());

        return "admin/user";
    }
}
