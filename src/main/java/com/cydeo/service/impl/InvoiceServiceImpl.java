package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final InvoiceProductRepository invoiceProductRepository;

    private final MapperUtil mapperUtil;

    private final InvoiceProductService invoiceProductService;

    private final ProductService productService;

    private final SecurityService securityService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, @Lazy InvoiceProductService invoiceProductService, ProductService productService, SecurityService securityService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.securityService = securityService;
    }

    @Override
    public InvoiceDto findById(Long id) throws Exception {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new Exception("invoice not found"));

        InvoiceDto invoiceDTO = mapperUtil.convert(invoice, new InvoiceDto());

        if (!invoiceProductService.findAllByInvoice(invoiceDTO.getId()).isEmpty()) {

            invoiceDTO.setTax(totalTaxOfInvoice(invoiceDTO.getId())); // if we do not set these, on the print page, they will be null, because they dont exist in entity
            invoiceDTO.setPrice(totalPriceOfInvoiceWithoutTax(invoiceDTO.getId()));
            invoiceDTO.setTotal(totalPriceOfInvoiceWithTax(invoiceDTO.getId()));
        }

        return invoiceDTO;

    }

    private Company getCurrentCompany() {

        return mapperUtil.convert(securityService.getLoggedInCompany(), new Company());
    }

    @Override
    public List<InvoiceDto> listPurchaseInvoices() {

        return setUpInvoiceList(invoiceRepository.findAllByInvoiceTypeAndCompanyOrderByInvoiceNoDesc(InvoiceType.PURCHASE, getCurrentCompany()));

    }

    @Override
    public List<InvoiceDto> listSalesInvoices() {

        return setUpInvoiceList(invoiceRepository.findAllByInvoiceTypeAndCompanyOrderByInvoiceNoDesc(InvoiceType.SALES, getCurrentCompany()));
    }

    private List<InvoiceDto> setUpInvoiceList(List<Invoice> list) {

        return list.stream().map(invoice -> {

                    InvoiceDto invoiceDTO = mapperUtil.convert(invoice, new InvoiceDto());

                    if (!invoiceProductService.findAllByInvoice(invoiceDTO.getId()).isEmpty()) { // invoiceProductService.findAllByInvoice(invoiceDTO.getId())!=null

                        invoiceDTO.setTax(totalTaxOfInvoice(invoiceDTO.getId()));
                        invoiceDTO.setPrice(totalPriceOfInvoiceWithoutTax(invoiceDTO.getId()));
                        invoiceDTO.setTotal(totalPriceOfInvoiceWithTax(invoiceDTO.getId()));
                    } else {

                        invoiceDTO.setTax(0);
                        invoiceDTO.setPrice(BigDecimal.ZERO);
                        invoiceDTO.setTotal(BigDecimal.ZERO);

                    }

                    return invoiceDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {

        Invoice invoice = invoiceRepository.findById(id).get();

        invoice.setIsDeleted(true);

        invoiceRepository.save(invoice);

        invoiceProductService.deleteAllByInvoice(id);
    }

    @Override
    public void approveSalesInvoice(Long id) {

        approve(id);

        invoiceProductService.findAllByInvoice(id).forEach(invoiceProductDto -> {

            invoiceProductDto.setRemainingQuantity(0);
            invoiceProductDto.setProfitLoss(calculateProfitLoss(invoiceProductDto));

            invoiceProductService.update(invoiceProductDto);
        });
    }

    @Override
    public void approvePurchaseInvoice(Long id) {

        approve(id);

        invoiceProductService.findAllByInvoice(id).forEach(invoiceProductDto -> {

            invoiceProductDto.setRemainingQuantity(invoiceProductDto.getQuantity());
            invoiceProductDto.setProfitLoss(BigDecimal.ZERO);

            invoiceProductService.update(invoiceProductDto);
        });

    }


    private void approve(Long id) {

        Invoice invoice = invoiceRepository.findById(id).get();

        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);

        invoice.setDate(LocalDate.now());

        invoiceRepository.save(invoice);

        invoiceProductService.findAllByInvoice(invoice.getId()).forEach(productService::updateQuantityInStock);

    }


    private BigDecimal calculateProfitLoss(InvoiceProductDto invoiceProduct) {

        List<InvoiceProductDto> listOfInvoiceProductsInPurchaseInvoice =  // holds the invoice products, whose type is purchase, and the product is same as the one we want to sell
                invoiceProductService.findAllByProductAndInvoiceType(invoiceProduct.getProduct().getId(), InvoiceType.PURCHASE)
                        .stream()
                        .filter(invoiceProductDto -> invoiceProductDto.getRemainingQuantity() > 0).collect(Collectors.toList());

        int index = 0;
        int quantity = invoiceProduct.getQuantity();
        int profitLoss = 0;
        boolean done = false;

        while (!done) {

            InvoiceProductDto oldInvoiceProduct = listOfInvoiceProductsInPurchaseInvoice.get(index);

            if (oldInvoiceProduct.getRemainingQuantity() > quantity) {

                oldInvoiceProduct.setRemainingQuantity(oldInvoiceProduct.getRemainingQuantity() - quantity);

                profitLoss += quantity * invoiceProduct.getPrice().intValue() - oldInvoiceProduct.getPrice().intValue();

                done = true;

            } else {

                quantity -= oldInvoiceProduct.getRemainingQuantity();

                profitLoss += oldInvoiceProduct.getRemainingQuantity() * invoiceProduct.getPrice().intValue() - oldInvoiceProduct.getPrice().intValue();

                oldInvoiceProduct.setRemainingQuantity(0);

            }

            index++;
        }

        return BigDecimal.valueOf(profitLoss);

    }

    @Override
    public InvoiceDto create(InvoiceDto invoice, InvoiceType invoiceType) {

        invoice.setCompany(securityService.getLoggedInCompany());

        invoice.setInvoiceType(invoiceType);

        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);

        return mapperUtil.convert(invoiceRepository.save(mapperUtil.convert(invoice, new Invoice())), new InvoiceDto());
    }

    public List<InvoiceDto> listAllInvoices(InvoiceType invoiceType) { // includes the deleted invoices as well, needed for generating the invoice no

        return invoiceRepository.findAllIncludeDeleted(invoiceType.toString(), getCurrentCompany().getId()).stream().map(invoice -> mapperUtil.convert(invoice, new InvoiceDto())).collect(Collectors.toList());

    }

    @Override
    public String generateInvoiceNo(InvoiceType invoiceType) {

        if (invoiceType.equals(InvoiceType.PURCHASE)) {

            String lastInvoiceNo = listAllInvoices(InvoiceType.PURCHASE).stream().findFirst().orElse(new InvoiceDto()).getInvoiceNo();

            int no = lastInvoiceNo == null ? 1 : Integer.parseInt(lastInvoiceNo.substring(2)) + 1;

            return no < 10 ? "P-00" + no : no > 99 ? "P-" + no : "P-0" + no;

        }

        String lastInvoiceNo = listAllInvoices(InvoiceType.SALES).stream().findFirst().orElse(new InvoiceDto()).getInvoiceNo();

        int no = lastInvoiceNo == null ? 1 : Integer.parseInt(lastInvoiceNo.substring(2)) + 1;

        return no < 10 ? "S-00" + no : no > 99 ? "S-" + no : "S-0" + no;
    }

    private Integer totalTaxOfInvoice(Long invoiceId) {

        return totalPriceOfInvoiceWithTax(invoiceId).intValue() - totalPriceOfInvoiceWithoutTax(invoiceId).intValue();

    }

    @Override
    public Integer calculateTaxByTaxRate(Integer taxRate, BigDecimal price) {

        return price.multiply(BigDecimal.valueOf(taxRate).divide(BigDecimal.valueOf(100))).intValue();

    }

    @Override
    public InvoiceDto update(InvoiceDto invoice) {

        Invoice notUpdatedInvoice = invoiceRepository.findById(invoice.getId()).get();

        Invoice updatedInvoice = mapperUtil.convert(invoice, new Invoice());

        updatedInvoice.setInvoiceStatus(notUpdatedInvoice.getInvoiceStatus()); // could also directly set to 'awaiting_approval' since the approved ones are not editable
        updatedInvoice.setCompany(getCurrentCompany());
        updatedInvoice.setInvoiceType(notUpdatedInvoice.getInvoiceType());

        return mapperUtil.convert(invoiceRepository.save(updatedInvoice), new InvoiceDto());

    }

    @Override
    public List<InvoiceDto> findAllInvoiceByClientVendorId(Long id) {

        return invoiceRepository.findInvoiceByClientVendor_Id(id)
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());

    }

    @Override
    public List<InvoiceDto> listLastThreeApprovedInvoices() {

        List<Invoice> invoices = invoiceRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Invoice::getDate).reversed())
                .filter(invoice -> invoice.getInvoiceStatus() == InvoiceStatus.APPROVED)
                .limit(3)
                .collect(Collectors.toList());

        return setUpInvoiceList(invoices);
    }

    @Override
    public Map<String, BigDecimal> invoiceMonthlyProfitLost() {

//        Map<String, BigDecimal> profitMap = new HashMap<>();
//
//        //List<InvoiceProduct> invoiceProductList =
//        invoiceProductRepository.find
//                invoiceProductService.findAllByCompany().stream()
//                .map(invoiceProductDto -> mapperUtil.convert(invoiceProductDto,new InvoiceProduct()))
//                .filter(invoiceProduct -> invoiceProduct.getProfitLoss() != BigDecimal.ZERO)
//                //.collect(Collectors.toList());
//                .forEach(invoiceProduct -> {
//                    BigDecimal profitLoss = invoiceProduct.getProfitLoss();
//                    String invoiceTime = invoiceProduct.lastUpdateDateTime.getMonth().toString() + " " + invoiceProduct.lastUpdateDateTime.getYear();
//                    profitMap.put(invoiceTime, profitMap.getOrDefault(invoiceTime, BigDecimal.ZERO).add(profitLoss));
//                });
//        return profitMap;

//        for (InvoiceProduct invoiceProduct : invoiceProductList) {
//            BigDecimal profitLoss = invoiceProduct.getProfitLoss();
//            String invoiceTime = invoiceProduct.lastUpdateDateTime.getMonth().toString() + " " + invoiceProduct.lastUpdateDateTime.getYear();
//            profitMap.put(invoiceTime, profitMap.getOrDefault(invoiceTime, BigDecimal.ZERO).add(profitLoss));
//        }
//        return profitMap;

        return invoiceProductService.invoiceMonthlyProfitLost();

    }



    private BigDecimal totalPriceOfInvoiceWithoutTax(Long invoiceId) {

        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findAllByInvoice(invoiceId);

        return invoiceProducts
                .stream()
                .map(invoiceProduct -> invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal::add).get();

    }

    private BigDecimal totalPriceOfInvoiceWithTax(Long invoiceId) {

        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findAllByInvoice(invoiceId);

        return invoiceProducts
                .stream()
                .map(InvoiceProductDto::getTotal)
                .reduce(BigDecimal::add).get();
    }

    //-------
    private BigDecimal totalInvoicesProfitLoss(Long invoiceId) {
        return invoiceProductService.findAllByInvoice(invoiceId)
                .stream()
                .map(p -> p.getProfitLoss())
                .reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));

    }

}


