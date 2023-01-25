package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "categories")
@Entity
public class Category extends BaseEntity {

    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

}
