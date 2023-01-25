package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;

import java.util.List;

public interface ClientVendorService {

    ClientVendorDto findById(Long id);

    List<ClientVendorDto> findAllVendors(); // was needed for the Invoice -reyhan
    List<ClientVendorDto> findAllClients(); // was needed for the Invoice -reyhan

    List<ClientVendorDto> listAllClientVendors();

    void save(ClientVendorDto clientVendorDto);

    void update(ClientVendorDto clientVendorDto);

    void delete(Long id);

    boolean clientVendorWithInvoice(Long id);



}
