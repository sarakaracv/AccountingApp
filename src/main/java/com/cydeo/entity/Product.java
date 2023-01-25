package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Entity
@Where(clause = "is_deleted=false")
public class Product extends BaseEntity {
    private String name;
    private int quantityInStock;
    private int lowLimitAlert;
    @Enumerated(value = EnumType.STRING)
    private ProductUnit productUnit;
    @ManyToOne
    private Category category;

}
