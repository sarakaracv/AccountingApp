package com.cydeo.repository;

import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findCategoriesByCompany(Company company);

    List<Category> findAll();
}
