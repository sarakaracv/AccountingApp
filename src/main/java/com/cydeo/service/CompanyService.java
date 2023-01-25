package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;


public interface CompanyService {

    CompanyDto findById(Long id);
    List<CompanyDto> listAllCompanies();

    CompanyDto getCompanyByLoggedInUser();

    void save(CompanyDto companyDto);

    void update(CompanyDto companyDto);







    List<CompanyDto> getAllByUsers();
}
