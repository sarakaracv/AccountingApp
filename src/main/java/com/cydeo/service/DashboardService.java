package com.cydeo.service;

import java.math.BigDecimal;

public interface DashboardService {

    BigDecimal totalCostOfAllApprovedPurchaseInvoiceWithTax();

    BigDecimal totalSalesWithTax();

    BigDecimal totalProfitLoss();
}
