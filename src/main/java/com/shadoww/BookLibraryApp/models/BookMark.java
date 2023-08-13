package com.shadoww.BookLibraryApp.models;

import com.shadoww.BookLibraryApp.models.user.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;


@Entity
//@Table(name = "bookmarks")
@Setter
@Getter
@NoArgsConstructor
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    /**
     * Закладка в книжці
     *
     **/
    @ManyToOne
    @JoinColumn(name = "catalog")
//    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private BookCatalog catalog;


    @ManyToOne
    @JoinColumn(name = "book")
    private Book book;

//    private Chapter chapter;

    @ManyToOne
    @JoinColumn(name = "chapter")
    private Chapter chapter;


    @Min(value = 0)
    private int paragraph;


    @ManyToOne
    @JoinColumn(name = "person")
//    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Person person;


    @Transient
    private String url;



    public String getUrl() {
        return "/book/"+ book.getId() + "/ch/" + chapter.getNumberOfPage();
//        return "/book/"+ book.getId() + "/ch/" + chapter.getNumberOfPage() + (paragraph != 0 ? "?par=" + paragraph : "");
    }


    public boolean isValid() {
        return this.book != null &&
               this.catalog != null &&
               this.chapter != null &&
               this.person != null &&
               this.paragraph >= 0;
    }
    @Override
    public String toString() {
        return "BookMark{" +
                "book=" + book +
                ", chapter=" + chapter +
                ", paragraph=" + paragraph +
                '}';
    }
}

