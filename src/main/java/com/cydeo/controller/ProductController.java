package com.cydeo.controller;


import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CompanyService companyService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, ProductRepository productRepository, CompanyService companyService, CategoryService categoryService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.companyService = companyService;
        this.categoryService = categoryService;
    }


    @GetMapping("/list")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/product-list";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return "redirect:/products/list";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
        return "product/product-create";
    }
    @PostMapping("/create")
    public String createPostProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto, BindingResult bindingResult, Model model) {
        if (productService.isProductNameExist(productDto)){
            bindingResult.rejectValue("name", " ", "product name already exist");
        }
        if (productDto.getLowLimitAlert()<1){
            bindingResult.rejectValue("lowLimitAlert", " ", "Low limit alert must be greater than 1");
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("products", productService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            return "product/product-create";
        }

        productService.save(productDto);

        return "redirect:/products/list";
    }

    @GetMapping("/update/{id}")
    public String getUpdateProduct(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
        model.addAttribute("categories", categoryService.findAll());
        return "product/product-update";
    }
    @PostMapping("/update/{id}")
    public String updateProduct(@Valid @PathVariable("id") Long id, @ModelAttribute("product") ProductDto productDto, BindingResult bindingResult, Model model) {

        if (productDto.getLowLimitAlert()<1){
            bindingResult.rejectValue("lowLimitAlert", " ", "Low Limit Alert should be at least 1");
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            model.addAttribute("categories", categoryService.findAll());
            return "product/product-update";
        }
        productService.update(productDto);
        return "redirect:/products/list";
    }

}
