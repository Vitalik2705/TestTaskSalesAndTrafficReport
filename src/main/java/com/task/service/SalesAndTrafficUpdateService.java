package com.task.service;

import com.task.embedded.SalesAndTrafficByDate;
import com.task.model.SalesAndTrafficReport;
import com.task.repository.SalesAndTrafficReportRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesAndTrafficUpdateService {

    private final SalesAndTrafficReportRepository repository;

    @Autowired
    public SalesAndTrafficUpdateService(SalesAndTrafficReportRepository repository) {
        this.repository = repository;
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
}

