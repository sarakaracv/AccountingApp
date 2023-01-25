package com.cydeo.controller;

import com.cydeo.client.CurrencyClient;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class DashboardController {

    private final InvoiceService invoiceService;
    private final CurrencyClient currencyClient;
    private final DashboardService dashboardService;

    public DashboardController(InvoiceService invoiceService, CurrencyClient currencyClient, DashboardService dashboardService) {
        this.invoiceService = invoiceService;
        this.currencyClient = currencyClient;
        this.dashboardService = dashboardService;
    }

    // this method has only dummy info and should be modified in accordance with user stories.
    @GetMapping("/dashboard")
    public String dashboard(Model model){
        Map<String, BigDecimal> summaryNumbers = Map.of(
                "totalCost", dashboardService.totalCostOfAllApprovedPurchaseInvoiceWithTax(),
                "totalSales", dashboardService.totalSalesWithTax(),
                "profitLoss", dashboardService.totalProfitLoss()
        );



        model.addAttribute("summaryNumbers", summaryNumbers);
        model.addAttribute("invoices", invoiceService.listLastThreeApprovedInvoices());
        model.addAttribute("exchangeRates", currencyClient.getExchangeRates());
        model.addAttribute("title", "Cydeo Accounting-Dashboard");
        return "dashboard";
    }


}

