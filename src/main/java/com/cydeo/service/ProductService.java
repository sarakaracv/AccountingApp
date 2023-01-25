package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;

import java.util.List;

public interface ProductService {
    public ProductDto create(ProductDto productDto);
    ProductDto update(ProductDto productDto);
    void delete(Long id);
    ProductDto findById (Long id);

    List<ProductDto> findAll(); // was needed for the Invoice -reyhan

    void updateQuantityInStock(InvoiceProductDto invoiceProduct); // was needed for the Invoice -reyhan
     List<ProductDto> findAllProductByCategory(Long id);

    ProductDto save(ProductDto productDTO);

    boolean isProductNameExist(ProductDto productDto);
}
