package com.finhero.finhero.controller;

import com.finhero.finhero.model.Category;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        log.info("Recebida requisição de listagem de categorias");
        
        Long userId = SecurityUtil.getCurrentUserId();
        List<Category> categories = categoryRepository.findByUserId(userId);
        
        log.info("Retornando {} categorias para usuário: {}", categories.size(), userId);
        return ResponseEntity.ok(categories);
    }
}

