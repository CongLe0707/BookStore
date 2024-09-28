package com.example.WebBanSach.services;


import com.example.WebBanSach.entity.Book;
import com.example.WebBanSach.repository.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BookServices {
    @Autowired
    private IBookRepository bookRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";


    public List<Book> getALlBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }
    public String saveImage(MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                byte[] bytes = imageFile.getBytes();
                Path path = Paths.get(UPLOAD_DIR + imageFile.getOriginalFilename());
                Files.write(path, bytes);
                return "/uploads/" + imageFile.getOriginalFilename();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
