package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientVendorRepository extends JpaRepository<ClientVendor,Long> {

    List<ClientVendor> findAllByClientVendorTypeAndCompanyOrderByClientVendorName(ClientVendorType clientVendorType, Company company); // was needed for the Invoice -reyhan




}
