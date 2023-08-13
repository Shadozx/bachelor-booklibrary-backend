package com.shadoww.BookLibraryApp.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shadoww.BookLibraryApp.forms.CatalogForm;
import com.shadoww.BookLibraryApp.models.user.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
//@Table(name = "catalogs")
@Setter
@Getter
@NoArgsConstructor
public class BookCatalog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @NotBlank(message = "Title cannot be empty")
    private String title;


    private boolean isPublic = true;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "person")
    private Person person;


    @JsonIgnore
    @OneToMany(mappedBy = "catalog")
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private List<BookMark> bookMarks;



    public BookCatalog(CatalogForm catalogForm) {
        this.setTitle(catalogForm.getTitle());
        this.setPublic(catalogForm.isPublic());
    }

    @Override
    public String toString() {
        return "BookCatalog{" +
                "title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", person=" + person +
                '}';
    }
}
