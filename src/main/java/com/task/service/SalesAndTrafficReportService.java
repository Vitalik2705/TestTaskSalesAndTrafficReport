package com.task.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.embedded.*;
import com.task.model.SalesAndTrafficReport;
import com.task.repository.SalesAndTrafficReportRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalesAndTrafficReportService {

    private final SalesAndTrafficReportRepository repository;

    @Autowired
    public SalesAndTrafficReportService(SalesAndTrafficReportRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "getAllSalesAndTrafficByDateCache")
    public List<SalesAndTrafficByDate> getAllSalesAndTrafficByDate() {
        List<SalesAndTrafficReport> reports = repository.findAll();
        return reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByDate)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "getAllSalesAndTrafficByAsinCache")
    public List<SalesAndTrafficByAsin> getAllSalesAndTrafficByAsin() {
        List<SalesAndTrafficReport> reports = repository.findAll();
        return reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByAsin)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "getSalesAndTrafficByDateCache", key = "#targetDate")
    public SalesAndTrafficByDate getSalesAndTrafficByDate(String targetDate) {
        List<SalesAndTrafficReport> reports = repository.findAll();

        Optional<SalesAndTrafficByDate> result = reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByDate)
                .flatMap(List::stream)
                .filter(entry -> entry.getDate().equals(targetDate))
                .findFirst();

        return result.orElse(null);
    }

    @Cacheable(value = "getSalesAndTrafficByAsinCache", key = "#asin")
    public SalesAndTrafficByAsin getSalesAndTrafficByAsin(String asin) {
        List<SalesAndTrafficReport> reports = repository.findAll();

        Optional<SalesAndTrafficByAsin> result = reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByAsin)
                .flatMap(List::stream)
                .filter(entry -> entry.getParentAsin().equals(asin))
                .findFirst();

        return result.orElse(null);
    }

    @Cacheable(value = "getSalesAndTrafficByDatesCache", key = "#targetDates")
    public List<SalesAndTrafficByDate> getSalesAndTrafficByDates(List<String> targetDates) {
        List<SalesAndTrafficReport> reports = repository.findAll();

        List<SalesAndTrafficByDate> resultList = reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByDate)
                .flatMap(List::stream)
                .filter(entry -> targetDates.contains(entry.getDate()))
                .collect(Collectors.toList());

        return resultList.isEmpty() ? null : resultList;
    }

    @Cacheable(value = "getSalesAndTrafficByAsinsCache", key = "#asins")
    public List<SalesAndTrafficByAsin> getSalesAndTrafficByAsins(List<String> asins) {
        List<SalesAndTrafficReport> reports = repository.findAll();

        List<SalesAndTrafficByAsin> resultList = reports.stream()
                .map(SalesAndTrafficReport::getSalesAndTrafficByAsin)
                .flatMap(List::stream)
                .filter(entry -> asins.contains(entry.getParentAsin()))
                .collect(Collectors.toList());

        return resultList.isEmpty() ? null : resultList;
    }

    @CacheEvict(value = {"getAllSalesAndTrafficByDateCache", "getSalesAndTrafficByDateCache", "getSalesAndTrafficByDatesCache"}, key = "#reportId")
    public void addSalesAndTrafficByDate(String reportId, SalesAndTrafficByDate newEntry) throws ChangeSetPersister.NotFoundException {
        Optional<SalesAndTrafficReport> optionalReport = repository.findById(reportId);

        if (optionalReport.isPresent()) {
            SalesAndTrafficReport existingReport = optionalReport.get();
            List<SalesAndTrafficByDate> salesAndTrafficByDate = existingReport.getSalesAndTrafficByDate();
            salesAndTrafficByDate.add(newEntry);
            existingReport.setSalesAndTrafficByDate(salesAndTrafficByDate);

            existingReport.setId(new ObjectId(reportId));

            repository.save(existingReport);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    @Scheduled(fixedRate = 300000)
    public void addSalesAndTrafficPeriodically() {
        SalesAndTrafficByDate newEntry = createNewSalesByDate();

        String reportId = "65e0bca30ea3a62f9f859bca";
        try {
            addSalesAndTrafficByDate(reportId, newEntry);
        } catch (ChangeSetPersister.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private SalesAndTrafficByDate createNewSalesByDate() {
        SalesAndTrafficByDate newSalesByDate = new SalesAndTrafficByDate();
        LocalDate currentDate = LocalDate.parse("2024-02-26", DateTimeFormatter.ISO_DATE);
        LocalDate newDate = currentDate.plusDays(1);
        newSalesByDate.setDate(newDate.toString());

        newSalesByDate.setSalesByDate(new SalesByDate());

        newSalesByDate.getSalesByDate().setOrderedProductSales(createNewOrderedProductSales());
        newSalesByDate.getSalesByDate().setOrderedProductSalesB2B(createNewOrderedProductSalesB2B());
        newSalesByDate.getSalesByDate().setUnitsOrdered(600);
        newSalesByDate.getSalesByDate().setUnitsOrderedB2B(12);
        newSalesByDate.getSalesByDate().setTotalOrderItems(560);
        newSalesByDate.getSalesByDate().setTotalOrderItemsB2B(10);
        newSalesByDate.getSalesByDate().setAverageSalesPerOrderItem(createNewAverageSalesPerOrderItem());
        newSalesByDate.getSalesByDate().setAverageSalesPerOrderItemB2B(createNewAverageSalesPerOrderItem());
        newSalesByDate.getSalesByDate().setAverageUnitsPerOrderItem(1.07);
        newSalesByDate.getSalesByDate().setAverageUnitsPerOrderItemB2B(1.2);
        newSalesByDate.getSalesByDate().setAverageSellingPrice(createNewAverageSellingPrice());
        newSalesByDate.getSalesByDate().setAverageSellingPriceB2B(createNewAverageSellingPrice());
        newSalesByDate.getSalesByDate().setUnitsRefunded(16);
        newSalesByDate.getSalesByDate().setRefundRate(2.67);
        newSalesByDate.getSalesByDate().setClaimsGranted(1);
        newSalesByDate.getSalesByDate().setClaimsAmount(createNewClaimsAmount());
        newSalesByDate.getSalesByDate().setShippedProductSales(createNewShippedProductSales());
        newSalesByDate.getSalesByDate().setUnitsShipped(600);
        newSalesByDate.getSalesByDate().setOrdersShipped(590);

        newSalesByDate.setTrafficByDate(new TrafficByDate());

        newSalesByDate.getTrafficByDate().setBrowserPageViews(2000);
        newSalesByDate.getTrafficByDate().setBrowserPageViewsB2B(80);
        newSalesByDate.getTrafficByDate().setMobileAppPageViews(4000);
        newSalesByDate.getTrafficByDate().setMobileAppPageViewsB2B(90);
        newSalesByDate.getTrafficByDate().setPageViews(6000);
        newSalesByDate.getTrafficByDate().setPageViewsB2B(170);
        newSalesByDate.getTrafficByDate().setBrowserSessions(1200);
        newSalesByDate.getTrafficByDate().setBrowserSessionsB2B(50);
        newSalesByDate.getTrafficByDate().setMobileAppSessions(1700);
        newSalesByDate.getTrafficByDate().setMobileAppSessionsB2B(35);
        newSalesByDate.getTrafficByDate().setSessions(3000);
        newSalesByDate.getTrafficByDate().setSessionsB2B(85);
        newSalesByDate.getTrafficByDate().setBuyBoxPercentage(98.5);
        newSalesByDate.getTrafficByDate().setBuyBoxPercentageB2B(94.0);
        newSalesByDate.getTrafficByDate().setOrderItemSessionPercentage(18.0);
        newSalesByDate.getTrafficByDate().setOrderItemSessionPercentageB2B(11.0);
        newSalesByDate.getTrafficByDate().setUnitSessionPercentage(19.0);
        newSalesByDate.getTrafficByDate().setUnitSessionPercentageB2B(13.0);
        newSalesByDate.getTrafficByDate().setAverageOfferCount(240);
        newSalesByDate.getTrafficByDate().setAverageParentItems(135);
        newSalesByDate.getTrafficByDate().setFeedbackReceived(2);
        newSalesByDate.getTrafficByDate().setNegativeFeedbackReceived(0);
        newSalesByDate.getTrafficByDate().setReceivedNegativeFeedbackRate(0);

        return newSalesByDate;
    }

    private OrderedProductSales createNewOrderedProductSales() {
        OrderedProductSales orderedProductSales = new OrderedProductSales();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }

    private OrderedProductSalesB2B createNewOrderedProductSalesB2B() {
        OrderedProductSalesB2B orderedProductSales = new OrderedProductSalesB2B();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }

    private AverageSalesPerOrderItem createNewAverageSalesPerOrderItem() {
        AverageSalesPerOrderItem orderedProductSales = new AverageSalesPerOrderItem();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }

    private AverageSellingPrice createNewAverageSellingPrice() {
        AverageSellingPrice orderedProductSales = new AverageSellingPrice();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }

    private ClaimsAmount createNewClaimsAmount() {
        ClaimsAmount orderedProductSales = new ClaimsAmount();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }

    private ShippedProductSales createNewShippedProductSales() {
        ShippedProductSales orderedProductSales = new ShippedProductSales();
        orderedProductSales.setAmount(10614.69);
        orderedProductSales.setCurrencyCode("USD");
        return orderedProductSales;
    }
}
