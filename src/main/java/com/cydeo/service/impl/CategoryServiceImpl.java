package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final SecurityService securityService;
    private final ProductService productService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil, UserRepository userRepository, CompanyRepository companyRepository, CompanyService companyService, SecurityService securityService, ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.securityService = securityService;
        this.productService = productService;
    }

//    @Override
//    public List<CategoryDto> getCategoryList(Company company) {
//        List<CategoryDto> categoryDtoList = categoryRepository.findAll().stream()
//                .map(category -> mapperUtil.convert(category, new CategoryDto())).sorted(
//                        Comparator.comparing(CategoryDto::getDescription)
//                ).collect(Collectors.toList());
//        return categoryDtoList;
//    }

    @Override
    public CategoryDto findById(Long id) {
        CategoryDto dto=mapperUtil.convert(categoryRepository.findById(id), new CategoryDto());
        dto.setHasProduct(productService.findAllProductByCategory(id).stream().anyMatch(productDto -> productDto.getQuantityInStock()>0));
        return dto;
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> category.getCompany().getTitle().equals(companyService.getCompanyByLoggedInUser().getTitle()))
                .filter(category -> category.getIsDeleted().equals(false))
                .sorted(Comparator.comparing(Category::getDescription))
                .map(category -> {
                    CategoryDto dto = mapperUtil.convert(category, new CategoryDto());
                    dto.setHasProduct(productService.findAllProductByCategory(category.getId()).stream().anyMatch(productDto -> productDto.getQuantityInStock()>0));
                    return dto;
                })
                .collect(Collectors.toList());

    }
//
//    @Override
//    public List<CategoryDto> listAllCategories() {
//        return categoryRepository.findAll().stream().filter(category -> !category.getIsDeleted()).map(category ->
//                mapperUtil.convert(category, new CategoryDto())).collect(Collectors.toList());
//    }


    @Override
    public void update(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId()).get();
        Category categoryEntity = mapperUtil.convert(categoryDto, new Category());
        categoryEntity.setId(category.getId());
        categoryEntity.setCompany(mapperUtil.convert(category.getCompany(),new Company()));
        categoryRepository.save(categoryEntity);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).get();
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void save(CategoryDto categoryDto) {
        CompanyDto companyDto = categoryDto.getCompany();
        Company company = mapperUtil.convert(securityService.getLoggedInUser().getCompany(),new Company());
        companyRepository.save(company);
        Category category = mapperUtil.convert(categoryDto, new Category());
        category.setCompany(company);
        category.setDescription(categoryDto.getDescription());
        categoryRepository.save(category);

    }

}
