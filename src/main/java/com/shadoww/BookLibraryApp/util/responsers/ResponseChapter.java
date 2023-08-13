package com.shadoww.BookLibraryApp.util.responsers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseChapter {

    private ResponseChapter() {
    }

    public static ResponseEntity noFound() {
        return new ResponseEntity("Такої глави не існує", HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity noContent() {
        return new ResponseEntity("Форма для глави пуста", HttpStatus.BAD_REQUEST);
    }


    public static ResponseEntity noContent(String message) {
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }


    public static ResponseEntity exist() {
        return new ResponseEntity("Така глава вже існує", HttpStatus.CONFLICT);
    }

    public static ResponseEntity addSuccess() {
        return new ResponseEntity("Дана глава була успішно додана", HttpStatus.OK);
    }


    public static ResponseEntity deleteSuccess() {
        return new ResponseEntity("Дана глава була успішно видалена", HttpStatus.OK);
    }


    public static ResponseEntity updateSuccess() {
        return new ResponseEntity("Дана глава була успішно оновлена", HttpStatus.OK);
    }

    public static ResponseEntity errorServer() {
        return new ResponseEntity("Сталася помилка на сервері", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
