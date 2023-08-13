package com.shadoww.BookLibraryApp.models.user;

import lombok.Getter;

public enum Privilege {



//    PRIVILEGE_READ_BOOK("/book/**"),
//    PRIVILEGE_USER("/user/**"),
//    PRIVILEGE_API_IMG("/api/imgs/**"),

    PRIVILEGE_API_CATALOG("/api/catalogs/**"),
    PRIVILEGE_ADD_BOOKMARK("/api/books/bookmark"),
    PRIVILEGE_API_BOOK("/api/books/**"),

    PRIVILEGE_API_USER("/api/users/**"),
    PRIVILEGE_ADMIN("/admin/**"),

    PRIVILEGE_SUPER_ADMIN("/edit-user/");


    private String url;


    Privilege(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }



    @Override
    public String toString() {
        return "Privilege{" +
                name() +
                ":" + url +
                '}';
    }
}
