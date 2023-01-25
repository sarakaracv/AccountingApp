package com.cydeo.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDto {

    private Long id;
    @NotBlank
    @Size(max = 100, min = 2)
    private String description;
    private CompanyDto company;
    private boolean hasProduct;
}
