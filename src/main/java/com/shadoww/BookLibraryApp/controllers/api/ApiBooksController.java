package com.shadoww.BookLibraryApp.controllers.api;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.shadoww.BookLibraryApp.forms.BookForm;
import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.images.BookImage;
import com.shadoww.BookLibraryApp.services.BooksService;
import com.shadoww.BookLibraryApp.util.formatters.Formatter;
import com.shadoww.BookLibraryApp.util.parser.parsers.ParserHelper;
import com.shadoww.BookLibraryApp.util.responsers.ResponseBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).ADMIN.getRoleName())")
public class ApiBooksController {

    private final Formatter formatter;

    private final BooksService booksService;


    @Autowired
    public ApiBooksController(Formatter formatter, BooksService booksService) {
        this.formatter = formatter;
        this.booksService = booksService;
    }

    public static JsonMapper createMapper() {

        class ObjectArrayInNewLinePrettyPrinter extends DefaultPrettyPrinter {

            public ObjectArrayInNewLinePrettyPrinter() {
                super();
            }

            public ObjectArrayInNewLinePrettyPrinter(DefaultPrettyPrinter base) {
                super(base);
            }

            @Override
            public void writeStartObject(JsonGenerator g) throws IOException {
                _objectIndenter.writeIndentation(g, _nesting);
                super.writeStartObject(g);
            }

            @Override
            public void writeStartArray(JsonGenerator g) throws IOException {
                _arrayIndenter.writeIndentation(g, _nesting);
                super.writeStartArray(g);
            }

            @Override
            public DefaultPrettyPrinter createInstance() {
                return new ObjectArrayInNewLinePrettyPrinter(this);
            }
        }


        DefaultPrettyPrinter printer = new ObjectArrayInNewLinePrettyPrinter();
        printer.indentArraysWith(new DefaultIndenter());


        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .defaultPrettyPrinter(printer)
                .build();
    }


    /**
     * Get books
     */
    @GetMapping("/")
    public List<Book> show() throws JsonProcessingException {

//     JsonMapper mapper = createMapper();

//     return mapper.writeValueAsString(books);

        return booksService.findBooks();
    }

    /**
     * Add a book by a book's url
     */
    @PostMapping("/")
    public ResponseEntity addBook(@RequestParam("book-url") String bookUrl) {
        System.out.println("BookUrl: " + "\"" + bookUrl + "\"");

        if (bookUrl == null || Objects.equals(bookUrl, "")) {
            return ResponseBook.noContent();
        }

        if (booksService.exists(bookUrl)) return ResponseBook.exist();

        boolean isAdded = formatter.format(bookUrl);
        System.out.println("Book was added: " + isAdded);

        if (isAdded) {
            return ResponseBook.addSuccess();

        }

        System.out.println("Error!");
        return ResponseBook.errorServer();

    }

    @GetMapping("/{id}")
    public ResponseEntity getBook(@PathVariable int id) {
//        JsonMapper mapper = createMapper();

        return ResponseEntity.ok().body(booksService.findOne(id).orElse(null));
    }


    /**
     * Create a book by book's form
     */
    @PostMapping("/new")
    public ResponseEntity newBook(@RequestBody BookForm form) {

        if (form.isEmpty()) return ResponseBook.noContent();


        Book book = new Book();

        if (form.isTitleEmpty()) return ResponseBook.noContent("Назва книжки немає бути пустою");

        if (booksService.existByTitle(form.getTitle().trim())) return ResponseBook.exist();


        book.setTitle(form.getTitle());

        if (!form.isDescriptionEmpty()) {
            book.setDescription(form.getDescription());
        }

        if (!form.isBookImageUrlEmpty()) {
            try {
                BookImage bookImage = (BookImage) ParserHelper.parseImage(form.getBookImage());

                book.setBookImage(bookImage);
                bookImage.setBook(book);

                booksService.saveBookImage(book, bookImage);
            } catch (IOException e) {
                System.out.println("Error message in adding book with message:" + e.getMessage());
                return ResponseBook.errorServer();
            }
        } else {
            booksService.save(book);
        }

        return ResponseBook.addSuccess();

    }

    /**
     * Update a book by book's id
     */
    @PutMapping("/{bookId}")
    public ResponseEntity updateBook(@PathVariable int bookId,
                                     @RequestBody BookForm form) {

        System.out.println("HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) return ResponseBook.noFound();

        if (form.isEmpty()) return ResponseBook.noContent();

        Book book = foundBook.get();

        if (!form.isTitleEmpty()) {

            book.setTitle(form.getTitle());
        }

        if (!form.isDescriptionEmpty()) {
            book.setDescription(form.getDescription());
        }

        if (!form.isBookImageUrlEmpty()) {

            try {

                BookImage parsedImage = (BookImage) ParserHelper.parseImage(form.getBookImage());

                BookImage bookImage = book.getBookImage();

                if (bookImage == null) bookImage = new BookImage();

                bookImage.setData(parsedImage.getData());

                System.out.println("BookImage parsing...");
                System.out.println(bookImage);
                book.setBookImage(bookImage);
                bookImage.setBook(book);

                booksService.saveBookImage(book, bookImage);
            } catch (IOException e) {
                System.out.println("Error message in adding book with message:" + e.getMessage());
                return ResponseBook.errorServer();
            }
        } else {
            booksService.save(book);
        }

        return ResponseBook.addSuccess();
    }

    /**
     * Delete a book by book's id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBook(@PathVariable int id) {

        Optional<Book> foundBook = booksService.findOne(id);

        if (foundBook.isEmpty()) return ResponseBook.noFound();


        booksService.deleteBook(foundBook.get());

        System.out.println("Done!");

        return ResponseBook.deleteSuccess();
    }


}
