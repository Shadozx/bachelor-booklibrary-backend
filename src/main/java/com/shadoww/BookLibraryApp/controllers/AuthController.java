package com.shadoww.BookLibraryApp.controllers;


import com.shadoww.BookLibraryApp.forms.PersonForm;
import com.shadoww.BookLibraryApp.models.user.Person;
import com.shadoww.BookLibraryApp.services.PeopleDetailsService;
import com.shadoww.BookLibraryApp.services.PeopleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {


    @GetMapping("/login")
    public String loginPage() {

        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration() {

        return "auth/registration";
    }


//    @PostMapping("/login")
//    public String login() {
////        System.out.println(user);
//        return "redirect:/admin/";
//    }

}
