package com.cydeo.controller;



import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    private final MapperUtil mapper;

    public CompanyController( CompanyService companyService, CompanyRepository companyRepository, MapperUtil mapper) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
    }

    @GetMapping("/list")
    public String navigateToCompanyList(Model model) {
    model.addAttribute("companies",companyService.listAllCompanies());

        return "/company/company-list";
    }


    @GetMapping("/create")
    public String companyCreate( Model model ){

            model.addAttribute("newCompany", new CompanyDto());

        return "company/company-create";
    }
    @PostMapping("/create")
    public String insertCompany(@Valid @ModelAttribute("newCompany") CompanyDto companyDto ,BindingResult bindingResult){

        if(bindingResult.hasErrors()){

            return "/company/company-create";

        }

        companyService.save(companyDto);

        return "redirect:/companies/list";
    }

    @GetMapping("/update/{id}")
    public String editCompany(@PathVariable("id") Long id, Model model ){
        //CompanyDto company =companyService.findById(id);

        model.addAttribute("company",companyService.findById(id));

        return "/company/company-update";
    }


    @PostMapping("/update/{id}")
    public String updateCompany(@Valid @ModelAttribute("company") CompanyDto companyDto,BindingResult bindingResult ){

        if(bindingResult.hasErrors()){
            return "/company/company-update";
        }

        companyService.update(companyDto);
        return "redirect:/companies/list";

    }

    @GetMapping("/deactivate/{id}")
    public String deactivateCompany(@PathVariable("id") Long id){

        Company company=companyRepository.findById(id).get();
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);

        return "redirect:/companies/list";

    }

    @GetMapping("/activate/{id}")
    public String activateCompany(@PathVariable("id") Long id){

        Company company=companyRepository.findById(id).get();
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(company);

        return "redirect:/companies/list";


    }


}
