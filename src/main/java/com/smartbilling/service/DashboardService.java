package com.smartbilling.service;

import com.smartbilling.dao.InvoiceDAO;
import com.smartbilling.dao.ProductDAO;
import com.smartbilling.model.DashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for dashboard aggregate data.
 * Collects statistics from product and invoice DAOs.
 */
@Service
public class DashboardService {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private InvoiceDAO invoiceDAO;

    /**
     * Build the complete dashboard statistics.
     *
     * @return DashboardStats populated with all aggregate data
     */
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalProducts(productDAO.getTotalCount());
        stats.setTotalSales(invoiceDAO.getTotalSales());
        stats.setTotalInvoices(invoiceDAO.getTotalCount());
        stats.setLowStockCount(productDAO.getLowStockCount());
        stats.setRecentInvoices(invoiceDAO.findRecent(5));
        stats.setLowStockProducts(productDAO.findLowStockProducts());
        return stats;
    }
}
