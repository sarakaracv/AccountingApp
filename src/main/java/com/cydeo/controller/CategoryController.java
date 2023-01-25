package com.cydeo.controller;
import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/list")
    public String navigateToCategoryList(Model model) throws Exception {
        model.addAttribute("categories", categoryService.findAll());
        return "/category/category-list";
    }

    @GetMapping("update/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model){
        model.addAttribute("category", categoryService.findById(id));
        return "category/category-update";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@Valid @ModelAttribute("category") CategoryDto category, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "category/category-update";
        }
        categoryService.update(category);
        return "redirect:/categories/list";
    }

    @GetMapping("/create")
    public String createCategory(Model model){
        model.addAttribute("newCategory", new CategoryDto());
        return "category/category-create";
    }
    @PostMapping("/create")
    public String insertCategory(@Valid @ModelAttribute("newCategory") CategoryDto categoryDto,BindingResult bindingResult ,Model model){
        if (bindingResult.hasErrors()) {
            return "category/category-create";
        }

        categoryService.save(categoryDto);
        return "redirect:/categories/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id){
        categoryService.delete(id);
        return "redirect:/categories/list";
    }
}
