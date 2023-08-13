package com.shadoww.BookLibraryApp.services;


import com.shadoww.BookLibraryApp.models.Book;
import com.shadoww.BookLibraryApp.models.Chapter;
import com.shadoww.BookLibraryApp.repositories.ChaptersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChaptersService {
    private ChaptersRepository chaptersRepository;



    @Autowired
    public ChaptersService(ChaptersRepository chaptersRepository) {
        this.chaptersRepository = chaptersRepository;
    }



    public List<Chapter> findChapters() {
        return chaptersRepository.findAll();
    }


    public Optional<Chapter> findByBookAndNumber(Book book, int number) {
        return chaptersRepository.findChaptersByBookAndNumberOfPage(book, number);
    }

    public Optional<Chapter> findFirstChapterByBook(Book book) {
        return chaptersRepository.findChapterByBookAndNumberOfPage(book, 1);
    }

    public List<Chapter> findChaptersByBook(Book book) {
        List<Chapter> chapters  = chaptersRepository.findAllByBookId(book, Sort.by(Sort.Direction.ASC, "numberOfPage"));
         Collections.reverse(chapters);
         return chapters;
    }

    public List<Chapter> findByBook(Book book) {

        return chaptersRepository.findChaptersByBook(book, Sort.by(Sort.Direction.ASC, "numberOfPage"));
    }
    public Optional<Chapter> findById(int id) {
        return chaptersRepository.findById(id);
    }

    @Transactional
    public void save(Chapter c) {
        chaptersRepository.save(c);
    }

    @Transactional
    public void update(int chId, Chapter updated) {
        Optional<Chapter> forUpdate = findById(chId);

        //            forUpdate.get().setTitle(updated.getTitle());
        //            forUpdate.get().setText(updated.getText());
        //            forUpdate.get().setNumberOfPage(updated.getNumberOfPage());
        //            save(forUpdate.get());
        forUpdate.ifPresent(chapter -> update(updated, chapter));
    }



    @Transactional
    public void update(Chapter updated, Chapter toUpdated) {
        toUpdated.setTitle(updated.getTitle());
        toUpdated.setText(updated.getText());
        toUpdated.setNumberOfPage(updated.getNumberOfPage());
        save(toUpdated);
    }

    @Transactional
    public void deleteOne(Chapter forDelete) {
        chaptersRepository.delete(forDelete);
    }
    @Transactional
    public void deleteByBook(int id) {
        chaptersRepository.deleteChaptersByBook_Id(id);
    }
}
