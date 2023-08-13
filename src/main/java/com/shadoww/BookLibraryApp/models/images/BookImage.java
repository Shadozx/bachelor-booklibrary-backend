package com.shadoww.BookLibraryApp.models.images;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shadoww.BookLibraryApp.models.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Entity
//@Table(name = "book_images")
@DiscriminatorValue("Book")
@Setter
@Getter
@NoArgsConstructor
public class BookImage extends Image {


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;


    @JsonIgnore
    @OneToOne(optional = true)
    @JoinColumn(name = "book", nullable = true)
    private Book book;


//    private String contentType;
//
////    @Column(columnDefinition = "varchar")
//    private byte[] data;
//
//
//    private String filename;



    //    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "chapter", referencedColumnName = "id")
//    private Chapter chapter;

    public BookImage(Image image) {
        this.setContentType(image.getContentType());
        this.setData(image.getData());
    }

    public void setBook(Book book) {
        if(book!= null) {
            this.book = book;
            this.setFilename( book.getId() + ".jpeg");
        }
    }



}

