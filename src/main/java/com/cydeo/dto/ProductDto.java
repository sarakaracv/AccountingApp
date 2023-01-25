package com.cydeo.dto;


import com.cydeo.enums.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;
    @NotNull(message = "Product Name is required field.")
    @Size(max = 100, min = 2)
    @Column(unique = true)
    private String name;


    private Integer quantityInStock;

    @NotNull(message = "Low Limit Alert is a required field.")
    @Min(1)
    private Integer lowLimitAlert;

    @NotNull(message = "Product Unit is a required field.")
    @Column(updatable = true)
    private ProductUnit productUnit;


    @NotNull(message = "Category is a required field.")
    @Column(updatable = true)
    private CategoryDto category;
}
