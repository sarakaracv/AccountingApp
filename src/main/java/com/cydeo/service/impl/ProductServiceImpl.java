package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final SecurityService securityService;
    private final InvoiceProductService invoiceProductService;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil, CompanyService companyService, SecurityService securityService, InvoiceProductService invoiceProductService) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.securityService = securityService;
        this.invoiceProductService = invoiceProductService;
    }
    @Override
    public ProductDto create(ProductDto productDto) {
        Product product = productRepository.save(mapperUtil.convert(productDto, new Product()));
        return mapperUtil.convert(product, new ProductDto());
    }

       @Override
    public ProductDto update(ProductDto productDto) {
           Optional<Product> product = productRepository.findById(productDto.getId());
           ProductDto convert = mapperUtil.convert(product, new ProductDto());
           convert.setQuantityInStock(product.get().getQuantityInStock());
           convert.setId(product.get().getId());
           convert.setCategory(productDto.getCategory());
           convert.setLowLimitAlert(productDto.getLowLimitAlert());
           convert.setProductUnit(productDto.getProductUnit());
           convert.setName(productDto.getName());
           productRepository.save(mapperUtil.convert(convert, new Product()));
           return convert;
    }

    @Override
    public void delete(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if(invoiceProductService.findAllInvoiceProductsByProductId(id).size() <1){
            product.get().setIsDeleted(true);
            product.get().setName(product.get().getName()+" - "+product.get().getId());
            productRepository.save(product.get());
        }


    }

    @Override
    public ProductDto findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return mapperUtil.convert(product, new ProductDto());
    }

    @Override
    public List<ProductDto> findAll() { // all products that belong to current company
        return productRepository.findAll().stream()
                .filter(product -> product.getCategory().getCompany().getId() == securityService.getLoggedInUser().getCompany().getId())
                .map(product -> mapperUtil.convert(product, new ProductDto())).collect(Collectors.toList());

    }

    @Override
    public void updateQuantityInStock(InvoiceProductDto invoiceProduct) {

        ProductDto product = invoiceProduct.getProduct();

        if (invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.SALES)) {

            product.setQuantityInStock(invoiceProduct.getProduct().getQuantityInStock() - invoiceProduct.getQuantity());

        } else {
            product.setQuantityInStock(invoiceProduct.getProduct().getQuantityInStock() + invoiceProduct.getQuantity());
        }

        productRepository.save(mapperUtil.convert(product, new Product()));
    }

    @Override
    public List<ProductDto> findAllProductByCategory(Long id) {

        return productRepository.findAllByCategoryIdOrderByNameAsc(id).stream()
                .map(product -> mapperUtil.convert(product, new ProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        productDto.getCategory().setCompany(companyService.getCompanyByLoggedInUser());
        Product product = mapperUtil.convert(productDto, new Product());
        Product product1 = productRepository.save(product);
        return mapperUtil.convert(product1, new ProductDto());
    }

    @Override
    public boolean isProductNameExist(ProductDto productDto) {
        return productRepository.existsByName(productDto.getName());
    }

}
