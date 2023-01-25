package com.cydeo.controller;

import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;

    public ReportingController(ReportingService reportingService, InvoiceService invoiceService, InvoiceProductService invoiceProductService) {
        this.reportingService = reportingService;
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping("/stockData")
    public String getStockReport(Model model){

        model.addAttribute("invoiceProducts", reportingService.getStockReport());

        return "report/stock-report";
    }
    @GetMapping("/profitLossData")
    public String profLos(Model model){


        model.addAttribute("monthlyProfitLossDataMap", invoiceProductService.invoiceMonthlyProfitLost());

                return "report/profit-loss-report";
    }

}
