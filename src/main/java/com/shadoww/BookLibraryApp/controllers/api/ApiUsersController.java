package com.shadoww.BookLibraryApp.controllers.api;


import com.shadoww.BookLibraryApp.forms.PersonForm;
import com.shadoww.BookLibraryApp.models.images.PersonImage;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.security.PersonDetails;
import com.shadoww.BookLibraryApp.services.*;
import com.shadoww.BookLibraryApp.util.responsers.ResponseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class ApiUsersController {


    private final BooksService booksService;
    private final BookCatalogsService bookCatalogsService;

    private final ChaptersService chaptersService;

    private final BookMarksService bookMarksService;

    private PeopleService peopleService;

    private ImagesService imagesService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public ApiUsersController(BooksService booksService, BookCatalogsService bookCatalogsService, ChaptersService chaptersService, BookMarksService bookMarksService, PeopleService peopleService, ImagesService imagesService, PasswordEncoder passwordEncoder) {
        this.booksService = booksService;
        this.bookCatalogsService = bookCatalogsService;
        this.chaptersService = chaptersService;
        this.bookMarksService = bookMarksService;
        this.peopleService = peopleService;
        this.imagesService = imagesService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/")
    @PreAuthorize("true")
    public ResponseEntity createUser(@RequestBody PersonForm form) {

        System.out.println("Create new user");
        System.out.println(form);
        if (form.isEmpty()) return ResponseUser.noContent("Форма для реєстрації пуста");

        if (form.isUsernameEmpty()) return ResponseUser.noContent("Імя не може бути пустим");
        if (form.isPasswordEmpty()) return ResponseUser.noContent("Пароль не може бути пустим");

        if (peopleService.existByUsername(form.getUsername())) return ResponseUser.exist();

        Person person = new Person();

        person.setUsername(form.getUsername());
        person.setPassword(form.getPassword());

        peopleService.savePerson(person);

        return ResponseUser.addSuccess();
    }


    @PutMapping("/")
    public ResponseEntity updateUser(Authentication authentication, @RequestBody PersonForm personForm) {
        System.out.println(personForm);

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


        if (authentication.getPrincipal() instanceof PersonDetails authUser) {

            Person person = authUser.getPerson();

            person.setUsername(personForm.getUsername());
            person.setPassword(personForm.getPassword());


            System.out.println("Updated: " + person);
            peopleService.update(person);

            return ResponseUser.updateSuccess();

        }

        return ResponseUser.errorServer();

    }

    @PostMapping("/image")
    public ResponseEntity addImage(Authentication authentication,
                                   @RequestParam("data") MultipartFile file) throws IOException {

        System.out.println("HERE");

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (file == null) return new ResponseEntity("Дана фотографія користувача пуста", HttpStatus.NO_CONTENT);


        PersonDetails authUser = (PersonDetails) authentication.getPrincipal();


        Person person = authUser.getPerson();

        PersonImage personImage = person.getPersonImage();


        if (personImage == null) {

            personImage = new PersonImage();

            personImage.setFilename("user_" + person.getId());

            person.setPersonImage(personImage);
            personImage.setPerson(person);

        }

        personImage.setData(file.getBytes());

        imagesService.save(personImage);

        peopleService.save(person);
        /*if (personImage != null) {

            personImage.setData(file.getBytes());
            System.out.println("User image:");
            System.out.println(file.getBytes().length);

            System.out.println("User image was updated");

            imagesService.save(personImage);


        }

        else {
            personImage = new PersonImage();

            personImage.setFilename("user_" + person.getId());



//                    personImage.setContentType("image/jpeg");
            person.setPersonImage(personImage);
            personImage.setPerson(person);

            System.out.println("User image was added");

            System.out.println(personImage);

            imagesService.save(personImage);

            peopleService.save(person);


        }

        */

        System.out.println("Sorry...");
        return new ResponseEntity("Фотографія була додана", HttpStatus.OK);

    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).SUPER_ADMIN.getRoleName())")
    public ResponseEntity editUserById(@PathVariable int id, @RequestBody PersonForm form) {

        System.out.println("Form: " + form);

        Optional<Person> foundPerson = peopleService.findById(id);


        if (foundPerson.isEmpty()) return ResponseUser.noFound();

        Person person = foundPerson.get();

        if (form.isEmpty()) return ResponseUser.noContent();


        if (!form.isUsernameEmpty()) person.setUsername(form.getUsername());

        if (!form.isPasswordEmpty()) person.setPassword(passwordEncoder.encode(form.getPassword()));

        if (!form.isRoleEmpty()) person.setRole(form.getRole());

        System.out.println("Updated person: " + person);
        peopleService.save(person);

        return ResponseUser.updateSuccess();


    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).SUPER_ADMIN.getRoleName())")
    public ResponseEntity deletePerson(@PathVariable int id) {

        Optional<Person> foundPerson = peopleService.findById(id);

        if (foundPerson.isEmpty()) return ResponseUser.noFound();

        peopleService.delete(foundPerson.get());

        return ResponseUser.deleteSuccess();
    }


}
