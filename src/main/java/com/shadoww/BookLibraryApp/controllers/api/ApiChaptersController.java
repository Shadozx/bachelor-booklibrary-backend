package com.shadoww.BookLibraryApp.controllers.api;


import com.shadoww.BookLibraryApp.forms.ChapterForm;
import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.models.images.ChapterImage;
import com.shadoww.BookLibraryApp.services.BooksService;
import com.shadoww.BookLibraryApp.services.ChaptersService;
import com.shadoww.BookLibraryApp.services.ImagesService;
import com.shadoww.BookLibraryApp.util.formatters.Formatter;
import com.shadoww.BookLibraryApp.util.responsers.ResponseChapter;
import com.shadoww.BookLibraryApp.util.texformatters.TextFormatter;
import com.shadoww.BookLibraryApp.util.texformatters.elements.TextElement;
import com.shadoww.BookLibraryApp.util.texformatters.elements.TextElements;
import com.shadoww.BookLibraryApp.util.texformatters.types.ElementType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chapters/")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).ADMIN.getRoleName())")
public class ApiChaptersController {

    private BooksService booksService;
    private ChaptersService chaptersService;

    private ImagesService imagesService;

    private Formatter formatter;

    @Autowired
    public ApiChaptersController(BooksService booksService, ChaptersService chaptersService, ImagesService imagesService, Formatter formatter) {
        this.booksService = booksService;
        this.chaptersService = chaptersService;
        this.imagesService = imagesService;
        this.formatter = formatter;
    }


    @PostMapping("/book/{bookId}/add")
    public ResponseEntity addChapter(@PathVariable int bookId,
                                     @RequestBody ChapterForm form
    ) {
        System.out.println("Form: " + form);

        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) return new ResponseEntity("Дана книжка не існує", HttpStatus.NOT_FOUND);

        Book book = foundBook.get();

        if (form.isEmpty()) return ResponseChapter.noContent();

        Chapter newChapter = new Chapter();

        if (form.isTitleEmpty() || form.isTextEmpty() || form.isNumberOfPageEmpty())
            return ResponseChapter.noContent();


        TextElements newElements = TextFormatter.parse(form.getText());

        if (newElements.isEmpty()) return ResponseChapter.noContent();


        List<ChapterImage> images = new ArrayList<>();
        for (var element : newElements) {
            if (element.hasType(ElementType.Image) && element.hasAttribute("data-filename")) {

                ChapterImage image = new ChapterImage();
                image.setContentType("image/jpeg");
                String decodedImage = element.attr("data");

                if (decodedImage != null && !decodedImage.equals("")) {

                    element.deleteAttribute("data-filename");
                    element.deleteAttribute("data");

                    byte[] data = Base64.getDecoder().decode(decodedImage.getBytes());


                    image.setChapterImage(book);
                    image.setChapter(newChapter);
                    element.addAttribute("filename", image.getFilename());

                    image.setData(data);
                    images.add(image);
                }
            }
        }


        newChapter.setBook(foundBook.get());
        newChapter.setTitle(form.getTitle());

        newChapter.setText(newElements.toPatternText());
        
        newChapter.setNumberOfPage(form.getNumberOfPage());



        int amount = book.getAmount();
        book.setAmount( amount + 1);


        imagesService.saveChapterImages(images);

        System.out.println("Title:" + newChapter.getTitle());
        System.out.println("Text:" + newChapter.getText());
        System.out.println("Number:" + newChapter.getNumberOfPage());
        chaptersService.save(newChapter);
        booksService.save(book);


        return ResponseChapter.addSuccess();
    }

    @GetMapping("/book/{bookId}/reload")
    public ResponseEntity reloadChapters(@PathVariable int bookId) {
        Optional<Book> foundBook = booksService.findOne(bookId);

        if (foundBook.isEmpty()) return new ResponseEntity("Дана книжка не існує", HttpStatus.NOT_FOUND);

        foundBook.ifPresent(value -> formatter.parseChapters(value));

        return new ResponseEntity("Дана книжка була успішно перегружена", HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity getChapter(@PathVariable int id) {
        System.out.println("HERE!!!!!");

        Optional<Chapter> foundChapter = chaptersService.findById(id);

        if (foundChapter.isEmpty()) return ResponseChapter.noFound();

        Chapter chapter = foundChapter.get();

        chapter.setText(TextFormatter.parsePatterns(chapter.getText()).toPatternText());
        return ResponseEntity.ok(chapter);
    }

    @PutMapping("/{chId}")
    public ResponseEntity updateChapter(@PathVariable int chId,
                                        @RequestBody ChapterForm form) {


        Optional<Chapter> foundChapter = chaptersService.findById(chId);

        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        if (foundChapter.isEmpty()) {
            return ResponseChapter.noFound();
        }

        if (form.isEmpty()) return ResponseChapter.noContent();

        if(form.isTitleEmpty() || form.isTextEmpty() || form.isNumberOfPageEmpty()) return ResponseChapter.noContent();


        Book book = foundChapter.get().getBook();

        if (book == null) return new ResponseEntity("Дана книжка не існує", HttpStatus.NOT_FOUND);

        Chapter chapter = foundChapter.get();


        TextElements newElements = TextFormatter.parse(form.getText());

        if (newElements.isEmpty()) return ResponseChapter.noContent();


        chapter.setTitle(form.getTitle());
        chapter.setNumberOfPage(form.getNumberOfPage());

        TextElements oldElements = TextFormatter.parsePatternText(chapter.getText());

        if (newElements.equals(oldElements)) {

            System.out.println("Не було змін...");

            return new ResponseEntity("Нічого нового не було додано", HttpStatus.SEE_OTHER);
        }

        List<TextElement> newImages = newElements.stream().filter(e -> e.hasType(ElementType.Image)).toList();

        for (var element : oldElements) {
            if (element.hasType(ElementType.Image) && !newImages.contains(element)) {
                imagesService.deleteByFilename(element.attr("filename"));
            }
        }
//
        List<ChapterImage> images = new ArrayList<>();
        for (var element : newElements) {
            if (element.hasType(ElementType.Image) && element.hasAttribute("data-filename")) {

                ChapterImage image = new ChapterImage();
                image.setContentType("image/jpeg");
                String decodedImage = element.attr("data");

                if (decodedImage != null && !decodedImage.equals("")) {

                    element.deleteAttribute("data-filename");
                    element.deleteAttribute("data");

                    byte[] data = Base64.getDecoder().decode(decodedImage.getBytes());


                    image.setChapterImage(book);

                    element.addAttribute("filename", image.getFilename());

                    image.setData(data);
                    images.add(image);

                }

            }
        }
        imagesService.saveChapterImages(images);

        System.out.println("Result:");

        chapter.setText(newElements.toPatternText());

        chaptersService.save(chapter);


        return ResponseChapter.updateSuccess();
    }

    @DeleteMapping("/{chId}")
    public ResponseEntity deleteChapter(@PathVariable int chId) {

        Optional<Chapter> foundChapter = chaptersService.findById(chId);

        if (foundChapter.isEmpty()) {
            return ResponseChapter.noFound();
        }

        booksService.deleteChapter(foundChapter.get());

        System.out.println("Chapter with id " + chId + " was deleted!");

        return ResponseChapter.deleteSuccess();
    }

}
