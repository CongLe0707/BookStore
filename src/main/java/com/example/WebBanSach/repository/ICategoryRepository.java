package com.example.WebBanSach.repository;


import com.example.WebBanSach.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
}
