package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapper;

    private final InvoiceService invoiceService;

    private final SecurityService securityService;


    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapper, @Lazy InvoiceService invoiceService, SecurityService securityService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapper = mapper;
        this.invoiceService = invoiceService;
        this.securityService = securityService;

    }

    @Override
    public InvoiceProductDto findById(Long id) {
        return mapper.convert(invoiceProductRepository.findById(id).get(), new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAllByInvoice(Long id) {
        return invoiceProductRepository.findAllByInvoice_Id(id).stream()
                .map(invoiceProduct -> {

                    InvoiceProductDto invoiceProductDto = mapper.convert(invoiceProduct, new InvoiceProductDto());
                    invoiceProductDto.setTotal(calculateTotalWithTax(invoiceProductDto));
                    return invoiceProductDto;

                })
                .collect(Collectors.toList());
    }


    private BigDecimal calculateTotalWithTax(InvoiceProductDto invoiceProduct) {

        BigDecimal totalPrice = invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()));

        return totalPrice.add(BigDecimal.valueOf(invoiceService.calculateTaxByTaxRate(invoiceProduct.getTax(), totalPrice)));

    }

    @Override
    public void addToInvoice(InvoiceProductDto invoiceProduct, Long invoiceId) throws Exception {

        InvoiceDto invoiceDTO = invoiceService.findById(invoiceId);

        if (invoiceDTO.getInvoiceType().equals(InvoiceType.SALES)) {

            if (invoiceProduct.getQuantity() > invoiceProduct.getProduct().getQuantityInStock())
                throw new RuntimeException("Not enough " + invoiceProduct.getProduct().getName() + " quantity to sell...");

        }

//        invoiceDTO.getInvoiceProducts().add(mapper.convert(invoiceProduct, new InvoiceProductDto()));

        invoiceProduct.setInvoice(invoiceDTO);

        invoiceProductRepository.save(mapper.convert(invoiceProduct, new InvoiceProduct()));

    }

    @Override
    public void delete(Long invoiceId, Long invoiceProductId) throws Exception {

        InvoiceProductDto invoiceProductDTO = findById(invoiceProductId);

        invoiceService.findById(invoiceId).getInvoiceProducts().remove(invoiceProductDTO);

        InvoiceProduct invoiceProduct = mapper.convert(invoiceProductDTO, new InvoiceProduct());

        invoiceProduct.setIsDeleted(true);

        invoiceProductRepository.save(invoiceProduct);

    }

    @Override
    public void deleteAllByInvoice(Long invoiceId) { // used, when an invoice is deleted

        findAllByInvoice(invoiceId).forEach(invoiceProductDto -> {

            InvoiceProduct invoiceProduct = mapper.convert(invoiceProductDto, new InvoiceProduct());

            invoiceProduct.setIsDeleted(true);

            invoiceProductRepository.save(invoiceProduct);

        });
    }


    @Override
    public List<InvoiceProductDto> findAllByProductAndInvoiceType(Long id, InvoiceType invoiceType) {
        return invoiceProductRepository.findAllByProductIdAndInvoice_InvoiceTypeOrderByInvoiceInvoiceNo(id, invoiceType).stream().map(invoiceProduct -> mapper.convert(invoiceProduct, new InvoiceProductDto())).collect(Collectors.toList());
    }

    @Override
    public InvoiceProductDto update(InvoiceProductDto invoiceProduct) {
        InvoiceProduct invoiceProduct1 = invoiceProductRepository.save(mapper.convert(invoiceProduct, new InvoiceProduct()));
        return mapper.convert(invoiceProduct1, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAllByInvoiceStatus(InvoiceStatus invoiceStatus) {

        Company company = mapper.convert(securityService.getLoggedInCompany(), new Company());

        return invoiceProductRepository.findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoiceDateDesc(invoiceStatus, company)
                .stream()
                .map(invoiceProduct -> mapper.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());

    }

    @Override
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long id) {
        return invoiceProductRepository.findAllInvoiceProductsByProductId(id).stream()
                .map(invoiceProduct -> mapper.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceProductDto> findAllByCompany() {
        Company company = mapper.convert(securityService.getLoggedInCompany(), new Company());
        return invoiceProductRepository.findAllByProductCategoryCompany(company).stream()
                .map(invoiceProduct -> mapper.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }


    @Override
    public Map<String, BigDecimal> invoiceMonthlyProfitLost() {
        Map<String, BigDecimal> profitMap = new HashMap<>();
        Company company = mapper.convert(securityService.getLoggedInCompany(), new Company());
        invoiceProductRepository.findAllByProductCategoryCompany(company).stream()
                .filter(invoiceProduct -> invoiceProduct.getProfitLoss() != BigDecimal.ZERO)
                .forEach(p -> {
                    BigDecimal profitLoss = p.getProfitLoss();
                    String invoiceTime = p.lastUpdateDateTime.getMonth().toString() + " " + p.lastUpdateDateTime.getYear();
                    profitMap.put(invoiceTime, profitMap.getOrDefault(invoiceTime, BigDecimal.ZERO).add(profitLoss));
                });
        return profitMap;
    }

    public Map<String, BigDecimal> invoiceMonthlyProfitLost1() {
        Map<String, BigDecimal> profitMap = new HashMap<>();
        Company company = mapper.convert(securityService.getLoggedInCompany(), new Company());
        List<InvoiceProduct> invoiceProductList = invoiceProductRepository.findAllByProductCategoryCompany(company).stream()
                .filter(invoiceProduct -> invoiceProduct.getProfitLoss() != BigDecimal.ZERO)
                .collect(Collectors.toList());
        for (InvoiceProduct invoiceProduct : invoiceProductList) {
            BigDecimal profitLoss = invoiceProduct.getProfitLoss();
            String invoiceTime = invoiceProduct.lastUpdateDateTime.getMonth().toString() + " " + invoiceProduct.lastUpdateDateTime.getYear();
            profitMap.put(invoiceTime, profitMap.getOrDefault(invoiceTime, BigDecimal.ZERO).add(profitLoss));
        }
        return profitMap;

    }
}

