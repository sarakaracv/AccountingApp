package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceProductDto {

    private Long id;

    @NotNull
    @Positive
    @Max(100)
    private Integer quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Max(20)
    @Min(5)
    private Integer tax;

    private BigDecimal total;
    private BigDecimal profitLoss;
    private Integer remainingQuantity;
    private InvoiceDto invoice;

    @NotNull
    private ProductDto product;


}
