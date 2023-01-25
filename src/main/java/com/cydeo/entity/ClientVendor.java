package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="clients_vendors")
@Where(clause = "is_deleted=false")
//added to clientVendor class entity the @where clause
public class ClientVendor extends BaseEntity {

    @Column(nullable = false,updatable = true)

    private String clientVendorName;
    private String phone;
    private String website;

    @Enumerated(EnumType.STRING)
    private ClientVendorType clientVendorType;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;


    @ManyToOne(cascade = CascadeType.MERGE)
    private Company company;

}
