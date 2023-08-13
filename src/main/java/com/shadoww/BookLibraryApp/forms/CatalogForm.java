package com.shadoww.BookLibraryApp.forms;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CatalogForm {

    private String title;

    private boolean isPublic = true;

    @Override
    public String toString() {
        return "CatalogForm{" +
                "title='" + title + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }


    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }



    public boolean isTitleEmpty() {
        return title == null || title.equals("");
    }
}
