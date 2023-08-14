package com.shadoww.BookLibraryApp;

import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.util.parser.factories.ParserFactory;
import com.shadoww.BookLibraryApp.util.parser.parsers.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FormatTextTest {


//    @Test

    public void testLibrebook() {
        Parser parser = ParserFactory.createLibreBookParser();

        Book book = parser.parseBook("https://librebook.me/jizn_kastruchcho_kastrakani_iz_lukki");

        System.out.println(book);

        List<Chapter> chapters = parser.parseChapters("https://librebook.me/jizn_kastruchcho_kastrakani_iz_lukki");

        displayChapters(chapters);
    }


    private void displayChapters(List<Chapter> chapters) {

        for (var c : chapters) displayChapter(c);
    }

    private void displayChapter(Chapter chapter) {
        System.out.println("Title:" + chapter.getTitle());

        System.out.println("-".repeat(20));
        System.out.println(chapter.getText());
        System.out.println("=".repeat(20));
    }

}
