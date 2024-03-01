package com.task.controller;
import com.task.embedded.SalesAndTrafficByAsin;
import com.task.embedded.SalesAndTrafficByDate;
import com.task.model.SalesAndTrafficReport;
import com.task.service.SalesAndTrafficReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salesAndTrafficReports")
public class SalesAndTrafficReportController {

    private final SalesAndTrafficReportService salesAndTrafficReportService;

    @Autowired
    public SalesAndTrafficReportController(SalesAndTrafficReportService salesAndTrafficReportService) {
        this.salesAndTrafficReportService = salesAndTrafficReportService;
    }

    @GetMapping("/byDate")
    public ResponseEntity<?> getSalesAndTrafficReportByDate() {
        List<SalesAndTrafficByDate> result = salesAndTrafficReportService.getAllSalesAndTrafficByDate();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/byAsin")
    public ResponseEntity<?> getSalesAndTrafficReportByAsin() {
        List<SalesAndTrafficByAsin> result = salesAndTrafficReportService.getAllSalesAndTrafficByAsin();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/byConcreteDate/{date}")
    public ResponseEntity<?> getSalesAndTrafficReportByDate(@PathVariable String date) {
        SalesAndTrafficByDate result = salesAndTrafficReportService.getSalesAndTrafficByDate(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/byConcreteAsin/{asin}")
    public ResponseEntity<?> getSalesAndTrafficReportByAsin(@PathVariable String asin) {
        SalesAndTrafficByAsin result = salesAndTrafficReportService.getSalesAndTrafficByAsin(asin);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/byConcreteDates/{dates}")
    public ResponseEntity<?> getSalesAndTrafficReportByDate(@PathVariable List<String> dates) {
        List<SalesAndTrafficByDate> result = salesAndTrafficReportService.getSalesAndTrafficByDates(dates);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/byConcreteAsins/{asins}")
    public ResponseEntity<?> getSalesAndTrafficReportByAsin(@PathVariable List<String> asins) {
        List<SalesAndTrafficByAsin> result = salesAndTrafficReportService.getSalesAndTrafficByAsins(asins);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{reportId}/addSalesTrafficByDate")
    public ResponseEntity<String> addSalesAndTrafficByDate(
            @PathVariable String reportId,
            @RequestBody SalesAndTrafficByDate newEntry
    ) {
        try {
            salesAndTrafficReportService.addSalesAndTrafficByDate(reportId, newEntry);
            return new ResponseEntity<>("Sales and traffic entry added successfully", HttpStatus.OK);
        } catch (ChangeSetPersister.NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add sales and traffic entry", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

