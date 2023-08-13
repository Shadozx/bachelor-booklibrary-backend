package com.shadoww.BookLibraryApp.controllers.api;


import com.shadoww.BookLibraryApp.forms.CatalogForm;
import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.security.PersonDetails;
import com.shadoww.BookLibraryApp.services.BookCatalogsService;
import com.shadoww.BookLibraryApp.util.responsers.ResponseCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/catalogs")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class ApiCatalogsController {

    private BookCatalogsService bookCatalogsService;

    @Autowired
    public ApiCatalogsController(BookCatalogsService bookCatalogsService) {
        this.bookCatalogsService = bookCatalogsService;
    }

    @PostMapping("/")
    public ResponseEntity addCatalog(Authentication authentication,
                                     @RequestBody CatalogForm catalogForm) {

        System.out.println(catalogForm);

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        if (catalogForm.isTitleEmpty()) return ResponseCatalog.noContent();


        if(authentication.getPrincipal() instanceof PersonDetails authUser) {

            BookCatalog catalog = new BookCatalog(catalogForm);

            catalog.setPerson(authUser.getPerson());

            System.out.println(catalog);
            bookCatalogsService.save(catalog);

            return ResponseCatalog.addSuccess();
        }

        return ResponseCatalog.errorServer();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCatalog> getCatalog(@PathVariable int id) {

        Optional<BookCatalog> foundCatalog = bookCatalogsService.findById(id);

        if (foundCatalog.isEmpty()) return ResponseCatalog.noFound();

        return ResponseEntity.ok(foundCatalog.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCatalog(Authentication authentication,
                                        @PathVariable int id,
                                        @RequestBody CatalogForm catalogForm) {

        System.out.println("METHOD UPDATE CATALOG");
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (catalogForm.isTitleEmpty()) return ResponseCatalog.noContent();

        if(authentication.getPrincipal() instanceof PersonDetails authUser) {

            Optional<BookCatalog> foundCatalog = bookCatalogsService.findByIdAndPerson(id, authUser.getPerson());

            if (foundCatalog.isEmpty()) return ResponseCatalog.noFound();

            BookCatalog catalog = foundCatalog.get();

            catalog.setTitle(catalogForm.getTitle());
            catalog.setPublic(catalog.isPublic());

            catalog.setPerson(authUser.getPerson());

            System.out.println(catalog);
            bookCatalogsService.save(catalog);

            return ResponseCatalog.updateSuccess();
        }

        return ResponseCatalog.errorServer();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCatalog(@PathVariable int id) {

        Optional<BookCatalog> foundCatalog = bookCatalogsService.findById(id);

        if (foundCatalog.isEmpty()) return ResponseCatalog.noFound();

        bookCatalogsService.delete(foundCatalog.get());

        return ResponseCatalog.deleteSuccess();
    }


}
