package com.cydeo.service;

import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

   // List<CategoryDto> getCategoryList(Company company);

    CategoryDto findById(Long id);
    List<CategoryDto> findAll();
    void update(CategoryDto categoryDto);
    void delete(Long id);
    void save(CategoryDto categoryDto);

}
