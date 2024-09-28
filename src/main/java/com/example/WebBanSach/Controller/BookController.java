package com.example.WebBanSach.Controller;

import  com.example.WebBanSach.entity.Book;
import com.example.WebBanSach.services.BookServices;
import com.example.WebBanSach.services.CategoryServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookServices bookService;
    private final String uploadDir = "uploads/";

    @Autowired
    private CategoryServices categoryService;

    @GetMapping
    public String showAllBooks(Model model) {
        List<Book> books = bookService.getALlBooks();
        model.addAttribute("books", books);
        return "book/list";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/add";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/add";
        }

        // Xử lý lưu ảnh
        String imageUrl = bookService.saveImage(imageFile);
        book.setImageUrl(imageUrl);

        bookService.addBook(book);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }
        return "redirect:/books";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("book") Book book,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }

        // Lấy sách từ database để lấy thông tin hình ảnh cũ
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return "redirect:/books";
        }

        // Nếu người dùng có tải lên hình ảnh mới
        if (!imageFile.isEmpty()) {
            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException("Không thể tạo thư mục upload: " + e.getMessage());
                }
            }

            // Lưu file vào thư mục
            try {
                String fileName = imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                book.setImageUrl("/" + uploadDir + fileName); // Cập nhật đường dẫn ảnh mới
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu file: " + e.getMessage());
            }
        } else {
            // Giữ nguyên hình ảnh cũ
            book.setImageUrl(existingBook.getImageUrl());
        }

        // Cập nhật các thông tin khác
        bookService.updateBook(book);

        return "redirect:/books";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
