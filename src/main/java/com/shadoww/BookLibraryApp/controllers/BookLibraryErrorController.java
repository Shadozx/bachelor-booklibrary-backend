package com.shadoww.BookLibraryApp.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BookLibraryErrorController implements ErrorController {


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {


            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "Вибач але такої сторінки немає");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("message", "На жаль на сервері сталася якась помилка");
            }

            // 400
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("message", "Вибач але це не зрозумілий запит");
            }else {
                model.addAttribute("message", "Щось пішло не так");
            }



        }else {
            model.addAttribute("message", "Щось пішло не так");
        }

        return "errors/error";
    }
}
