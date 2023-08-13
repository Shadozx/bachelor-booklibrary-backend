package com.shadoww.BookLibraryApp.controllers;


import com.shadoww.BookLibraryApp.models.BookCatalog;
import com.shadoww.BookLibraryApp.models.images.PersonImage;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.security.PersonDetails;
import com.shadoww.BookLibraryApp.services.PeopleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class UserController {

    private PeopleService peopleService;

    public UserController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable int id,
                           Model model,
                           Authentication authentication) {

        Optional<Person> foundPerson = peopleService.findById(id);

        if (foundPerson.isPresent()) {

            if (authentication != null) {
                PersonDetails authUser = (PersonDetails) authentication.getPrincipal();
                model.addAttribute("authUser", authUser);

                if (authUser.getPerson().equals(foundPerson.get())) {
                    List<BookCatalog> catalogs = foundPerson.get().getCatalogs();


                    System.out.println("Person image:" + foundPerson.get().getPersonImage());
                    model.addAttribute("user", foundPerson.get());
                    model.addAttribute("catalogs", catalogs);


                    model.addAttribute("isEditable", true);


                    model.addAttribute("newImage", new PersonImage());
                    model.addAttribute("newCatalog", new BookCatalog());

                    return "user/user";
                }

            }


            List<BookCatalog> catalogs = foundPerson.get().getCatalogs().stream().filter(BookCatalog::isPublic).toList();

            model.addAttribute("user", foundPerson.get());
            model.addAttribute("catalogs", catalogs);
            model.addAttribute("isEditable", false);


            return "user/user";

            /*if (authentication != null) {
                PersonDetails authUser = (PersonDetails) authentication.getPrincipal();

                model.addAttribute("authUser" ,authUser);

                List<BookCatalog> catalogList;
                if (foundPerson.get().getId() == authUser.getPerson().getId()) {

                    catalogList= foundPerson.get().getCatalogs();
                }else {
                    catalogList= foundPerson.get().getCatalogs().stream().filter(BookCatalog::isPublic).toList();
                }

                model.addAttribute("counter", new Counter());
                model.addAttribute("catalogs", catalogList);
                model.addAttribute("newCatalog", new BookCatalog());
            }else {
                model.addAttribute("authUser", null);
            }


             */
        }else {
            model.addAttribute("message", "Такого користувача не існує");

            return "error";
        }



    }
}
