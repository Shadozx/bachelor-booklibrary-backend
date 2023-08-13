package com.shadoww.BookLibraryApp.controllers.api;


import com.shadoww.BookLibraryApp.models.images.Image;
import com.shadoww.BookLibraryApp.services.ImagesService;
import com.shadoww.BookLibraryApp.util.responsers.ResponseImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/media")

@PreAuthorize("hasRole(T(com.shadoww.BookLibraryApp.models.user.Role).USER.getRoleName())")
public class ApiImagesController {

    private ImagesService imagesService;

    @Autowired
    public ApiImagesController(ImagesService imagesService) {
        this.imagesService = imagesService;
    }

    //    @CrossOrigin
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getBookImageData(@PathVariable String filename) {
        Optional<Image> foundImage = imagesService.findImageByFilename(filename);

        return foundImage.map(image -> ResponseEntity.ok().contentType(MediaType.valueOf(image.getContentType())).body(image.getData())).orElseGet(ResponseImage::noFound);

        //
//        return foundImage.map(image -> ResponseEntity.ok().contentType(MediaType.valueOf(image.getContentType())).body(image.getData()))
//                         .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

//        return foundImage.map(image -> ResponseEntity.ok().contentType(MediaType.valueOf(image.getContentType())).body(image.getData())).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

