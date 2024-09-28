package com.example.WebBanSach.repository;

import com.example.WebBanSach.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<Book, Long> {
}
