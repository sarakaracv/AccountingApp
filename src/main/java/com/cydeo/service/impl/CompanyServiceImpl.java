package com.cydeo.service.impl;

import com.cydeo.dto.AddressDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Address;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.AddressRepository;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapper;
    private final AddressRepository addressRepository;


    private final SecurityService securityService;

    public CompanyServiceImpl(CompanyRepository companyRepository,AddressRepository addressRepository,MapperUtil mapper, SecurityService securityService) {
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        this.addressRepository=addressRepository;

        this.securityService = securityService;
    }
    @Override
    public List<CompanyDto> listAllCompanies(){
    return companyRepository.findAll()
            .stream()
            .map(company -> mapper.convert(company, new CompanyDto()))
            .collect(Collectors.toList());

    }

    @Override
    public CompanyDto getCompanyByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany();
    }

    @Override
    public void save(CompanyDto companyDto) {

        AddressDto addressDto=companyDto.getAddress();
        Address address=mapper.convert(addressDto,new Address());
        addressRepository.save(address);

        Company company=mapper.convert(companyDto, new Company());
        if(company.getCompanyStatus()==null) {
            company.setCompanyStatus(CompanyStatus.PASSIVE);
        }
        company.setAddress(address);

        companyRepository.save(company);
    }

    @Override
    public void update(CompanyDto companyDto) {
            Company company= companyRepository.findById(companyDto.getId()).get();

        Company convertedDto = mapper.convert(companyDto, new Company());
        convertedDto.setId(company.getId());

        convertedDto.setCompanyStatus(companyRepository.findById(companyDto.getId()).get().getCompanyStatus());

        convertedDto.setAddress(mapper.convert(company.getAddress(),new Address()));

        companyRepository.save(convertedDto);

    }




    @Override
    public CompanyDto findById(Long id) {

        return mapper.convert(companyRepository.findById(id), new CompanyDto() );
    }



    @Override
    public List<CompanyDto> getAllByUsers() {
        CompanyDto loggedInCompany = securityService.getLoggedInCompany();

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return companyRepository.findAll().stream()
                    .filter(company -> !company.getTitle().equals(loggedInCompany.getTitle()))
                    .map(company -> mapper.convert(company, new CompanyDto()))
                    .collect(Collectors.toList());
        } else {
            return companyRepository.findAll().stream()
                    .filter(company -> company.getTitle().equals(loggedInCompany.getTitle()))
                    .map(company -> mapper.convert(company, new CompanyDto()))
                    .collect(Collectors.toList());
        }
    }



//    @Override
//    public CompanyDto getCompanyByLoggedInUser() {
//        return securityService.getLoggedInUser().getCompany();
//    }
}
