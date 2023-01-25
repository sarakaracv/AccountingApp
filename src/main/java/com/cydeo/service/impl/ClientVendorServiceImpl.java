package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {


    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapper;
    private final SecurityService securityService;

    private final InvoiceService invoiceService;


    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapper,
              SecurityService securityService,@Lazy InvoiceService invoiceService) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapper = mapper;
        this.securityService = securityService;
        this.invoiceService = invoiceService;
    }


    @Override
    public ClientVendorDto findById(Long id) {
        return mapper.convert(clientVendorRepository.findById(id), new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findAllVendors() {

        return clientVendorRepository
                .findAllByClientVendorTypeAndCompanyOrderByClientVendorName(ClientVendorType.VENDOR, mapper.convert(securityService.getLoggedInUser().getCompany(), new Company()))
                .stream()
                .map(clientVendor -> mapper.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> findAllClients() {
        return clientVendorRepository
                .findAllByClientVendorTypeAndCompanyOrderByClientVendorName(ClientVendorType.CLIENT, mapper.convert(securityService.getLoggedInUser().getCompany(), new Company()))
                .stream()
                .map(clientVendor -> mapper.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> listAllClientVendors() {
        return clientVendorRepository
                .findAll()
                .stream()
                .filter(clientVendor -> clientVendor.getCompany().getId().equals(mapper.convert(securityService.getLoggedInUser().getCompany(),new Company()).getId()))
                .sorted(Comparator.comparing(ClientVendor::getClientVendorType).reversed())
                .map(clientVendor -> mapper.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(ClientVendorDto clientVendorDto) {

        ClientVendor clientVendor=mapper.convert(clientVendorDto,new ClientVendor());
        clientVendor.setCompany(mapper.convert(securityService.getLoggedInUser().getCompany(),new Company()));
        clientVendorRepository.save(clientVendor);

    }

    @Override
    public void update(ClientVendorDto clientVendorDto) {

        ClientVendor clientVendor=mapper.convert(clientVendorDto,new ClientVendor());
       clientVendor.setCompany(mapper.convert(securityService.getLoggedInUser().getCompany(),new Company()));
        clientVendorRepository.save(clientVendor);

    }

    @Override
    public void delete(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id).orElseThrow();
        clientVendor.setIsDeleted(true);
        clientVendorRepository.save(clientVendor);

        //added to clientVendor class entity the @where clause
    }

    public boolean clientVendorWithInvoice(Long id) {

        if (!(invoiceService.findAllInvoiceByClientVendorId(id).isEmpty() || invoiceService.findAllInvoiceByClientVendorId(id) == null
        )) {
            return true;
        } else {
            return false;
        }

    }
    }