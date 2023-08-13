package com.shadoww.BookLibraryApp.models.images;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
//@Table(name = "images")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "image_type")
@Getter
@Setter
@NoArgsConstructor
public class Image implements Serializable {


//    @Type(type = "org.hibernate.type.BinaryType")

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;


    private String contentType = "image/jpeg";

//    @Column(columnDefinition = "varchar")
    private byte[] data;


    @Column(unique = true)
    @NotBlank(message = "Filename cannot be empty")
    private String filename;



//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "chapter", referencedColumnName = "id")
//    private Chapter chapter;
    @Transient
    private String url;

    /**
     * contentType: String,
     *   data: { type: Buffer, required: true },
     *   book: { type: Schema.Types.ObjectId, ref: 'Book' },
     *   filename: { type: String, required: true },
     *   url: { type: String, required: true },
     *
     * */

    public Image(String filename) {
        this.filename = filename;
    }



//    public void setChapter(Book book, int i) {
//        if (book != null) {
//            this.book = book;
//            this.filename = book.getId() + "_" + i + ".jpeg";
//        }
//    }

//    public void setChapter(Book book, List<Image> images) {
//    public void setChapterImage(Book book) {
//        if (book != null) {
//            this.book = book;
////            this.filename = book.getId() + "_" + (images.size() + 1) + ".jpeg";
//            this.filename = book.getId() + "_" + UUID.randomUUID() + ".jpeg";
//        }
//    }

    private static String getStandardImageUrl() {
        return "/api/media/";
    }


    public static String getFileNameFromImg(String src) {
        return src.startsWith(getStandardImageUrl()) ? src.replace(getStandardImageUrl(), "") : null;
    }

    public String getUrl() {
        return getStandardImageUrl() + filename;
    }

//    @Override
//    public String toString() {
//        return "Image{" +
//                "id=" + id +
//                ", contentType='" + contentType + '\'' +
//                ", filename='" + filename + '\'' +
//                '}';
//    }
}
